import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class GoodOldTimes {
	public static void main(String args[]) throws IOException {
		Scanner readScanner = new Scanner(new FileReader("..\\stop_words.txt"));
		/* Creating heap, as a String Array */
		String heap[] = new String[11];

		/* Loading all the stop words in heap[0] as a String */
		heap[0] = "," + readScanner.nextLine() + ",";

		// heap[1] - the next line to be processed
		heap[2] = ""; // the generated token is stored in this variable.
		// heap[3] - array counter
		// heap[4] - updated counter
		// heap[5] - the file seek position
		// heap[6] - used as boolean to check if the generated token exists in
		//			 the intermediate
		// heap[7] - the string that need to be written back in the intermediate
		// heap[8] - to store the count of the word in intermediate
		// heap[9] - to store the next line from intermediate
		// heap[10] - to store last read file name
		readScanner.close();
		readScanner = new Scanner(new FileReader("..\\pride-and-prejudice.txt"));
		RandomAccessFile randomAccessFile = null;

		while (readScanner.hasNextLine()) {
			/* Load the next line to be processed */
			heap[1] = "" + readScanner.nextLine();

			for (heap[3] = "0"; Integer.parseInt(heap[3]) < heap[1].length() + 1; heap[3] = ""
					+ (Integer.parseInt(heap[3]) + 1)) {
				if ((Integer.parseInt(heap[3]) < heap[1].length())
						&& (((heap[1].charAt(Integer.parseInt(heap[3])) >= 'a')
								&& (heap[1].charAt(Integer.parseInt(heap[3])) <= 'z'))
								|| ((heap[1].charAt(Integer.parseInt(heap[3])) >= 'A')
										&& (heap[1].charAt(Integer.parseInt(heap[3])) <= 'Z'))
								|| ((heap[1].charAt(Integer.parseInt(heap[3])) >= '0')
										&& (heap[1].charAt(Integer.parseInt(heap[3])) <= '9')))) {
					heap[2] += heap[1].charAt(Integer.parseInt(heap[3]));
				} else if (heap[2].trim().length() >= 2
						&& !heap[0].contains("," + heap[2].toLowerCase().trim() + ",")) {
					/* A new token has been detected, that is not a stop word */
					heap[2] = heap[2].toLowerCase().trim();

					/* If the corresponding file is already open, no point
					 * re-opening it */
					if (randomAccessFile == null || !heap[10].equals(heap[2].charAt(0))) {
						/* We know that the required file is not open, so we
						 * shall close the earlier file and open the new one,
						 * mapping being a:a.txt, b:b.txt ... 0-9:0.txt */
						if(randomAccessFile != null)
							randomAccessFile.close();

						heap[10] = (heap[2].charAt(0) >= 'a' && heap[2].charAt(0) <= 'z') ? "" +heap[2].charAt(0):"0";
						randomAccessFile = new RandomAccessFile(heap[10]+".txt", "rw");
					} else {
						randomAccessFile.seek(0);
					}
					/* We shall assume that this token is not present in the
					 * intermediate */
					heap[6] = "NOT FOUND";
					heap[5] = "0";

					while (true) {
						heap[5] = "" + randomAccessFile.getFilePointer();
						/* Load the next line from intermediate, shall be loaded
						 * in the format of 'abc 001'*/
						heap[9] = randomAccessFile.readLine();

						/* to handle EOF */
						if (heap[9] == null || heap[9].equals(""))
							break;

						/* Load the word part of the line,'abc' */
						heap[8] = heap[9].split(" ")[0];

						if (heap[8].equals(heap[2])) {
							/* a word match has been found */
							heap[6] = "FOUND";

							/* Load the current frequency and update by 1 */
							heap[4] = "" + (Integer.parseInt(heap[9].split(" ")[1]) + 1);

							/* Change the format of the string, so append extra
							 * 0's to make it 3 digits */
							heap[4] = Integer.parseInt(heap[4]) < 10 ? "00" + heap[4]
									: (Integer.parseInt(heap[4]) < 100) ? "0" + heap[4] : heap[4];

							/* Create the String that needs to be written, in
							 * the format 'abc 002' */
							heap[7] = heap[2] + " " + heap[4];
							break;
						}
					}

					if (heap[6].equals("FOUND")) {
						/* Since the word was found in the file, go to seek
						 * position and over-write */
						randomAccessFile.seek(Long.parseLong(heap[5]));
						randomAccessFile.writeBytes(heap[7]);
					} else {
						/* Since the word was not found, append the String at
						 * the end in the format, 'xyz 001' */
						randomAccessFile.seek(randomAccessFile.length());
						randomAccessFile.writeBytes(heap[2] + " 001\n");
					}

					/* Reset Token */
					heap[2] = "";
				} else {
					/* Since the token was rejected, we reset it */
					heap[2] = "";
				}
			}
		}

		/* Close all the files, and pointers */
		randomAccessFile.close();
		readScanner.close();
		randomAccessFile = null;

		/* Destroy the heap */
		heap = null;

		/* Re-creating heap, this time as a 2D String array */
		String newHeap[][] = new String[4][];
		// newHeap[0] - an array of the top 25 words, in the format 'abc 005'
		newHeap[1] = new String[1]; // - An array of size 1, to store newHeap[0]
		newHeap[1][0] = ""; // as a single string in the format 'abc 004,xyz
							// 003,gef 002'
		// newHeap[2] - to store the token and frequency that needs to be
		// inserted
		
		newHeap[3] = new String[4]; 
		/* [3][0] is counter
		 * [3][1] is boolean
		 * [3][2] is file counter
		 * [3][3] is file name */

		/* Counter for the files, since we need to go thorough all the potential
		 * 27 files. */
		for (newHeap[3][2] = "0"; Integer.parseInt(newHeap[3][2]) <= 26; newHeap[3][2] = ""
				+ (Integer.parseInt(newHeap[3][2]) + 1)) {
			try {
				/* This is special case, where we read the file with all the
				 * numbers */
				if (newHeap[3][2].equals("26"))
					newHeap[3][3] = "0.txt";
				else
					newHeap[3][3] = (char) (Integer.parseInt(newHeap[3][2]) + 'a') + ".txt";
				readScanner = new Scanner(new FileReader(newHeap[3][3]));
			} catch (Exception e) {
				continue;
			}

			while (readScanner.hasNextLine()) {
				/* Split heap[1] into 25 sections and store, each section is of
				 * format 'abc 005' */
				newHeap[0] = newHeap[1][0].split(",", 25);

				/* Read the next line, index [2][0] - word and index [2][1] -
				 * frequency */
				newHeap[2] = readScanner.nextLine().split(" ");

				/* Since this is the 1st word, simply input */
				if (newHeap[1][0].length() == 0) {
					newHeap[1][0] = newHeap[2][0] + " - " + Integer.parseInt(newHeap[2][1]);
					continue;
				}

				/* We know that the count of the current word is less than the
				 * least value that we have, no point wasting time in sorting
				 * hence we ignore */
				if ((newHeap[0].length == 25)
						&& (Integer.parseInt(newHeap[0][24].split(" - ")[1]) > Integer.parseInt(newHeap[2][1])))
					continue;

				/* Initial assumption, we haven't inserted yet; which to be
				 * honest isn't wrong */
				newHeap[3][1] = "NOT INSERTED";
				for (newHeap[3][0] = "0"; Integer.parseInt(newHeap[3][0]) < newHeap[0].length; newHeap[3][0] = ""
						+ (Integer.parseInt(newHeap[3][0]) + 1)) {

					if ((Integer.parseInt(newHeap[0][Integer.parseInt(newHeap[3][0])].split(" - ")[1])) <= Integer
							.parseInt(newHeap[2][1])) {

						/* Break the string and insert the word in between,
						 * where-ever it fits */
						newHeap[1][0] = newHeap[1][0].substring(0,
								newHeap[1][0].indexOf(newHeap[0][Integer.parseInt(newHeap[3][0])])) + newHeap[2][0]
								+ " - " + Integer.parseInt(newHeap[2][1]) + "," + newHeap[1][0]
										.substring(newHeap[1][0].indexOf(newHeap[0][Integer.parseInt(newHeap[3][0])]));
						newHeap[3][1] = "INSERTED";
						break;
					}
				}

				if (newHeap[3][1].equals("NOT INSERTED") && (newHeap[0].length < 25))
					/* If the word wasn't inserted, but the total number of
					 * words that we have is less than 25, then it means this is
					 * the least frequent of all the words we have seen hence
					 * append at the back */
					newHeap[1][0] = newHeap[1][0] + "," + newHeap[2][0] + " - " + Integer.parseInt(newHeap[2][1]);
				else if (newHeap[0].length == 25 && newHeap[3][1].equals("INSERTED"))
					/* We have inserted the word, but we already had 25 words
					 * from before, then remove the last word */
					newHeap[1][0] = newHeap[1][0].substring(0, newHeap[1][0].lastIndexOf(','));
			}
			readScanner.close();

			/* Always clean up your own mess */
			(new File(newHeap[3][3])).delete();
		}

		newHeap[0] = newHeap[1][0].split(",", 25);
		for (newHeap[3][0] = "0"; Integer.parseInt(newHeap[3][0]) < newHeap[0].length; newHeap[3][0] = ""
				+ (Integer.parseInt(newHeap[3][0]) + 1)) {
			/* Give the answer; because now we can */
			System.out.println(newHeap[0][(Integer.parseInt(newHeap[3][0]))]);
		}
	}
}
