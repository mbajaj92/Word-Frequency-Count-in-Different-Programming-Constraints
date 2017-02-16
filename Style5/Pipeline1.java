import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * In this implementation we will try to emulate the pipeline style of coding;
 * where the out-put of every function is taken in as an input for another
 * function, creating a chain of functions.
 * 
 * @author Madhur J. Bajaj
 *
 */
public class Pipeline1 {

	/**
	 * This function will read the complete file and return the same.
	 * 
	 * @param path
	 * @return A string containing the complete file
	 * @throws Exception
	 */
	public static String read_data(String path) throws Exception {
		String data = "";
		Scanner sc = new Scanner(new FileReader(path));
		sc.useDelimiter("\\Z");
		data = sc.next();
		sc.close();
		return data;
	}

	/**
	 * This function will take a String and replace all Alpha-Numeric Characters
	 * with space; change to lower-case; and convert to a List of the words
	 * 
	 * @param data
	 * @return
	 */
	public static List<String> cleanDataAndSplit(String data) {
		return Arrays.asList(data.replaceAll("[^A-Za-z0-9]", " ").toLowerCase().split(" "));
	}

	/**
	 * This will remove all the stop words, trim them.
	 * 
	 * @param words
	 * @return
	 * @throws Exception
	 */
	public static List<String> removeUnwantedWords(List<String> words, String ignore) throws Exception {
		Scanner sc = new Scanner(new FileReader(ignore));
		sc.useDelimiter("\\Z");
		String stopwords = "," + sc.next();
		sc.close();
		for (int i = words.size() - 1; i >= 0; i--) {
			if (stopwords.contains("," + words.get(i) + ","))
				words.set(i, " ");
		}
		words.forEach((word) -> word.trim());
		return words;
	}

	/**
	 * This will calculate the frequency of the given list, and ignore all the
	 * words with length < 2
	 * 
	 * @param words
	 * @return
	 */
	public static ArrayList<Map.Entry<String, Integer>> calcFreq(List<String> words) {
		HashMap<String, Integer> freq = new HashMap<String, Integer>();
		for (String word : words) {
			if (word.length() < 2)
				continue;

			if (freq.containsKey(word))
				freq.put(word, (freq.get(word) + 1));
			else
				freq.put(word, 1);
		}
		return new ArrayList<Map.Entry<String, Integer>>(freq.entrySet());
	}

	/**
	 * This will sort the given ArrayList, and print the top 25 values
	 * 
	 * @param values
	 */
	public static void sortAndPrint(ArrayList<Map.Entry<String, Integer>> values) {
		Collections.sort(values, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}
		});

		for (int i = 0; i < 25; i++)
			System.out.println(values.get(i).getKey() + " - " + values.get(i).getValue());
	}

	public static void main(String args[]) throws Exception {
		sortAndPrint(calcFreq(removeUnwantedWords(cleanDataAndSplit(read_data(args[0])), "..\\stop_words.txt")));
	}
}