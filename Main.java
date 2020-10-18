import java.awt.Point;
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
	 * Class representing a magnet's location
	 * @author DGOAT
	 *
	 */
	public class MagnetLoc
	{
		// location of positive side of magnet
		public Point pos;
		
		// location of negative side of magnet
		public Point neg;
		
		// Default Constructor
		public MagnetLoc()
		{
			pos = new Point(0,0);
			neg = new Point(0,0);
		}
	}
	
	
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
	 * @param board = 2d array representing the board
	 * @param magnets = number of magnets we need to put on the board
	 * @return
	 * 				= list of MagnetLoc objects representing the places we placed the magnets
	 */
	public static List<MagnetLoc> placeMagnets(char[][] board, int magnets)
	{
		
		// draw it out first so that I can picture this better
		// and see how I can generalize the algorithm to 
		// fit all cases 	
		
		/*
		 * Algorithm after writing it out:
		 * 
		 * 1. Find the next home occurrence (+ or - that is defined from the board's birth)
		 * 2. Redefine the spaces around the current home occurrence to be compatible
		 * (we will perform this step cell by cell around the home occurence)
		 * Ex. if we are at a + then we must make the spaces to the left, right, and 
		 * directly underneath - unless one of those locations already has a + or -
		 * occupying it (edge case here)
		 * 3. Repeat step 2 until we are out of home occurrences that we haven't done this
		 * for
		 * 4. Once we are out of home occurrences, if we still don't have at least 2 * M spaces
		 * that we have defined, then we must do what we did for home occurrences with
		 * free spaces 
		 */
	
		
		// these are the home occurrences where each point holds location to a home occurrence
		List<Point> home = findHomeOccurrences(board);
		
		// number of spaces where we can validly place a magnet
		int spaces = home.size();
		
		// number of spaces required to place the number of magnets down
		int requiredSpaces = magnets * 2;
		
		String oneLine = "";
		char[] c = {'+', '-'};
		
		while(spaces != requiredSpaces)
		{
			// redefine spaces around current home space to be valid places to place a magnet
			for (int i = 0; i < home.size(); i++)
			{
				//  redefine spaces around current home space
				
				// gives us the coordinates of current home space
				Point currHome = home.get(i);
						
				// get the char value to redefine the area around home to be
				char redefine = board[currHome.x][currHome.y] == '+' ? '-' : '+';
				
				// redefine spaces around home space
				spaces += redefineFreeSpaces(board, home.get(i), redefine);
					
			}
				
		}
		
		return null;
		
	}
	
	/**
	 * Method to change the free spaces around the specified home coordinates 
	 * of board with the character specified by redefine
	 * @param board = 2d board to hold all the magnets
	 * @param coordinates = the coordinates of the current home space
	 * @param redefine = character to change the spaces around around home space
	 * 								  to if they fit the criteria
	 * @return 
	 * 				= the number of spaces added to place magnets on (spaces successfully changed)
	 */
	public static int redefineFreeSpaces(char[][] board, Point coordinates, char redefine)
	{		
		// base case (redefine 0 points)
		if (board[coordinates.x][coordinates.y] == redefine )
		{
			return 0;
		}
		
		// base case 2 (redefine 1 point)
		else if (board[coordinates.x][coordinates.y] == '*')
		{
			board[coordinates.x][coordinates.y] = redefine;
			return 1;
		}
			
		
		// determine if you will need to redefine 2 or 3 points
		
		// coordinates x will be vertical and y will be horizontal in board
		// define 3 points
		// if we are not at 0 and not at board.length - 1 (last space)

		// if we are anywhere in the first row(x == 0) and not at the first column (y > 0)
		// we will have 3 points to define
		// redefine location to the left of coordinate
			if (coordinates.y > 0)
				if (board[coordinates.x][coordinates.y - 1] == '*')
					redefineFreeSpaces(board, new Point(coordinates.x, coordinates.y - 1), redefine);
			
			// redefine location to the right of coordinate
			if (coordinates.y < board[0].length - 1)
				if(board[coordinates.x][coordinates.y + 1] != redefine)
					redefineFreeSpaces(board, new Point(coordinates.x, coordinates.y + 1), redefine);
					
			// redefine location directly above coordinate
			if (coordinates.x == 1)
				if(board[0][coordinates.y] != redefine)
					redefineFreeSpaces(board, new Point(0, coordinates.y), redefine);
			
			// redefine location directly below coordinate
			if (coordinates.x == 0)
				if(board[1][coordinates.y] != redefine)
					redefineFreeSpaces(board, new Point(1, coordinates.y), redefine);
				
			
		return 0;
	}
	
	
	/**
	 * Method to determine the locations of all indices that start with
	 * '+' and '-' which I call home occurrences representing their locations 
	 * @param board = the 2d board that we are placing magnets on
	 * @return
	 * 				= a list of point objects where each index will hold a point 
	 * 					corresponding to a home index 
	 */
	public static List<Point> findHomeOccurrences(char[][] board)
	{
		List<Point> home = new ArrayList<Point>();
		
		for(int i = 0; i < 2; i++)
		{
			for (int j = 0; j < board[i].length; j++)
			{
				if (board[i][j] == '+' || board[i][j]== '-')
					home.add(new Point(i, j));
			}
		}
		
		return home;
	}
	
	public static void main(String[] args)
	{
		char[][] board = readInput();
				
		System.out.print("HI");
		
		// CURRENTLY NEED TO WORK ON RECURSION LOGIC FOR 
		// METHOD REDEFINE_FREE_SPACES

	}

}
