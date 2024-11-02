package main;

//Necessary imports
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Main class
public class Main {

	//List of specific positions where starting holes could be, up to symmetry. Used in populating all
	//permutations of a certain board
	//Important - the USER needs to set these for each change in size of board to analyze.
	public static int[][] setHoles = {{0,0}, {0, 1}, {0, 2}, {1, 2}};
	
	//List of all boards to solve
	public static List<int[]> boards = new ArrayList<int[]>();
	
	
	
	
	//Home base for the algorithm.
	//All accessible modifications (board size, board position) are to be made from this method
	//More documentation can be found in the README
	public static void main(String[] args) throws IOException {
		//Tracks the elapsed time since initialization.
		long startTime = System.nanoTime();

		//TRUE = generate a solution path for ALL boards for the given size in 3 colors.
		//FALSE = don't do that. <------- Default
		boolean solveAllBoards = false;
		
		//Used for solving individual boards.
		if (!solveAllBoards) {
			
			// Replace the board initialization with your starting board configuration
			// Triangle is stored from the left, which each successive row containing one more element than the last
			int[][] boardTemp = {
					{0, 0, 0, 0, 0},
					{1, 1, 0, 0, 0},
					{1, 1, 1, 0, 0},
					{1, 1, 1, 1, 0},
					{1, 1, 1, 1, 1}
			};
			
			//board, colors, filename, animate in console, output to file, restrict to only jumps with form (n-1), computeVector
			MulticolorSolver solve = new MulticolorSolver(boardTemp, 5, "singularSolutions.txt", true, false, false, false);
			

		}
		
		
		
		
		//Used for solving all boards of a certain size in three colors.
		if (solveAllBoards) {
			
			
			//Where the user sets the size of board to solve in three colors.
			int boardSize = 5;
			
			//Determining the number of elements based on the size of the board.
			//Used in generating all permutations of 3 color board elements.
			int numElements = 0;
			for (int x = 1; x <= boardSize; x++) {
				numElements += x;
			}
			
			//Generates all permutations of the board of a given size.
			generateBoards(numElements - 1);
			
			//Solves all boards for each given starting vacancy.
			for (int[] hole : setHoles) {
				
				for (int[] board : boards) {
					
					int[][] newBoard = populateBoard(boardSize, board, hole);
					
					//replace "solutions.txt" with the desired filename for solutions.
					MultiboardSolver m = new MultiboardSolver(newBoard, "solutions.txt");
					
				}
			
			}
		}
		
		//Captures elapsed time for computation.
		long endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime) + " ns"); 

	}
	
	
	
	
	
	//Generates all possible permutations of a board for a given size.
	//Each iteration, a board of 1's has a single 2 added to the front of the array
	//This 2 replaces the first 1 it encounters.
	//Then, all lexicographical permutations of the elements are appended to the list of possible boards.
	public static void generateBoards(int size) {
		
		int[] board = new int[size];
		
		Arrays.fill(board,1);
		
		for(int i = size - 1; i >= 0; i--) {
			board[i] = 2;
			int[] temp = Arrays.copyOf(board, board.length);
			do {
				boards.add(Arrays.copyOf(temp, temp.length));
			} while (permuteLexically(temp));
			
		}
		
		
	}
	
	//Generates the next lexicographical permutation of an array of data (in this case, board elements)
	//Each time this function is called, the 2's will be in the front of the array.
	//Thus, a lexicographical permutation will give us all permutations for the given elements
	public static boolean permuteLexically(int[] data) {
		int k = data.length - 2;
		while (data[k] >= data[k+1]) {
			k--;
			if (k < 0) {
				return false;
			}
		}
		
		int l = data.length - 1;
		while (data[k] >= data[l]) {
			l--;
		}
		swap(data, k, l);
		int length = data.length - (k+1);
		for (int i = 0; i < length / 2; i++) {
			swap(data, k + 1 + i, data.length - i - 1);
		}
		return true;
	}
	
	
	//Helper method to swap the position of two elements
	public static void swap(int[] data, int x, int y) {
		int temp = data[x];
		data[x] = data[y];
		data[y] = temp;
	}
	
	
	//Helper method to populate a board, from a 1D array of values to a 2D array of a board.
	//Takes in the size of the board, the 1D representation of the board elements, and the 
	//starting vacancy hole.
	public static int[][] populateBoard(int size, int[] oneD, int[] hole) {
		int[][] board = new int[size][size];
		int incrementor = 0;
		for (int i = 0; i < board.length; i++) {
        	
            for (int j = 0; j <= i; j++) {
            	if (j == hole[0] && i == hole[1]) {
            		board[i][j] = 0;
            	} else {
            		board[i][j] = oneD[incrementor];
                	incrementor++;
            	}
            	
            }
            
        }
		
		
		return board;
	}
	
}
