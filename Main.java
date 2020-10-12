import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class that houses the solution to CECS 328 Project 3 Magnets
 * @author DGOAT
 * Date Started: 10/11/2020
 * Last Edit: 10/11/2020
 * Date Finished: 
 */
public class Main 
{

	/**
	 * Method to write the results of countRecustions to an
	 * output file
	 * @param ezDivNums = easily divisible numbers
	 * @param scores = the scores to write to the file
	 */
	public static void writeOutput(List<Integer> ezDivNums, Integer[] scores)
	{
		File outFile = new File("output.txt");
		PrintWriter writer = null;
		int index = 0;

		try
		{
			writer = new PrintWriter(outFile);

			// print out the "easily divisible nums" and their scores 
			// separated by a space
			for (Integer Int : ezDivNums)
			{
				writer.print(Int + " " + scores[index] + "\n");
				index++;
			}


		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			writer.close();
		}

	}


	/**
	 * Method to read the input from the file and return
	 * a char array that models the board where the
	 * magnets will reside
	 * @return
	 * 				= a character array corresponding to the board
	 */
	public static char[][] readInput()
	{
		// Read input from file
		File inFile = new File("input.txt");
		Scanner scan = null;

		// necessary variables to store input 
		int numMagnets = 0;  //M
		String oneLine = ""; // one line of input
		char[][] board = new char[2][0];		

		try
		{
			scan = new Scanner(inFile);

			// get the number of magnets to place
			numMagnets = Integer.valueOf(scan.nextLine());
			
			// get the rest of the lines of input
			oneLine = scan.nextLine();
			
			// populate the array with the input
			board[0] = oneLine.toCharArray();
			board[1] = scan.nextLine().toCharArray();

		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			scan.close();
		}

		return board;
	}

	/**
	 * Method to place the magnets in their proper place
	 * @return
	 */
	public static int[][] placeMagnets(char[][] board)
	{
		
		// draw it out first so that I can picture this better
		// and see how I can generalize the algorithm to 
		// fit all cases 	
		
		
		return null;
		
	}
	
	public static void main(String[] args)
	{
		char[][] board = readInput();
		System.out.print("HI");

	}

}
