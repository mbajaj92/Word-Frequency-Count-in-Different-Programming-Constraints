import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * In this style the main objective is to reduce the number of characters, in the code.
 */

/**
 * 
 * @author Madhur J. Bajaj
 *
 */
public class CodeGolf {
	@SuppressWarnings("resource")
	public static void main(String args[]) throws Exception {
		/* This line will load the stop words into String s */
		String s = "," + (new Scanner(new FileReader("..\\stop_words.txt")).useDelimiter("\\Z").next());

		/*
		 * This line will read the file, tokenize it, lower case it, remove the
		 * stop words, count the frequency, sort them in reverse order, and then
		 * print the top 25
		 */
		(Arrays.asList(new Scanner(new FileReader(args[0])).useDelimiter("\\Z").next().replaceAll("[\\W_]", " ")
				.split(" "))).stream().map(w -> w.trim().toLowerCase())
						.filter(w -> (w.length() > 1 && !s.contains("," + w + ",")))
						.collect(Collectors.groupingBy(e -> e, Collectors.counting())).entrySet().stream()
						.sorted((x, y) -> y.getValue().compareTo(x.getValue())).limit(25)
						.forEach(p -> System.out.println(p.getKey() + " - " + p.getValue()));
	}
}