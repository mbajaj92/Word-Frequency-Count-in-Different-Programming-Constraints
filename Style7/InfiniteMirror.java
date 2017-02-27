import java.io.FileReader;
import java.io.IOException;
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
 * In this style, we are suppose to model the complete problem using Induction;
 * solvec the problem for case n, and then use the same solution to solve for
 * case n+1; This requires extensive use of Recursion.
 * Since there is a practical limit to the depth of recursion; 
 * I have implemented the following memory safe solution
 * 
 * @author Madhur J. Bajaj
 *
 */
public class InfiniteMirror {
	/* This is the limit I have set for my implementation */
	public static int RECURSION_LIMIT = 1000;

	/**
	 * This function will count the frequency of the words in data from start to
	 * end(inclusive) index, recursively
	 */
	public static void count(List<String> data, String stop_words, int start, int end, HashMap<String, Integer> map) {
		if (start > end)
			/* This is the base case, where we stop the recursion */
			return;

		String str = data.get(start).trim().toLowerCase();
		if (str.length() >= 2 && !stop_words.contains("," + str + ",")) {
			if (map.containsKey(str))
				map.put(str, map.get(str) + 1);
			else
				map.put(str, 1);
		}

		/* Although Java doesn't optimize it, I have still coded the solution
		 * with Tail Call Optimization in mind */
		count(data, stop_words, start + 1, end, map);
	}

	/**
	 * This function will process the list data starting from start index till
	 * the end of the list, recursively
	 */
	public static void process(List<String> data, String stop_words, int start, HashMap<String, Integer> map) {
		if (data.size() - 1 - start > RECURSION_LIMIT) {
			count(data, stop_words, start, start + RECURSION_LIMIT, map);
			process(data, stop_words, start + RECURSION_LIMIT + 1, map);
		} else
			count(data, stop_words, start, data.size() - 1, map);
	}

	public static void main(String args[]) throws IOException {
		Scanner sc = (new Scanner(new FileReader(args[0])));
		sc.useDelimiter("\\Z");
		List<String> list = Arrays.asList((sc.next().replaceAll("[^A-Za-z0-9]", " ")).split(" "));
		sc.close();

		sc = (new Scanner(new FileReader("..\\stop_words.txt")));
		sc.useDelimiter("\\Z");
		String stop_words = "," + sc.next();
		sc.close();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		process(list, stop_words, 0, map);

		ArrayList<Map.Entry<String, Integer>> abc = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		/* Sorting the list */
		Collections.sort(abc, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}
		});

		for (int i = 0; i < 25; i++)
			System.out.println(abc.get(i).getKey() + " - " + abc.get(i).getValue());

	}
}