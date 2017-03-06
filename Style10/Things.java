import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * In this style; we have to implement the complete program by abstracting
 * relevant portions of the program into objects.
 * 
 * @author Madhur J. Bajaj
 *
 */
public class Things {

	/**
	 * Abstract Class, to be used as a word manager. This class will read a
	 * file, and then generates a list of words from the file. The generation of
	 * this list, needs to be implemented by the child class that extends this
	 * class.
	 * 
	 * @author Madhur J. Bajaj
	 *
	 */
	private static abstract class WordManager {
		protected ArrayList<String> mWordList;
		private Scanner mScanner;

		/* Abstract function that needs to be implemented by the child class, to
		 * generate a list of words using the file content */
		protected abstract void generateList(String content);

		WordManager(String path) throws Exception {
			mScanner = new Scanner(new FileReader(path));
			mScanner.useDelimiter("\\Z");
			generateList(mScanner.next());
			mScanner.close();
		}
	}

	/**
	 * Child Class, in this Class, every word is actually a potential token
	 * 
	 * @author Madhur J. Bajaj
	 *
	 */
	private static class TokenManager extends WordManager {

		private int mCounter = 0;

		TokenManager(String path) throws Exception {
			super(path);
		}

		@Override
		protected void generateList(String content) {
			mWordList = new ArrayList<String>(
					Arrays.asList(content.replaceAll("[\\W_]", " ").toLowerCase().split(" ")));
		}

		/* This function will throw the next token to the caller */
		public String getNextToken() {
			if (mCounter < mWordList.size())
				return mWordList.get(mCounter++).trim();
			return null;
		}
	}

	/**
	 * Child Class, in this class, every word is a stop word
	 * 
	 * @author Madhur J. Bajaj
	 *
	 */
	private static class StopWordsManager extends WordManager {

		StopWordsManager(String path) throws Exception {
			super(path);
		}

		@Override
		protected void generateList(String content) {
			mWordList = new ArrayList<String>(Arrays.asList(content.split(",")));
		}

		/* This function will tell whether the given word is a stop word or not */
		public boolean isStopWord(String word) {
			return (word.length() < 2 || mWordList.contains(word));
		}
	}

	/**
	 * Class to manage the frequency of the tokens
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

		/*
		 * Function to print the sorted map, in descending order, upto maximum
		 * of @limit values
		 */
		public void printReverseSorted(int limit) {
			mMap.entrySet().stream().sorted((x, y) -> y.getValue().compareTo(x.getValue())).limit(limit)
					.forEach(p -> System.out.println(p.getKey() + " - " + p.getValue()));
		}
	}

	public static void main(String args[]) throws Exception {
		TokenManager mTokenManager = new TokenManager(args[0]);
		StopWordsManager mStopWordsManager = new StopWordsManager("..\\stop_words.txt");
		FrequencyManager mFrequencyManager = new FrequencyManager();

		String token = mTokenManager.getNextToken();
		while (token != null) {
			if (!mStopWordsManager.isStopWord(token))
				mFrequencyManager.handleToken(token);

			token = mTokenManager.getNextToken();
		}

		mFrequencyManager.printReverseSorted(25);
	}
}
