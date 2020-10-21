import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
 * Last Edit: 10/20/2020
 * Date Finished: 10/20/2020
 * RESULT: 4/4 (100%)
 * 
 * Improvements to be made: 
 *  - fix redefineFreeSpaces to detect the character to place at a free space
 *    to allow the board to be maximally populated
 *  - try another algorithm which gets the home spaces, zig zags through to
 *    determine where magnets are placed and places magnets down as you go through it
 *   - document the time/space complexity
 *    
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
		 * Serial Version UID because we extended Point and Point is serializable
		 */
		private static final long serialVersionUID = 3504962621239841892L;

		// coordinate x will be vertical and y will be horizontal in board
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
	 * Method to write the results of creating the dominos to an
	 * output file
	 * @param outputName = the name of the output file (with the extension)
	 * @param dominos = the list of dominos to output
	 */
	public static void writeOutput(String outputName, List<Domino> dominos)
	{
		File outFile = new File(outputName);
		PrintWriter writer = null;

		try
		{
			writer = new PrintWriter(outFile);

			// print out the dominos
			for (Domino domino : dominos)
				writer.print(domino + "\n");


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
	 * 				= a boardNMagnets object which contains 
	 * 					the board and the number of magnets to place
	 * 					on it
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
	 * MAIN ALGORITHM
	 * Method to place the magnets in their proper place
	 * 
	 * Algorithm after writing it out:
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
	 * @param board = 2d array representing the board
	 * @param magnets = number of magnets we need to put on the board
	 * @return 
	 * 				= a list of myPoint objects that need to be used to create dominos from
	 */
	public static List<myPoint> placeMagnets(char[][] board, int magnets)
	{
		// these are the home occurrences where each point holds location to a home occurrence
		List<myPoint> home = findHomeOccurrences(board);

		// these are the spaces that have been added based on the below methods
		List<myPoint> additionalSpaces = new Vector<myPoint>();

		// number of spaces where we can validly place a magnet
		int spaces = home.size();

		// number of spaces required to place the number of magnets down
		int requiredSpaces = magnets * 2;

		// redefine spaces around current home space to be valid places to place a magnet
		for (int i = 0; i < home.size(); i++)
		{

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

		while (spaces < requiredSpaces && j < freeSpace.size())
		{
			// gives us the coordinate of current free space
			myPoint currFree = freeSpace.get(j);

			// list to hold all the temporary spaces that have been added
			Set<myPoint> temp = null;

			// can I define this space to be non free?
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
		// list of myPoint objects that have been redefined on the board
		List<myPoint> redefinedSpace = new Vector<myPoint>();

		// redefine location to the left of coordinate
		if (coordinate.y > 0)
		{
			// save current value
			char temp = board[coordinate.x][coordinate.y - 1];

			if (board[coordinate.x][coordinate.y - 1] == '*')
			{

				/* determine if the redefinition is compatible with the current board
					-/ if it is then we are goo so add to redefinedSpace list 
					-  if it isn't then reassign current spot to old value */
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
				/* determine if the redefinition is compatible with the current board
						- if it is then we are good so add to redefinedSpace list
						- if it isn't then reassign current spot to old value				*/
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
				/* determine if the redefinition is compatible with the current board
						- if it is then we are good so add to redefinedSpace list
						- if it isn't then reassign current spot to old value				*/
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
				/* determine if the redefinition is compatible with the current board
						- if it is then we are good so add to redefinedSpace list
						- if it isn't then reassign current spot to old value				*/
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
		// grab all the elements around coordinate
		List<Character> directions = new Vector<Character>();

		// this is what we will return to indicate how many spaces we have successfully redefined
		Set<myPoint> redefinedSpaces = new TreeSet<myPoint>();

		// temp list to store the free spaces around coordinate
		Set<myPoint> treeSet = new TreeSet<myPoint>();
		;
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
		;
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
				/* determine if the redefinition is compatible with the current board
						- if it is then we are good so add to redefinedSpace list
						- if it isn't then reassign current spot to old value				*/
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
					/* determine if the redefinition is compatible with the current board
						- if it is then we are good so add to redefinedSpace list
						- if it isn't then reassign current spot to old value				*/
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

		return redefinedSpaces;
	}

	/**
	 * Method to determine if the free space that was redefine is 
	 * compatible with the rest of the board
	 * @param board = 2 dimensional character array representing the board
	 * @param coordinate = a point that represents the current point we are at
	 * 									  in the board
	 * @param redefine = the character to redefine board with
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
	 * 									  in the boar
	 * @param populated = 2d array where true represents an index where a domino lies
	 * 									and a false means domino doesn't exist thered
	 * @return
	 * 				= a point object that is adjacent to coordinate and compatible with
	 * 				   coordinate
	 */
	public static myPoint findAdjacent(char[][] board, myPoint coordinate, boolean[][] populated)
	{
		char compatible = board[coordinate.x][coordinate.y] == '+' ? '-' : '+';



		// return the first compatible, adjacent value we find based on priority
		// bottom -> top -> right -> left

		//check adjacent location directly below
		if (coordinate.x == 0 && (board[1][coordinate.y] == compatible
				|| isFreeSpaceUsable(board, new myPoint (1, coordinate.y))))
			if (populated[1][coordinate.y] == false)
				return new myPoint (1, coordinate.y);

		//check adjacent location directly on top 
		if (coordinate.x == 1 && (board[0][coordinate.y] == compatible
				|| isFreeSpaceUsable(board, new myPoint (0, coordinate.y))))
			if (populated[0][coordinate.y] == false)
				return new myPoint (0, coordinate.y);

		//check adjacent location to the  right
		if (coordinate.y < board[0].length - 1 && (board[coordinate.x][coordinate.y + 1] == compatible
				|| isFreeSpaceUsable(board, new myPoint(coordinate.x, coordinate.y + 1))))
			if (populated[coordinate.x][coordinate.y + 1] == false)
				return new myPoint(coordinate.x,coordinate.y + 1);

		//check adjacent location to the  left
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
				board[dom.pos.x][dom.pos.y] = placeHolder;
				board[dom.neg.x][dom.neg.y] = placeHolder;
				populated[dom.pos.x][dom.pos.y] = true;
				populated[dom.neg.x][dom.neg.y] = true;
				placeHolder++;
				System.out.print("\n\n----Stage " + placeHolder + "\nA domino has been added" );
				printBoard(board);
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

		// if it gets here there is an error in how many magnets you placed on the board (can't put that many)
		if (dominos.size() < magnets)
			System.out.print("Based off the configuration of the board, you can"
					+ " only place down " + dominos.size() + " magnets");

		return dominos;
	}

	public static void main(String[] args)
	{
		boardNMagnets bnm = readInput("input.txt");
		
		System.out.print("\n\n-----Starting Board----");
		printBoard(bnm.board);
		
		List<myPoint> spaces = placeMagnets(bnm.board, bnm.magnets);

		System.out.print("\n\n-----Board after determining possible spots for magnets ----");
		printBoard(bnm.board);

		List<Domino> dominos = createDominos(bnm.board, bnm.magnets, spaces);

		writeOutput("output.txt", dominos);

		System.out.print("\n\n-----Finished Board");
		printBoard(bnm.board);

	}

}
