import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * In this constraint, we allowed the use of variables, but we aren't allowed
 * to create abstractions or procedures. The use of libraries need to be as 
 * minimal as possible
 */

/**
 * 
 * @author Madhur J. Bajaj
 *
 */
public class Monolithic {
	public static void main(String args[]) throws IOException {
		Scanner sc = new Scanner(new FileReader("..\\pride-and-prejudice.txt"));
		sc.useDelimiter("\\Z");
		String input = sc.next();
		sc.close();
		sc = new Scanner(new FileReader("..\\stop_words.txt"));
		String stop_words = "," + sc.next();
		sc.close();
		String token = "";
		HashMap<String, Integer> mapping = new HashMap<String, Integer>();
		ArrayList<String> sortedList = new ArrayList<String>();

		for (int i = 0; i < input.length(); i++) {
			char ch = input.charAt(i);
			if (Character.isLetterOrDigit(ch)) {
				/* Generating Token */
				token += ch;
			} else if (token.length() >= 2 && !stop_words.contains("," + token.toLowerCase() + ",")) {
				/* Token has been generated and it is a valid token */
				token = token.toLowerCase();

				if (mapping.containsKey(token)) {
					/*
					 * Case where the token has already been seen before,
					 * increment its counter
					 */
					mapping.put(token, mapping.get(token) + 1);

					if (sortedList.size() >= 25) {
						/*
						 * We shall initiate the sort ONLY when we know that
						 * this token is in top 25, since we already have top
						 * 25, we can predict it at this point
						 */
						if (mapping.get(token) > mapping.get(sortedList.get(24))) {
							/*
							 * Yes, this token deserves a spot in top 25, so
							 * start iteration from 24th to 0 and find its
							 * position, and insert it there. Because that is
							 * what even Heisenberg would do.
							 */
							int index = sortedList.indexOf(token);
							for (int j = Math.min(24, index); j > 0; j--) {
								if (mapping.get(token) > mapping.get(sortedList.get(j - 1))) {
									sortedList.remove(token);
									sortedList.add(j - 1, token);
								} else {
									/*
									 * Now we know that the rank of token can
									 * not improve, and since it doesn't improve
									 * for j-1, it sure wont improve for j-2 and
									 * so on; never ask out a girl again who's
									 * told you no once. Answer won't change.
									 * #factOfLife
									 */
									break;
								}
							}
						}
					} else {
						/*
						 * We don't even have 25 tokens yet, so we always need
						 * to sort the list; because top 25 are ALWAYS sorted
						 */
						for (int j = sortedList.size() - 1; j > 0; j--) {
							if (mapping.get(token) > mapping.get(sortedList.get(j - 1))) {
								sortedList.remove(token);
								sortedList.add(j - 1, token);
							} else {
								/*
								 * Again, do not ask out a girl again who's told
								 * you no once
								 */
								break;
							}
						}
					}
				} else {
					/*
					 * We know that this token exists, but not in top 25, save
					 * it for reference, who knows someday it'll grow up and
					 * crack the top 25; we might need it then
					 */
					sortedList.add(token);
					mapping.put(token, 1);
				}
				token = "";
			} else {
				token = "";
			}
		}

		for (int i = 0; i < 25; i++) {
			System.out.println(sortedList.get(i) + "  " + mapping.get(sortedList.get(i)));
		}

		/* Cleaning up my mess */
		mapping.clear();
		sortedList.clear();
	}
}