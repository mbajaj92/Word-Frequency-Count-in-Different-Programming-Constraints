import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This is the concurrent implementation of LetterBox Style, in this
 * implementation, I have created multiple threads with each thread handling one
 * Manager, and passed Messages among these threads to make use of the LetterBox
 * concept.
 * 
 * @author Madhur J. Bajaj
 *
 */
public class LetterBox {
	/**
	 * This is Message Class, any communication that needs to happen among the
	 * managers, will create an object of this class and fill in the details in
	 * this.
	 * 
	 * @author Madhur J. Bajaj
	 *
	 */
	private static class Message {
		public String what;
		public Object bundle;
		LetterBox mCallback;

		Message(String id, Object b, LetterBox s11) {
			what = id;
			bundle = b;
			mCallback = s11;
		}
	}

	/**
	 * Abstract Class, to be used as word manager. This class will read a file,
	 * and then generates a list of words from the file. The generation of this
	 * list, needs to be implemented by the child class that extends this class.
	 * This class also supports Message Handling which again needs to be
	 * partially implemented by its child class.
	 * 
	 * @author Madhur J. Bajaj
	 *
	 */
	private abstract class WordManager {
		/* List of words */
		protected ArrayList<String> mWordList;
		/* Scanner to read the file */
		private Scanner mScanner;
		/* A queue to accept any incoming message */
		private ArrayList<Message> mMessageQueue;
		/* Used as a mutex to edit the mMessageQueue */
		private Object mutex = null;
		/* Used to indicate to the thread to start termination process */
		protected boolean terminate = false;

		/* Thread, to read messages from the Queue and handle them accordingly */
		private class QueueObserverThread extends Thread {

			@Override
			public void run() {
				System.out.println("Queue Handler initiated " + Thread.currentThread().getId());
				while (true) {
					/* Acquire the lock */
					synchronized (mutex) {
						while (mMessageQueue.isEmpty()) {
							if (terminate) {
								System.out.println("Queue Handler terminated " + Thread.currentThread().getId());
								return;
							}

							try {
								mutex.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					
					Message msg = null;
					/* Acquire the lock */
					synchronized (mutex) {
						/* Read next message from the Queue */
						if(!mMessageQueue.isEmpty())
							msg = mMessageQueue.remove(0);
						
						/* Release the lock */
						mutex.notifyAll();
					}

					/* Handle the message */
					if (msg != null) {
						Message returnMsg = handleMessageImpl(msg);
						if (returnMsg != null)
							msg.mCallback.handleMessage(returnMsg);
					}					
				}
			}
		}

		/* Abstract function that needs to be implemented by the child class, to
		 * generate a list of words using the file content */
		protected abstract void generateList(String content);

		public void handleMessage(Message msg) {
			/* Acquire the lock */
			synchronized (mutex) {
				mMessageQueue.add(msg);
				/* Release the lock */
				mutex.notifyAll();
			}
		}

		protected abstract Message handleMessageImpl(Message msg);

		WordManager(String path) throws Exception {
			mutex = new Object();
			mMessageQueue = new ArrayList<Message>();
			mScanner = new Scanner(new FileReader(path));
			mScanner.useDelimiter("\\Z");
			generateList(mScanner.next());
			mScanner.close();

			/* Start the Queue Observer Thread */
			QueueObserverThread mQueueObserver = new QueueObserverThread();
			mQueueObserver.start();
		}
	}

	/**
	 * Child Class, in this Class, every word is actually a potential token
	 * 
	 * @author Madhur J. Bajaj
	 *
	 */
	private class TokenManager extends WordManager {
		public static final String NEXT_TOKEN = "GET_NEXT_TOKEN";
		public static final String LAST_MESSAGE = "LAST_MSG_TKM";
		private int mCounter = 0;

		TokenManager(String path) throws Exception {
			super(path);
		}

		public int getSize() {
			return mWordList.size();
		}

		@Override
		protected void generateList(String content) {
			mWordList = new ArrayList<String>(
					Arrays.asList(content.replaceAll("[^A-Za-z0-9]", " ").toLowerCase().split(" ")));
		}

		private boolean hasNextToken() {
			return (mCounter < mWordList.size());
		}

		/* This function will throw the next token to the caller */
		private String getNextToken() {
			if (hasNextToken())
				return mWordList.get(mCounter++).trim();
			return null;
		}

		@Override
		protected Message handleMessageImpl(Message msg) {
			Message returnMessage = null;
			if (msg.what == NEXT_TOKEN)
				returnMessage = new Message(msg.what, getNextToken(), null);
			else if (msg.what == LAST_MESSAGE) {
				/* This means, that this class will not receive any more message
				 * from the caller, so it is safe to end the Observer Thread
				 * after this message is processed completely */
				returnMessage = new Message(TERMINATION_INITIATED_TKM, null, null);
				terminate = true;
			}
			return returnMessage;
		}
	}

	/**
	 * Child Class, in this class, every word is a stop word
	 * 
	 * @author Madhur J. Bajaj
	 *
	 */
	private class StopWordsManager extends WordManager {
		public static final String IS_VALID_TOKEN = "IS_VALID_TOKEN";
		public static final String LAST_MESSAGE = "LAST_MSG_SWM";

		StopWordsManager(String path) throws Exception {
			super(path);
		}

		@Override
		protected void generateList(String content) {
			mWordList = new ArrayList<String>(Arrays.asList(content.split(",")));
		}

		/* This function will tell whether the given word is a stop word or not */
		private boolean isStopWord(String word) {
			return (word.length() < 2 || mWordList.contains(word));
		}

		@Override
		protected Message handleMessageImpl(Message msg) {
			Message returnMessage = null;
			if (msg.what == IS_VALID_TOKEN) {
				returnMessage = new Message(isStopWord((String) msg.bundle) ? INVALID_TOKEN : VALID_TOKEN, msg.bundle,
						null);
			} else if (msg.what == LAST_MESSAGE) {
				/* This means, that this class will not receive any more message
				 * from the caller, so it is safe to end the Observer Thread
				 * after this message is processed completely */
				returnMessage = new Message(TERMINATION_INITIATED_SWM, null, null);
				terminate = true;
			}
			return returnMessage;
		}
	}

	/**
	 * Class to Manage the frequency of the tokens
	 * 
	 * @author Madhur J. Bajaj
	 *
	 */
	private static class FrequencyManager {
		private HashMap<String, Integer> mMap;

		public FrequencyManager() {
			mMap = new HashMap<String, Integer>();
		}

		/* Function to handle a valid token */
		public void handleToken(String token) {
			if (mMap.containsKey(token))
				mMap.put(token, mMap.get(token) + 1);
			else
				mMap.put(token, 1);
		}

		/* Function to print the sorted map, in descending order, upto maximum
		 * of @limit values */
		public void printReverseSorted(int limit) {
			mMap.entrySet().stream().sorted((x, y) -> y.getValue().compareTo(x.getValue())).limit(limit)
					.forEach(p -> System.out.println(p.getKey() + " - " + p.getValue()));
		}
	}

	int counter = 0;

	/* Implementation of Handle Message */
	private void actuallyHandleMessage(Message msg) {
		switch (msg.what) {
		case TokenManager.NEXT_TOKEN:
			/* The incoming message contains the next token */
			Message newMsg = new Message(StopWordsManager.IS_VALID_TOKEN, msg.bundle, this);
			/* Send Message to Stop Words Manager to check if the token is a
			 * valid token or not */
			mStopWordsManager.handleMessage(newMsg);
			break;
		case VALID_TOKEN:
			/* The incoming message contains a valid token, send message to the
			 * frequency manager to handle this valid token */
			mFrequencyManager.handleToken((String) msg.bundle);
		case INVALID_TOKEN:
			/* The incoming message contains an invalid token, do nothing in
			 * this case */
			break;
		case TERMINATION_INITIATED_TKM:
			/* Token Manager has sent VALIDITY requests for all the tokens, and
			 * now there are no more pending requests, it has initiated
			 * termination sequence. So we are sending LAST MESSAGE to Stop Words Manager */
			System.out.println("Sending LAST MESSAGE to STOP_WORDS_MANAGER");
			Message abc = new Message(StopWordsManager.LAST_MESSAGE, null, this);
			mStopWordsManager.handleMessage(abc);
			break;
		case TERMINATION_INITIATED_SWM:
			/* Stop Words Manager has initiated termination sequence, it has
			 * processed the validity of all the tokens time to stop our thread,
			 * and print the top 25 tokens */
			System.out.println("Termination Seq initiated");
			initiateTerminationSequence = true;
			break;
		}
	}

	public void handleMessage(Message msg) {
		/* Acquiring the lock */
		synchronized (mutex) {
			/* Adding the message */
			mQueue.add(msg);
			/* Releasing the lock */
			mutex.notifyAll();
		}
	}

	public static final String TERMINATION_INITIATED_TKM = "TERMINATION_INITIATED_TKM";
	public static final String TERMINATION_INITIATED_SWM = "TERMINATION_INITIATED_SWM";
	public static final String VALID_TOKEN = "VALID_TOKEN";
	public static final String INVALID_TOKEN = "INVALID_TOKEN";
	/* Boolean used to denote to start the termination sequence */
	private boolean initiateTerminationSequence = false;
	private TokenManager mTokenManager;
	private StopWordsManager mStopWordsManager;
	private FrequencyManager mFrequencyManager;
	/* Queue used to hold the messages */
	private ArrayList<Message> mQueue = null;
	/* Object to be used as mutex will editing the mQueue */
	private Object mutex = null;

	/* Queue Handler Thread, to observe the message queue */
	private class QueueHandler extends Thread {

		@Override
		public void run() {
			System.out.println("Queue Handler initiated " + Thread.currentThread().getId());
			while (true) {
				synchronized (mutex) {
					while (mQueue.isEmpty()) {
						if (initiateTerminationSequence) {
							mFrequencyManager.printReverseSorted(25);
							System.out.println("Queue Handler terminated " + Thread.currentThread().getId());
							return;
						}

						try {
							mutex.wait();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}

				Message msg = null;
				synchronized (mutex) {
					if (!mQueue.isEmpty())
						msg = mQueue.remove(0);
					mutex.notifyAll();
				}

				if (msg != null)
					actuallyHandleMessage(msg);
			}
		}
	}

	LetterBox(String path) throws Exception {
		mutex = new Object();
		mQueue = new ArrayList<Message>();
		mTokenManager = new TokenManager(path);
		mStopWordsManager = new StopWordsManager("..\\stop_words.txt");
		mFrequencyManager = new FrequencyManager();
		QueueHandler mHandler = new QueueHandler();
		mHandler.start();

		int size = mTokenManager.getSize();
		for (int i = 0; i < size; i++) {
			/* Send message to the Token Manager to send Next Token */
			Message msg = new Message(TokenManager.NEXT_TOKEN, null, this);
			mTokenManager.handleMessage(msg);
		}

		/* We have now requested for all the possible tokens, sending LAST
		 * MESSAGE to Token Manager, indicating that there are no more requests,
		 * after this message the Token Manager can initiate termination
		 * sequence */
		System.out.println("Sending LAST MESSAGE to TOKEN_MANAGER");
		Message msg = new Message(TokenManager.LAST_MESSAGE, null, this);
		mTokenManager.handleMessage(msg);
	}

	public static void main(String args[]) throws Exception {
		new LetterBox(args[0]);
	}
}