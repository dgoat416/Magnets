// Deron Washington II
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
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
	 * New point object to be comparable and cast from myPoint to Point
	 * @author DGOAT
	 *
	 */
	public static class myPoint extends Point implements Comparable<Point>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 3504962621239841892L;
		public Point iPoint;

		@Override
		public String toString()
		{
			return iPoint.toString();
		}

		/**
		 * Compares two points
		 * @param p = another point object for comparison
		 */
		@Override
		public int compareTo(Point p)
		{
			// TODO Auto-generated method stub
			if (iPoint.x == p.x)
			{
				if (iPoint.y == p.y)
					return 0;
				else if (iPoint.y < p.y)
					return -1;
				else if (iPoint.y > p.y)	
					return 1;
			}
			else if (iPoint.x < p.x )
				return -1;

			else if (iPoint.x > p.x)
				return 1;

			// error
			return -999;
		}

		/**
		 * Default Constuctor
		 */
		public myPoint()
		{
			super(new Point(0,0));
			iPoint = new Point(0,0);
		}

		public myPoint(int x, int y)
		{
			super(x,y);
			iPoint  = new Point();
			iPoint.x = x;
			iPoint.y = y;
		}

		public myPoint(Point p)
		{
			super(p);
			iPoint = p; 
		}

	}


	/**
	 * Class representing a magnet's location
	 * @author DGOAT
	 *
	 */
	public static class Domino
	{
		// location of positive side of magnet
		public myPoint pos;

		// location of negative side of magnet
		public myPoint neg;

		// Default Constructor
		public Domino()
		{
			this.pos = new myPoint();
			this.neg = new myPoint();
		}

		// Parameterized Constructor
		public Domino(Point _pos, Point _neg)
		{
			this.pos = new myPoint(_pos);
			this.neg = new myPoint(_neg);
		}

		/**
		 * Method to output a domino in the format 
		 * {Positive x Positive y Negative x Negative y}
		 */
		public String toString()
		{
			return this.pos.x + " " + this.pos.y + " "
					+ this.neg.x + " " + this.neg.y;
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
	 * @param dominos = the list of dominos to output
	 */
	public static void writeOutput(String outputName, List<Domino> dominos)
	{
		File outFile = new File(outputName);
		PrintWriter writer = null;

		try
		{
			writer = new PrintWriter(outFile);

			// print out the "easily divisible nums" and their scores 
			// separated by a space
			for (Domino domino : dominos)
			{
				writer.print(domino + "\n");
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
	 * Method to print the board 
	 * @param board = 2d array that represents the board to place magnets on
	 */
	public static void printBoard(char[][] board)
	{
		for (int i = 0; i < 2; i++)
		{
			System.out.print("\n");
			for (int b= 0; b < board[0].length; b++)
				System.out.print(board[i][b] + " ");
		}
	}

	/**
	 * Method to read the input from the file and return
	 * a char array that models the board where the
	 * magnets will reside
	 * @return
	 * 				= a character array corresponding to the board
	 */
	public static boardNMagnets readInput(String fileName)
	{
		// Read input from file
		File inFile = new File(fileName);
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
	 */
	public static List<myPoint> placeMagnets(char[][] board, int magnets)
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
		List<myPoint> home = findHomeOccurrences(board);

		// these are the spaces that have been added based on the below methods
		List<myPoint> additionalSpaces = new Vector<myPoint>();

		// number of spaces where we can validly place a magnet
		int spaces = home.size();

		// number of spaces required to place the number of magnets down
		int requiredSpaces = magnets * 2;

		//	while(spaces != requiredSpaces)
		//{
		// redefine spaces around current home space to be valid places to place a magnet
		for (int i = 0; i < home.size(); i++)
		{
			//  redefine spaces around current home space

			// gives us the coordinate of current home space
			myPoint currHome = home.get(i);

			// list to hold all the temporary spaces that have been added
			List<myPoint> temp = null;

			// get the char value to redefine the area around home to be
			char redefine = board[currHome.x][currHome.y] == '+' ? '-' : '+';

			// redefine spaces around home space
			temp = redefineAreaAroundHomeSpace(board, home.get(i), redefine);
			spaces += temp.size();
			additionalSpaces.addAll(temp);
		}

		// create dominos from what we found by redefineAreaAroundHomeSpace
		// how many do we have?




		// these are the free space occurrences where each point holds location to a free space occurrence
		List<myPoint> freeSpace = findFreeSpaceOccurrences(board);
		int j = 0;
		int additionalSpace = 0;

		while (spaces < requiredSpaces && j < freeSpace.size())
		{
			// gives us the coordinate of current free space
			myPoint currFree = freeSpace.get(j);

			// list to hold all the temporary spaces that have been added
			Set<myPoint> temp = null;

			// can I define this space to be non free? (return 1 if yes and 0 if no)
			temp = redefineFreeSpaces(board, currFree);
			spaces += temp.size();
			j++;

			// if spaces increased then add the current coordinate to additionalSpaces list
			if (temp.size() >= 1)
				additionalSpaces.addAll(temp);

		}

		// get an initial view of what the board looks like
//		printBoard(board);

		// add all changed points to the home list 
		home.addAll(additionalSpaces);

		return home;
		//		
		//		if (spaces != requiredSpaces)
		//			System.out.print("Fix this edge case");


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
	public static List<myPoint> redefineAreaAroundHomeSpace(char[][] board, myPoint coordinate, char redefine)
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
		List<myPoint> redefinedSpace = new Vector<myPoint>();
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
				//board[coordinate.x][coordinate.y - 1] = redefine;

				// determine if the redefinition is compatible with the current board
				// if it is then we are good and add the score 
				// if it isn't then reassign current spot to old value
				if(isCompatible(board, new myPoint(coordinate.x, coordinate.y - 1), redefine))
					redefinedSpace.add(new myPoint(coordinate.x, coordinate.y - 1));
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
				//board[coordinate.x][coordinate.y + 1] = redefine;

				// determine if the redefinition is compatible with the current board
				// if it is then we are good and add the score 
				// if it isn't then reassign current spot to old value
				if(isCompatible(board, new myPoint(coordinate.x, coordinate.y + 1), redefine))
					redefinedSpace.add(new myPoint(coordinate.x, coordinate.y + 1));
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
				//board[0][coordinate.y] = redefine;

				// determine if the redefinition is compatible with the current board
				// if it is then we are good and add the score 
				// if it isn't then reassign current spot to old value
				if(isCompatible(board, new myPoint(0, coordinate.y), redefine))
					redefinedSpace.add(new myPoint(0, coordinate.y));
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
				//board[1][coordinate.y] = redefine;

				// determine if the redefinition is compatible with the current board
				// if it is then we are good and add the score 
				// if it isn't then reassign current spot to old value
				if(isCompatible(board, new myPoint(1, coordinate.y), redefine))
					redefinedSpace.add(new myPoint(1, coordinate.y));
				else
					board[1][coordinate.y] = temp;
			}
		}

		return redefinedSpace;
	}


	/** (possibly speed up by getting more than one free space at a time like in redefineAreaAroundHomeSpace)
	 * Method to redefine free spaces to be usable by the program
	 * @param board = 2d board representing locations where we can place magnets
	 * @param coordinate = point representing the free space location we are at
	 * @return
	 * 				= the number of spaces we make usable (change to '+' or '-')
	 */
	public static Set<myPoint> redefineFreeSpaces(char[][] board, myPoint coordinate)
	{
		//int score = 0;
		// determine if you will need to redefine 2 or 3 points

		// coordinate x will be vertical and y will be horizontal in board
		// define 3 points
		// if we are not at 0 and not at board.length - 1 (last space)

		// if we are anywhere in the first row(x == 0) and not at the first column (y > 0)
		// we will have 3 points to define
		List<Character> directions = new Vector<Character>();

		// this is what we will return to indicate how many spaces we have successfully redefined
		Set<myPoint> redefinedSpaces = new TreeSet<myPoint>();

		// temp list to store the free spaces around coordinate
		Set<myPoint> treeSet = new TreeSet<myPoint>();

		//char left, right, up, down;
		// redefine location to the left of coordinate
		if (coordinate.y > 0)
		{
			directions.add(board[coordinate.x][coordinate.y - 1]);

			// if it is a free space char around coordinate (which is also a free space character)
			// add to treeSet
			if(board[coordinate.x][coordinate.y - 1] == '*')
				treeSet.add(new myPoint(coordinate.x, coordinate.y - 1));
		}

		if (coordinate.y < board[0].length - 1)
		{	
			directions.add(board[coordinate.x][coordinate.y + 1]);

			// if it is a free space char around coordinate (which is also a free space character)
			// add to treeSet
			if(board[coordinate.x][coordinate.y + 1] == '*')
				treeSet.add(new myPoint(coordinate.x, coordinate.y + 1));
		}

		if (coordinate.x == 1)
		{
			directions.add(board[0][coordinate.y]);

			// if it is a free space char around coordinate (which is also a free space character)
			// add to treeSet
			if(board[0][coordinate.y] == '*')
				treeSet.add(new myPoint(0, coordinate.y));
		}

		if (coordinate.x == 0)
		{
			directions.add(board[1][coordinate.y]);

			// if it is a free space char around coordinate (which is also a free space character)
			// add to treeSet
			if(board[1][coordinate.y] == '*')
				treeSet.add(new myPoint(1, coordinate.y));
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
					return redefinedSpaces;
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

		// if we get here, the characters are compatible then we must assign the new value to coordinate
		// we can actually assign other characters here if we want to improve SPEED!!!!
		// just assign to + because it can be either one because it has free spaces all around it

		char temp = '*';
		char[] redefine = {'+', '-'};

		if (c == '*') // check if this is compatible at location coordinate
		{
			// since the character can be '+' or '-' let's test to see which one works
			for (int i = 0; i < 2; i++)
			{
				//board[coordinate.x][coordinate.y] = redefine[i];
				// determine if the redefinition is compatible with the current board
				// if it is then we are good and add the score 
				// if it isn't then reassign current spot to old value
				if(isCompatible(board, new myPoint(coordinate.x, coordinate.y), redefine[i]))
				{
					redefinedSpaces.add(new myPoint(coordinate.x, coordinate.y));
					break;
				}

				else
					board[coordinate.x][coordinate.y] = temp;

			}

			// find an adjacent free space that works
			for (myPoint p : treeSet)
			{
				// since the character can be '+' or '-' let's test to see which one works
				for (int i = 0; i < 2; i++)
				{
					//					temp = board[p.x][p.y];
					//	board[coordinate.x][coordinate.y] = '-';

					// determine if the redefinition is compatible with the current board
					// if it is then we are good and add the score 
					// if it isn't then reassign current spot to old value
					if(isCompatible(board, new myPoint(p.x, p.y), redefine[i]))
					{
						redefinedSpaces.add(new myPoint(p.x, p.y));
						break;
					}
					else
						board[p.x][p.y] = temp;

				}
			}


		}

		else 
		{	
			board[coordinate.x][coordinate.y] = c == '-' ? '+' : '-';
			redefinedSpaces.add(new myPoint(coordinate.x, coordinate.y));
		}


		// get all the locations around coordinate, that are also a '*' grab the first one and change it's value
		// to a compatible one


		return redefinedSpaces;
	}

	/**
	 * Method to determine if the free space that was redefine is 
	 * compatible with the rest of the board
	 * @param board = 2 dimensional character array representing the board
	 * @param coordinate = a point that represents the current point we are at
	 * 									  in the board
	 * @param define = the character to redefine board with
	 * @return
	 * 				= true if the redefinition is compatible
	 * 				= false otherwise
	 */
	public static boolean isCompatible(char[][] board, myPoint coordinate, char redefine)
	{
		char current = redefine;
		board[coordinate.x][coordinate.y] = redefine;

		// determine if the new value of coordinate is compatible with the rest of the board
		if (coordinate.y > 0)
		{
			if (board[coordinate.x][coordinate.y - 1] == current)
				return false;
		}

		//redefineAreaAroundHomeSpace(board, new Point(coordinate.x, coordinate.y - 1), redefine);

		// redefine location to the right of coordinate
		if (coordinate.y < board[0].length - 1)
		{
			if(board[coordinate.x][coordinate.y + 1] == current)
				return false;
		}

		// redefine location directly above coordinate
		if (coordinate.x == 1)
		{	
			if(board[0][coordinate.y] == current)
				return false;
		}

		// redefine location directly below coordinate
		if (coordinate.x == 0)
		{	
			if(board[1][coordinate.y] == current)
				return false;
		}

		return true;
	}

	/**
	 * Method to find an adjacent compatible space on the board
	 * @param  board = 2 dimensional character array representing the board
	 * @param coordinate = a point that represents the current point we are at
	 * 									  in the board
	 * @return
	 * 				= a point object that is adjacent to coordinate and compatible with
	 * 				   coordinate
	 */
	public static myPoint findAdjacent(char[][] board, myPoint coordinate, boolean[][] populated)
	{
		char compatible = board[coordinate.x][coordinate.y] == '+' ? '-' : '+';

		

		// return the first compatible, adjacent value we find based on priority
		// bottom -> top -> right -> left
		
		// bottom
		if (coordinate.x == 0 && (board[1][coordinate.y] == compatible
				|| isFreeSpaceUsable(board, new myPoint (1, coordinate.y))))
					if (populated[1][coordinate.y] == false)
							return new myPoint (1, coordinate.y);
		
		// top 
		if (coordinate.x == 1 && (board[0][coordinate.y] == compatible
				|| isFreeSpaceUsable(board, new myPoint (0, coordinate.y))))
					if (populated[0][coordinate.y] == false)
							return new myPoint (0, coordinate.y);
		
		// right
		if (coordinate.y < board[0].length - 1 && (board[coordinate.x][coordinate.y + 1] == compatible
				|| isFreeSpaceUsable(board, new myPoint(coordinate.x, coordinate.y + 1))))
					if (populated[coordinate.x][coordinate.y + 1] == false)
							return new myPoint(coordinate.x,coordinate.y + 1);
		
		// left
		if (coordinate.y > 0 && (board[coordinate.x][coordinate.y - 1] == compatible
				|| isFreeSpaceUsable(board, new myPoint(coordinate.x, coordinate.y - 1))))
					 if (populated[coordinate.x][coordinate.y - 1] == false)
						 	return new myPoint(coordinate.x, coordinate.y - 1);

		


		return null;		
	}


	/**
	 * Method to find an adjacent compatible space on the board
	 * @param  board = 2 dimensional character array representing the board
	 * @param coordinate = a point that represents the current point we are at
	 * 									  in the board
	 * @return
	 * 				= true if coordinate is a free space && it can be made to be 
	 * 				   a '+' or '-' and maintain compatibility with the board
	 */
	public static boolean isFreeSpaceUsable(char[][] board, myPoint coordinate)
	{

		if (board[coordinate.x][coordinate.y] == '*')
		{
			// change current free space to a '+' or '-' so we can find an adjacent value
			if (isCompatible(board, coordinate, '+'))
				return true; // coordinate value is now a '+'
			else if (isCompatible(board, coordinate, '-'))
				return true; // coordinate value is now a '-'
			else 
			{
				// nothing will be compatible so this coordinate can't be used
				board[coordinate.x][coordinate.y] = '*';
				return false;
			}
		}


		return false;
	}


	/**
	 * Method to determine the locations of all indices that start with
	 * '+' and '-' which I call home occurrences representing their locations 
	 * @param board = the 2d board that we are placing magnets on
	 * @return
	 * 				= a list of point objects where each index will hold a point 
	 * 					corresponding to a home index 
	 */
	public static List<myPoint> findHomeOccurrences(char[][] board)
	{
		List<myPoint> home = new ArrayList<myPoint>();

		for(int i = 0; i < 2; i++)
		{
			for (int j = 0; j < board[i].length; j++)
			{
				if (board[i][j] == '+' || board[i][j]== '-')
					home.add(new myPoint(i, j));
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
	public static List<myPoint> findFreeSpaceOccurrences(char[][] board)
	{
		List<myPoint> freeSpace = new ArrayList<myPoint>();

		for(int i = 0; i < 2; i++)
		{
			for (int j = 0; j < board[i].length; j++)
			{
				if (board[i][j] == '*' )
					freeSpace.add(new myPoint(i, j));
			}
		}

		return freeSpace;
	}


	/**
	 * Method to group the dominos in the format needed to output
	 * @param board = 2d array representing the board to place magnets on
	 * @param magnets = the number of magnets to put on the board
	 * @param spaces = comprehensive list of coordinates to place the magnets on
	 * @return
	 * 				= a list of dominos
	 */
	public static List<Domino> createDominos(char[][] board, int magnets, List<myPoint> spaces)
	{
		List<Domino> dominos = new ArrayList<Domino>();
		Domino dom = new Domino();
		myPoint tempPoint = null;
		boolean[][] populated = new boolean[2][board[0].length];

		Set<myPoint> tempSpaces = new TreeSet<myPoint>();

		// make the list of points into an ordered tree set
		for (myPoint p : spaces)
			tempSpaces.add(new myPoint(p));

		// create an iterator to traverse the set
		Iterator<myPoint> setItr = tempSpaces.iterator();
		char placeHolder = 48;

		while (setItr.hasNext())
		{
			tempPoint = setItr.next();

			// get the positive side of the domino
			dom.pos = board[tempPoint.x][tempPoint.y] == '+' 
							? tempPoint
							: findAdjacent(board, tempPoint, populated);

			// is dom.pos a valid value
			if (dom.pos != null) 
			{
				// get the negative side of the domino given a valid positive side of the domino
				if(dom.pos.equals(tempPoint))
					dom.neg = findAdjacent(board, tempPoint, populated);

				else //if (dom.pos != null)
					dom.neg = tempPoint;
			}

			else 
			{
			// get the negative side of the domino given an invalid positive side
			dom.neg = board[tempPoint.x][tempPoint.y] == '-' 
							? tempPoint
							: findAdjacent(board, tempPoint, populated);
			
			// is dom.neg a valid value
			if (dom.neg != null)
			{
				// get the positive side of the domino given a valid negative side of the domino
				if(dom.neg.equals(tempPoint))
					dom.pos = findAdjacent(board, tempPoint, populated);
				else
					dom.pos = tempPoint;
			}
			
			}

			// add the new domino if neither side is null
			if (dom.neg != null && dom.pos != null
					&& populated[dom.neg.x][dom.neg.y] == false
					&& populated[dom.pos.x][dom.pos.y] == false)
			{
				// add new domino to the list of dominos
				dominos.add(dom);

				// change the location on the board (can get rid of after testing)
//				board[dom.pos.x][dom.pos.y] = placeHolder;
//				board[dom.neg.x][dom.neg.y] = placeHolder;
//				populated[dom.pos.x][dom.pos.y] = true;
//				populated[dom.neg.x][dom.neg.y] = true;
//				placeHolder++;
//				System.out.print("\n\n");
//				printBoard(board);
			}

			// remove what we just processed
			if (dom.pos != null)
				tempSpaces.remove(dom.pos);
			
			if (dom.neg != null)
				tempSpaces.remove(dom.neg);

			// reset domino 
			dom = new Domino();

			// reset iterator
			setItr = tempSpaces.iterator();

			if (dominos.size() >= magnets)
				return dominos;

		}

		// get the furthest point out (largest y value)
		Object[] arr = tempSpaces.toArray();
		myPoint marker = (myPoint) arr[arr.length - 1];
		arr = null;

		// reset tempSpaces
		Set<myPoint> rest = new TreeSet<myPoint>();

		// create a data structure of all remaining points 
		for (myPoint p : tempSpaces)
		{
			if (											//p.y <= marker.y 
					//	&& 
					(board[p.x][p.y] ==  '-' || board[p.x][p.y] == '+' || board[p.x][p.y] == '*')	) 
				rest.add(p);

		}

		// reset iterator
		setItr = rest.iterator();

		while (dominos.size() < magnets)
		{
			// grab a positive, then grab an adjacent negative
			// (we can use isCompatible or redefine free spaces type logic to find spaces
			// that will actually work)
			// once we have both positive and negative create a domino object
			// and insert into dominos
			//System.out.print("Sorry son you failed!!!");
			// we can speed this up by creating a full list of points in the placeMagnets
			// method to complete do the same as we did for the second loop with the first
			// except we must change the return type of the redefineAreaAroundFreeSpaces
			// method to support all points changed (spaces will increase by the size of the point list)

			//	findAdjacent

			while (setItr.hasNext())
			{
				tempPoint = setItr.next();
				char c  = '\0';

				// is the free space compatible
				if (isCompatible(board, tempPoint, '+'))
					c = '+';
				else if (isCompatible(board, tempPoint, '-'))
					c = '-';
				else 
				{
					board[tempPoint.x][tempPoint.y] = '*';
					continue;
				}

				// get the positive side of the domino
				dom.pos = tempPoint; // board[tempPoint.x][tempPoint.y] == c
				//						? tempPoint
				//								: findAdjacent(board, tempPoint, populated);

				// get the positive side of the domino
				if (dom.pos == null)
					continue;

				// get the negative side of the domino	
				if(dom.pos.equals(tempPoint))
					dom.neg = findAdjacent(board, tempPoint, populated);

				else
					dom.neg = tempPoint;

				// add the new domino if neither side is null
				if (dom.neg != null && dom.pos != null
						&& populated[dom.neg.x][dom.neg.y] == false
						&& populated[dom.pos.x][dom.pos.y] == false)
				{
					// add new domino to the list of dominos
					dominos.add(dom);

					// change the location on the board (can get rid of after testing)
					board[dom.pos.x][dom.pos.y] = placeHolder;
					board[dom.neg.x][dom.neg.y] = placeHolder;
					populated[dom.pos.x][dom.pos.y] = true;
					populated[dom.neg.x][dom.neg.y] = true;
					placeHolder++;
					System.out.print("\n\n");
					printBoard(board);
				}

				// reset domino 
				dom = new Domino();


				// remove element I just processed
				tempSpaces.remove(dom.pos);
				tempSpaces.remove(dom.neg);

				// reset iterator
				setItr = tempSpaces.iterator();

				if (dominos.size() >= magnets)
					break;

			}


		}

		return dominos;
	}

	public static void main(String[] args)
	{
		boardNMagnets bnm = readInput("input.txt");
		List<myPoint> spaces = placeMagnets(bnm.board, bnm.magnets);

		List<Domino> dominos = createDominos(bnm.board, bnm.magnets, spaces);

		writeOutput("output.txt", dominos);
		
		System.out.print("\n\n");
		printBoard(bnm.board);


		//HashMap<myPoint, Character> hm = new HashMap<myPoint, Character>();
	}

}
