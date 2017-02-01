import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;
/*
 * In this programming style all the computations need to happen on stack only. 
 * And we now have access to a heap via string identifiers only 
 */

/**
 * 
 * @author Madhur J. Bajaj
 *
 */
public class GoForth {
	static Stack<Object> stack;
	static HashMap<String, Object> heap;

	/**
	 * This function will read the 2 files and store them in Stack.
	 * 
	 * @throws IOException
	 */
	public static void read_file() throws IOException {
		heap.put("scanner", new Scanner(new FileReader((String) stack.pop())));
		((Scanner) heap.get("scanner")).useDelimiter("\\Z");
		stack.push(((Scanner) heap.get("scanner")).next());
		((Scanner) heap.get("scanner")).close();
		heap.put("scanner", new Scanner(new FileReader("..\\stop_words.txt")));
		stack.push("," + ((Scanner) heap.get("scanner")).next());
		heap.remove("scanner");
	}

	public static void checkLoopCondition() {
		stack.push((int) stack.pop() < (int) stack.pop());
	}

	public static void addIntItems() {
		stack.push((int) stack.pop() + (int) stack.pop());
	}

	public static void addStringItems() {
		stack.push((String) stack.pop() + (String) stack.pop());
	}

	@SuppressWarnings("unchecked")
	public static void generateTokens() {
		heap.put("stop_words", (String) stack.pop());
		heap.put("inputfile", (String) stack.pop());
		heap.put("token_map", new HashMap<String, Integer>());
		heap.put("token", "");
		heap.put("loopcounter", 0);

		while (true) {
			stack.push(((String) heap.get("inputfile")).length());
			stack.push(heap.get("loopcounter"));
			/* Loop counter should not exceed the file length */
			checkLoopCondition();
			if (!(boolean) stack.pop())
				break;

			heap.put("ch", ((String) heap.get("inputfile")).charAt((int) heap.get("loopcounter")));
			if (Character.isLetterOrDigit((char) heap.get("ch"))) {
				stack.push("" + Character.toLowerCase((char) heap.get("ch")));
				stack.push(heap.get("token"));
				/* Add the current character to the token */
				addStringItems();
				heap.put("token", stack.pop());
			} else {
				/* Token generated */
				stack.push(1);
				stack.push(((String) heap.get("token")).length());
				/* Checking for token length > 1 */
				stack.push((int) stack.pop() > (int) stack.pop());

				if ((boolean) stack.pop()
						&& !((String) heap.get("stop_words")).contains("," + ((String) heap.get("token")) + ",")) {
					/*
					 * Token length is >1 and the word is NOT present in stop
					 * words
					 */

					if (((HashMap<String, Integer>) heap.get("token_map")).containsKey(heap.get("token"))) {
						/*
						 * This means, we have seen the token before; so update
						 * the count for the word
						 */
						stack.push(1);
						stack.push(((HashMap<String, Integer>) heap.get("token_map")).get(heap.get("token")));
						addIntItems();
						((HashMap<String, Integer>) heap.get("token_map")).put((String) heap.get("token"),
								(int) stack.pop());
					} else {
						/*
						 * Seeing the token for the 1st time, so adding the same
						 * into the HashMap
						 */
						((HashMap<String, Integer>) heap.get("token_map")).put((String) heap.get("token"), 1);
					}
				}
				heap.put("token", "");
			}

			/* update the loop counter */
			stack.push(1);
			stack.push(heap.get("loopcounter"));
			addIntItems();
			heap.put("loopcounter", stack.pop());
		}

		/*
		 * Clean the heap and push data on stack that needs to be referred later
		 */
		heap.remove("stop_words");
		heap.remove("inputfile");
		heap.remove("token");
		heap.remove("loopcounter");
		stack.push(heap.get("token_map"));
		heap.remove("token_map");
	}

	@SuppressWarnings("unchecked")
	public static void sortAndPrint() {
		heap.put("token_map", stack.pop());
		/* The final list that will be used for printing */
		heap.put("sorted_list", new ArrayList<String>());

		/* List of all the keys(tokens) */
		heap.put("key_list", ((HashMap<String, Integer>) heap.get("token_map")).keySet().toArray());

		/* Adding the 1st key in the sorted list */
		((ArrayList<String>) heap.get("sorted_list")).add(0, (String) (((Object[]) heap.get("key_list"))[0]));

		heap.put("loopcounter", 1);

		while (true) {
			stack.push(((Object[]) heap.get("key_list")).length);
			stack.push(heap.get("loopcounter"));
			/* To iterate through all the keys(tokens) once */
			checkLoopCondition();
			if (!(boolean) stack.pop()) {
				break;
			}

			/* Extract the new token from keylist */
			heap.put("new_token", (String) ((Object[]) heap.get("key_list"))[(int) heap.get("loopcounter")]);

			/* Extract the frequency of the new token from the token_map */
			heap.put("new_token_frequency",
					((HashMap<String, Integer>) heap.get("token_map")).get((String) heap.get("new_token")));

			/* Flag denotes that we HAVE NOT sorted/processed the token yet */
			heap.put("flag", true);

			/*
			 * If we already have 25 elements in the sorted list, check with the
			 * last element, if new token frequency is less than last element,
			 * we know that this element is useless. Principle in use -
			 * "If we know that an answer is bad, there is no point finding out exactly how bad it is."
			 */
			if (((ArrayList<String>) heap.get("sorted_list")).size() >= 25) {
				heap.put("old_token", (String) ((ArrayList<String>) heap.get("sorted_list")).get(24));
				heap.put("old_token_frequency",
						((HashMap<String, Integer>) heap.get("token_map")).get((String) heap.get("old_token")));
				if ((int) heap.get("new_token_frequency") < (int) heap.get("old_token_frequency")) {
					/*
					 * Frequency is less than the least frequent item we have,
					 * hence the token is now processed
					 */
					heap.put("flag", false);
				}
			}

			if ((boolean) heap.get("flag")) {
				/* To find proper position in the list */
				heap.put("innerloopcounter", 0);

				while (true) {
					stack.push(Math.min(((ArrayList<String>) heap.get("sorted_list")).size(), 25));
					stack.push(heap.get("innerloopcounter"));
					/*
					 * We need to check only top 25 elements in the sorted list
					 */
					checkLoopCondition();
					if (!(boolean) stack.pop())
						break;

					/* Frequency test */
					heap.put("old_token", (String) ((ArrayList<String>) heap.get("sorted_list"))
							.get((int) heap.get("innerloopcounter")));
					heap.put("old_token_frequency",
							((HashMap<String, Integer>) heap.get("token_map")).get((String) heap.get("old_token")));
					if ((int) heap.get("new_token_frequency") >= (int) heap.get("old_token_frequency")) {
						/* We found the correct position of the item */
						((ArrayList<String>) heap.get("sorted_list")).add((int) heap.get("innerloopcounter"),
								(String) heap.get("new_token"));

						heap.put("flag", true);
						break;
					}

					// Update the inner loop counter
					stack.push(1);
					stack.push(heap.get("innerloopcounter"));
					addIntItems();
					heap.put("innerloopcounter", stack.pop());
				}

				if (!(boolean) heap.get("flag")) {
					/*
					 * If the token is still not processed, then it means we
					 * have less than 25 items, and this token is the last of
					 * all, so append at end
					 */
					((ArrayList<String>) heap.get("sorted_list")).add((String) heap.get("new_token"));
				}
			}

			// Update the loop counter
			stack.push(1);
			stack.push(heap.get("loopcounter"));
			addIntItems();
			heap.put("loopcounter", stack.pop());
		}

		/* Always clean up your own mess */
		heap.remove("innerloopcounter");
		heap.remove("key_list");
		heap.put("loopcounter", 0);

		while (true) {
			stack.push(25);
			stack.push(heap.get("loopcounter"));
			/* Iterate through the 1st 25 elements */
			checkLoopCondition();
			if (!(boolean) stack.pop())
				break;

			/*
			 * Print the tokens and their frequencies because we have now got
			 * the values #done
			 */
			System.out.println((String) ((ArrayList<String>) heap.get("sorted_list")).get((int) heap.get("loopcounter"))
					+ " - " + ((HashMap<String, Integer>) heap.get("token_map")).get(
							(String) ((ArrayList<String>) heap.get("sorted_list")).get((int) heap.get("loopcounter"))));

			// Update the loop counter
			stack.push(1);
			stack.push(heap.get("loopcounter"));
			addIntItems();
			heap.put("loopcounter", stack.pop());
		}

		/* Told you once, repeating it again, Always clean up your own mess */
		heap.clear();
	}

	public static void main(String args[]) throws IOException {
		stack = new Stack<Object>();
		stack.push(args[0]);
		heap = new HashMap<String, Object>();
		read_file();
		generateTokens();
		sortAndPrint();
	}
}
