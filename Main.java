import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

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
	public static class MagnetLoc
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
	 * Class representing the board and 
	 * the number of magnets to place on the board
	 * @author DGOAT
	 *
	 */
	public static class boardNMagnets
	{
		char[][] board;
		int magnets;

		boardNMagnets(char[][] _board, int m)
		{
			board = _board;
			magnets = m;
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
	public static boardNMagnets readInput()
	{
		// Read input from file
		File inFile = new File("inputTest.txt");
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

		return new boardNMagnets(board, numMagnets);
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

		//	while(spaces != requiredSpaces)
		//{
		// redefine spaces around current home space to be valid places to place a magnet
		for (int i = 0; i < home.size(); i++)
		{
			//  redefine spaces around current home space

			// gives us the coordinate of current home space
			Point currHome = home.get(i);

			// get the char value to redefine the area around home to be
			char redefine = board[currHome.x][currHome.y] == '+' ? '-' : '+';

			// redefine spaces around home space
			spaces += redefineAreaAroundHomeSpace(board, home.get(i), redefine);

		}


		// these are the free space occurrences where each point holds location to a free space occurrence
		List<Point> freeSpace = findFreeSpaceOccurrences(board);
		int j = 0;

		while (spaces < requiredSpaces )
		{
			// gives us the coordinate of current free space
			Point currFree = freeSpace.get(j);

			// can I define this space to be non free? (return 1 if yes and 0 if no)
			spaces += redefineFreeSpaces(board, currFree);
			j++;
		}

		for (int i = 0; i < 2; i++)
		{
			System.out.print("\n");
			for (int b= 0; b < board[0].length; b++)
				System.out.print(board[i][b] + " ");
		}
		
		
		if (spaces != requiredSpaces)
			System.out.print("Fix this edge case");

		return null;

	}

	/**
	 * Method to change the free spaces around the specified home coordinate 
	 * of board with the character specified by redefine
	 * @param board = 2d board to hold all the magnets
	 * @param coordinate = the coordinate of the current home space
	 * @param redefine = character to change the spaces around around home space
	 * 								  to if they fit the criteria
	 * @return 
	 * 				= the number of spaces added to place magnets on (spaces successfully changed)
	 */
	public static int redefineAreaAroundHomeSpace(char[][] board, Point coordinate, char redefine)
	{		
		//		// base case (redefine 0 points)
		//		if (board[coordinate.x][coordinate.y] == redefine )
		//		{
		//			return 0;
		//		}
		//
		//		// base case 2 (redefine 1 point)
		//		else if (board[coordinate.x][coordinate.y] == '*')
		//		{
		//			board[coordinate.x][coordinate.y] = redefine;
		//			return 1;
		//		}

		int score = 0;
		// determine if you will need to redefine 2 or 3 points

		// coordinate x will be vertical and y will be horizontal in board
		// define 3 points
		// if we are not at 0 and not at board.length - 1 (last space)

		// if we are anywhere in the first row(x == 0) and not at the first column (y > 0)
		// we will have 3 points to define



		// redefine location to the left of coordinate
		if (coordinate.y > 0)
		{
			char temp = board[coordinate.x][coordinate.y - 1];

			if (board[coordinate.x][coordinate.y - 1] == '*')
			{
				board[coordinate.x][coordinate.y - 1] = redefine;

				// determine if the redefinition is compatible with the current board
				// if it is then we are good and add the score 
				// if it isn't then reassign current spot to old value
				if(isCompatible(board, new Point(coordinate.x, coordinate.y - 1)))
					score++;
				else
					board[coordinate.x][coordinate.y - 1] = temp;
			}

		}

		// redefine location to the right of coordinate
		if (coordinate.y < board[0].length - 1)
		{	
			char temp = board[coordinate.x][coordinate.y + 1];

			if(board[coordinate.x][coordinate.y + 1] == '*')
			{
				board[coordinate.x][coordinate.y + 1] = redefine;

				// determine if the redefinition is compatible with the current board
				// if it is then we are good and add the score 
				// if it isn't then reassign current spot to old value
				if(isCompatible(board, new Point(coordinate.x, coordinate.y + 1)))
					score++;
				else
					board[coordinate.x][coordinate.y + 1] = temp;
			}

		}
		// redefine location directly above coordinate
		if (coordinate.x == 1)
		{
			char temp = board[0][coordinate.y];

			if(board[0][coordinate.y] == '*')
			{
				board[0][coordinate.y] = redefine;

				// determine if the redefinition is compatible with the current board
				// if it is then we are good and add the score 
				// if it isn't then reassign current spot to old value
				if(isCompatible(board, new Point(0, coordinate.y)))
					score++;
				else
					board[0][coordinate.y] = temp;
			}
		}
		// redefine location directly below coordinate
		if (coordinate.x == 0)
		{
			char temp = board[1][coordinate.y];

			if(board[1][coordinate.y] == '*')
			{
				board[1][coordinate.y] = redefine;

				// determine if the redefinition is compatible with the current board
				// if it is then we are good and add the score 
				// if it isn't then reassign current spot to old value
				if(isCompatible(board, new Point(1, coordinate.y)))
					score++;
				else
					board[1][coordinate.y] = temp;
			}
		}

		return score;
	}


	/** (possibly speed up by getting more than one free space at a time like in redefineAreaAroundHomeSpace)
	 * Method to redefine free spaces to be usable by the program
	 * @param board = 2d board representing locations where we can place magnets
	 * @param coordinate = point representing the free space location we are at
	 * @return
	 * 				= the number of spaces we make usable (change to '+' or '-')
	 */
	public static int redefineFreeSpaces(char[][] board, Point coordinate)
	{
		//int score = 0;
		// determine if you will need to redefine 2 or 3 points

		// coordinate x will be vertical and y will be horizontal in board
		// define 3 points
		// if we are not at 0 and not at board.length - 1 (last space)

		// if we are anywhere in the first row(x == 0) and not at the first column (y > 0)
		// we will have 3 points to define
		List<Character> directions = new Vector<Character>();

		//char left, right, up, down;
		// redefine location to the left of coordinate
		if (coordinate.y > 0)
		{
			directions.add(board[coordinate.x][coordinate.y - 1]);
		}

		if (coordinate.y < board[0].length - 1)
		{	
			directions.add(board[coordinate.x][coordinate.y + 1]);
		}

		if (coordinate.x == 1)
		{
			directions.add(board[0][coordinate.y]);
		}

		if (coordinate.x == 0)
		{
			directions.add(board[1][coordinate.y]);
		}

		//char[] directions = {left, right, up, down};
		char c = '*';
		boolean found = false;
		for (int i = 0; i < directions.size(); i++)
		{
			for (int j = 0; j < directions.size(); j++)
			{
				// same character
				if (i == j)
					continue;

				// if the characters are incompatible
				else if (directions.get(i) != directions.get(j)
						&& (directions.get(j) != '*' && directions.get(i) != '*'))
				{
					return 0;
				}

				// compatible so far but haven't found a non star character
				else if (found == false)
				{
					if (directions.get(j) != '*')
					{
						c = directions.get(j);
						found = true;
					}
					else if (directions.get(i) != '*')
					{
						c = directions.get(i);
						found = true;
					}
				}
			}
		}

		// if we get here, the characters are compatible then we must assign the 
		// we can actually assign other characters here if we want to improve SPEED!!!!
		// just assign to + because it can be either one because it has free spaces all around it
		if (c == '*')
			return board[coordinate.x][coordinate.y] = '+';
		
		board[coordinate.x][coordinate.y] = c == '-' ? '+' : '-';
		return 1;
	}

	/**
	 * Method to determine if the free space that was redefine is 
	 * compatible with the rest of the board
	 * @param board = 2 dimensional character array representing the board
	 * @param coordinate = a point that represents the current point we are at
	 * 									  in the board
	 * @return
	 * 				= true if the redefinition is compatible
	 * 				= false otherwise
	 */
	public static boolean isCompatible(char[][] board, Point coordinate)
	{
		char current = board[coordinate.x][coordinate.y];

		// determine if the new value of coordinate is compatible with the rest of the board
		if (coordinate.y > 0)
		{
			if (board[coordinate.x][coordinate.y - 1] == current)
				return false;
		}

		//redefineAreaAroundHomeSpace(board, new Point(coordinate.x, coordinate.y - 1), redefine);

		// redefine location to the right of coordinate
		else if (coordinate.y < board[0].length - 1)
		{
			if(board[coordinate.x][coordinate.y + 1] == current)
				return false;
		}

		// redefine location directly above coordinate
		else if (coordinate.x == 1)
		{	
			if(board[0][coordinate.y] == current)
				return false;
		}

		// redefine location directly below coordinate
		else if (coordinate.x == 0)
		{	
			if(board[1][coordinate.y] == current)
				return false;
		}

		return true;
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

	/**
	 * Method to determine the locations of all indices that correspond with
	 * '*'  which I call free space occurrences representing their locations 
	 * @param board = the 2d board that we are placing magnets on
	 * @return
	 * 				= a list of point objects where each index will hold a point 
	 * 					corresponding to a free space index 
	 */
	public static List<Point> findFreeSpaceOccurrences(char[][] board)
	{
		List<Point> freeSpace = new ArrayList<Point>();

		for(int i = 0; i < 2; i++)
		{
			for (int j = 0; j < board[i].length; j++)
			{
				if (board[i][j] == '*' )
					freeSpace.add(new Point(i, j));
			}
		}

		return freeSpace;
	}

	public static void main(String[] args)
	{
		boardNMagnets bnm = readInput();
		placeMagnets(bnm.board, bnm.magnets);

		System.out.print("HI");

		// CURRENTLY NEED TO WORK ON RECURSION LOGIC FOR 
		// METHOD REDEFINE_FREE_SPACES

	}

}
