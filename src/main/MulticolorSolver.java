package main;

//Necessary imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


//Class containing the solving algorithm for arbitrary size and color boards.
//You may notice that many of the methods are passed something that is otherwise a Class-level variable (board)
//This is so that certain methods could be accessed for external boards, for added functionality.
public class MulticolorSolver {

	//2D representation of board
	private int[][] board;
	
	//Variable that tracks the level of recursion. Used for debugging.
	private int recursionLevel;
	
	//Tablebase of boards that have previously been determined unsolvable.
	//Generated at runtime.
	public static List<int[][]> previousBoards = new ArrayList<int[][]>();
	
	//Number of colors on the board
	private int numColors;
	
	//Name of file to output to
	private String fileName;
	
	//Animate in console?
	private boolean animate;
	
	//Output to file?
	private boolean output;
	
	//Special boolean to restrict gameplay to solely jumps of the form 110 or 1(n-1)0 (up to symmetry).
	//Used for assessing the performance of certain solution paths on boards of >4 colors.
	private boolean restrictToNJumps;
	
	//Boolean tracking whether a solution path has been found.
	private boolean solved = false;
	
	//Boolean to determine whether to output the parity-vector representation of the board*
	//As described in Bell (2008).
	private boolean computeVector;
	
	//Move array used in the recursiveSolve method. Each entry represents a jump in a different
	//orientation around a central peg; e.g., top-left to bottom-right, top-right to bottom-left, etc.
	private static final Move[] MOVES = {
		    new Move(new int[]{-1, -1}, new int[]{0, 0}, new int[]{1, 1}),
		    new Move(new int[]{0, -1}, new int[]{0, 0}, new int[]{0, 1}),
		    new Move(new int[]{1, 0}, new int[]{0, 0}, new int[]{-1, 0}),
		    new Move(new int[]{1, 1}, new int[]{0, 0}, new int[]{-1, -1}),
		    new Move(new int[]{0, 1}, new int[]{0, 0}, new int[]{0, -1}),
		    new Move(new int[]{-1, 0}, new int[]{0, 0}, new int[]{1, 0})
		};
	
	
	
	
	//Parameterized constructor
    public MulticolorSolver(int[][] board, int numColors, String fileName, boolean animate, boolean output, boolean restrictToNJumps, boolean computeVector) throws IOException {
    	this.board = board;
    	this.numColors = numColors;
    	this.fileName = fileName;
    	this.animate = animate;
    	this.output = output;
    	this.restrictToNJumps = restrictToNJumps;
    	this.computeVector = computeVector;
    	
    	if (output) {
        	filePrintBoard(board);
    	}
    	
    	if (animate) {
        	System.out.println("Beginning solution of board.");
    	}
    	
    	//Our solution List, if one is found, consists of moves that when played will solve the board.
    	//Each move is in the format 
    	List<Move> solution = initializeSolver();
    	
    	
    	if (solution != null) {
    		System.out.println("Solution found");
    		
    		if (output) {
        		filePrintSolution(solution);  
    		}
    		
        	if(animate) {
        		printBoard(board);
        		animateSolution(solution, board);
        		printSolution(solution, board);
        	}
    		
    		
    	} else {
    		if (output) {
    			filePrintSolution(null);
    		}
    		//printBoard(board);
    		if(animate) {
    			System.out.println("No solution found.");
    		}
    		
    	}
    	
    	
    	
    	
    }

    //Where it all begins.
    //Method creates a copy of the original board to prevent passed-by-reference shenanigans.
    //Begins the recursive solution algorithm.
    public List<Move> initializeSolver() {
    	
    	int[][] boardCopy = new int[board.length][board.length];
    	for(int x = 0; x < board.length; x++) {
    		for (int y = 0; y < board.length; y++) {
    			boardCopy[x][y] = board[x][y];
    		}
    	}
    	
    	List<Move> solution = recursiveSolve(boardCopy);
    	
    	return solution;
    }
    
    //Recursive solution to check for a solution of a board.
    //Algorithm description:
    //		Add the current board to the list of previously-searched boards.
    //		Begin search of current board.
    //		Create a new list for a potential path to solution on the board.
    //		If board is currently in solved state, return path.
    //		Check all valid moves on the board.
    //		For each valid move, check if the board has previously been searched, and if so, return.
    //		For each valid move, if the board is novel, go one level deeper in recursion and repeat the process.
    //		If no valid moves, return unsolvable board and remove move from potential solution path.
    public List<Move> recursiveSolve(int[][] board) {
        
        previousBoards.add(copyBoard(board));
        recursionLevel++;
        List<Move> path = new ArrayList<>();

        if (!anyPegsLeft(board)) {
            solved = true;
            return path;
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j <= i; j++) {
                if (board[i][j] != 0) {
                    int[] jumped = {j, i};

                    for (Move move : MOVES) {
                        int[] start = new int[]{j + move.start[0], i + move.start[1]};
                        int[] end = new int[]{j + move.end[0], i + move.end[1]};

                        if (isValidMove(board, start, jumped, end)) {
                            int sStart = board[start[1]][start[0]];
                            int sJumped = board[jumped[1]][jumped[0]];
                            int sEnd = board[end[1]][end[0]];

                            applyMove(board, start, jumped, end);
                            Move temp = new Move(start, jumped, end);
                            path.add(temp);

                            if (!containsCopy(board)) {
                                List<Move> subPath = recursiveSolve(board);
                                if (subPath != null) {
                                    pathAdd(path, subPath);
                                    return path;
                                }
                            }

                            // Undo the move if no solution found in the subpath
                            path.remove(path.size() - 1);
                            board[start[1]][start[0]] = sStart;
                            board[jumped[1]][jumped[0]] = sJumped;
                            board[end[1]][end[0]] = sEnd;
                        }
                    }
                }
            }
        }

        recursionLevel--;
        return null;
    }
    

    //Helper method to check if a position is on a given board.
    //working as intended for variable board sizes, passed invididual coordinates
    public boolean isOnBoard(int board[][], int x, int y) {
    	if (y >= 0 && y < board.length && x >= 0 && x <= y) {
    		return true;
    	}
    	
    	return false;
    }
    
    //passed arrays of the positions, appears to be working......
    public boolean isValidMove(int board[][], int[] start, int[] jumped, int[] end) {
    	
    	//makes sure that a move is all on the board
    	
    	if (isOnBoard(board, start[0], start[1]) && isOnBoard(board, jumped[0], jumped[1]) && isOnBoard(board, end[0], end[1])) {
    		
    		//validates for testing
        	if (restrictToNJumps == true) {
        		if ((getColor(board, start) != 1 && getColor(board, jumped) != 1) && (getColor(board, start) == getColor(board, jumped))) {
        			return false;
        		}
        	}
    		
    		if ((isFilled(board, start) && !isFilled(board, end)) && (isFilled(board, jumped))) {
    			
    			
    			if ((start[0] == jumped[0]) && (end[0] == jumped[0])) {
    				//if they jump over the same x coordinates
    				if(((start[1] == jumped[1] - 1) && (end[1] == jumped[1] + 1)) || ((start[1] == jumped[1] + 1) && end[1] == jumped[1] - 1)) {
    					return true;
    					
    				}
    				
    			} else if ((start[1] == jumped[1]) && (end[1] == jumped[1])) {
    				//if they jump over the same y coord
    				if (((start[0] == jumped[0]-1) && (end[0] == jumped[0]+1)) || ((start[0] == jumped[0]+1 )&& end[0] == jumped[0] - 1)) {
    					
    					return true;
    				}
    				
    				
    			} else if ((start[0] == jumped[0] - 1) && (start[1] == jumped[1]-1) && (end[0] == jumped[0] + 1) && (end[1] == jumped[1] + 1) || (start[0] == jumped[0] + 1) && (start[1] == jumped[1]+1) && (end[0] == jumped[0] - 1) && (end[1] == jumped[1] - 1)) {
    				return true;
    			}
    			
    		}
    		
    	}
    	
    	
    	return false;
    }
    
    
    //helper method to check if a coordinate is filled
    public boolean isFilled(int[][] board, int[] coord) {
    	
    	if (board[coord[1]][coord[0]] == 0) {
    		return false;
    	}
    	return true;
    }
    
    //helper method to return num in a space
    public int getColor(int[][] board, int[] coord) {
    	return board[coord[1]][coord[0]];
    }
    
    //Helper method - applies move on board
    public void applyMove(int[][] board, int[] start, int[] jumped, int[] end) {
    	
    	
    	board[end[1]][end[0]] = board[start[1]][start[0]];
    	board[start[1]][start[0]] = 0;
    	
    	board[jumped[1]][jumped[0]] = (board[end[1]][end[0]] + board[jumped[1]][jumped[0]]) % numColors;
    	
    	
    }
    
    //Helper method to check if any pegs are still filled on the board other than the last one.
    public boolean anyPegsLeft(int[][] board) {
    	int pegCount = 0;
        for (int[] row : board) {
            for (int cell : row) {
                if (cell != 0) {
                    pegCount++;
                }
            }
        }
        
        if (pegCount <= 1) {
        	return false;
        }
        return true;
    }


    //Method to print the solution of a given board in console.
    public void printSolution(List<Move> solution, int[][] board) {
    	if (solution != null) {
    		for (Move x : solution) {
        		System.out.print("[");
        		System.out.print("(" + x.start[0] + ", " + x.start[1] + ")" + "(" + x.jumped[0] + ", " + x.jumped[1] + ")" + "(" + x.end[0] + ", " + x.end[1] + ")");
        		System.out.println("]");
        	}
    	}
    	
    	System.out.println("End of this one.");
    	
    }
    
    //Method to print solution of a given board in file.
    public void filePrintSolution(List<Move> solution) throws IOException {
    	BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
    	
    	if(solution == null) {
    		writer.write("no solution found.");
    		writer.newLine();
    		writer.close();
    		return;
    	}
    	
    	writer.write("solution found. colors = " + numColors);
    	writer.newLine();
    	
    	
    	
    	if (solution != null) {
    		for (Move x : solution) {
        		writer.write("[");
        		writer.write("(" + x.start[0] + ", " + x.start[1] + ")" + "(" + x.jumped[0] + ", " + x.jumped[1] + ")" + "(" + x.end[0] + ", " + x.end[1] + ")");
        		writer.write("]");

        		writer.newLine();
        		
        	}
    	}
    	
    	writer.write("End of this one.");
    	writer.newLine();
    	writer.close();
    }
    
    //Helper method to add a path to another path (used in recursive algorithm).
    public void pathAdd(List<Move> path, List<Move> subPath) {
    	
    	for(Move temp : subPath) {
    		path.add(temp);
    	}
    	
    	
    }
    
    //Helper method that prints a given board, provided as a 2D array.
    public void printBoard(int[][] board) {
        for (int i = 0; i < board.length; i++) {
        	
        	
        	int spacePad = board.length - 1;

        	while(spacePad > i) {
        		System.out.print(" ");
        		spacePad--;
        	}
        	
            for (int j = 0; j <= i; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    
    //Method to print a specific board to a file.
    public void filePrintBoard(int[][] board) throws IOException {
    	BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
    	writer.write(generateCodeLookup());
    	writer.newLine();
    	
        for (int i = 0; i < board.length; i++) {
        	
        	
        	int spacePad = board.length - 1;

        	while(spacePad > i) {
        		writer.write(" ");
        		spacePad--;
        	}
        	
            for (int j = 0; j <= i; j++) {
            	writer.write(board[i][j] + " ");
            }
            writer.newLine();
        }
        writer.newLine();
        writer.close();
    }
    
    
    //Helper method to animate the solution of the board.
    public void animateSolution(List<Move> solution, int[][] board) {
    	
    	//Goes through each move in the solution and actually applies it to the board in question
    	for(Move x : solution) {
    		if (true) {
    			applyMove(board, x.start, x.jumped, x.end);
        		printBoard(board);
        		if (computeVector) {
        			computeVector(board);
        		}
        		//System.out.println(saxCountForT5());
        		System.out.println();
        		System.out.println();
    		}
    	}
    }
    
    
    //Method to compute the pagoda function parity vector as described by Bell, 2008.
    public int computeVector(int[][] board) {
    	
    	int[][] labeling = new int[board[0].length][board.length];
    	int[] vectorMod = new int[3];
    	
    	//Sets up a board with skew coordinates (x,y) labeled (x+y) mod 3
    	for (int i = 0; i < board.length; i++) {
            for (int j = 0; j <= i; j++) {
            	labeling[i][j] = (i + j) % 3;
            }
    	}
    
    	//If there is a peg in each position, we add it to a vector storing the values.
    	for (int i = 0; i < board.length; i++) {
            for (int j = 0; j <= i; j++) {
            	vectorMod[labeling[i][j]] += board[i][j];
            }
    	}
    	
    	//The number of pegs is taken mod 2 to determine parity
    	vectorMod[0] %= 2;
    	vectorMod[1] %= 2;
    	vectorMod[2] %= 2;
    	
    	//A final vector is determined by taking the sum of each component mod 2
    	int[] finalVector = {
    			(vectorMod[1] + vectorMod[2]) % 2,
    			(vectorMod[0] + vectorMod[2]) % 2,
    			(vectorMod[0] + vectorMod[1]) % 2
    	};
    	
    	//Print the vector
    	//TODO - add file output functionality
    	System.out.println(Arrays.toString(finalVector));
    	return 0;
    }
    
    //Hard-coded function to determine the SAX count for a given board as described by Bell, 2008.
    //Non-generalizable functionality for larger boards and higher number of colors.
    public int saxCountForT5() {
    	int X = 0;
    	 if(board[0][0] != 0) {
    		 X++;
    	 }
    	 if(board[2][0] != 0) {
    		 X++;
    	 }
    	 if(board[2][2]!= 0) {
    		 X++;
    	 }
    	 if(board[4][0]!= 0) {
    		 X++;
    	 }
    	 if(board[4][2] != 0) {
    		 X++;
    	 }
    	 if(board[4][4] != 0) {
    		 X++;
    	 }
    	 
    	 int A = 0;
    	 if(board[2][1] != 0) {
    		 A++;
    	 }
    	 if(board[3][1] != 0) {
    		 A++;
    	 }
    	 if(board[3][2] != 0) {
    		 A++;
    	 }
    	 
    	int S = 0;
    	
    	int pegCount1 = 0;
    	if(board[1][0] != 0) {
   		 pegCount1++;
    	}
    	if(board[2][0] != 0) {
      		 pegCount1++;
       	}
    	if(board[3][0] != 0) {
      		 pegCount1++;
       	}
    	
    	if (pegCount1 >=2) {
    		S++;
    	}
    	int pegCount2 = 0;
    	if(board[4][1] != 0) {
   		 pegCount2++;
    	}
    	if(board[4][2] != 0) {
      		 pegCount2++;
       	}
    	if(board[4][3] != 0) {
      		 pegCount2++;
       	}
    	
    	if (pegCount2 >=2) {
    		S++;
    	}
    	
    	int pegCount3 = 0;
    	if(board[1][1] != 0) {
   		 pegCount3++;
    	}
    	if(board[2][2] != 0) {
      		 pegCount3++;
       	}
    	if(board[3][3] != 0) {
      		 pegCount3++;
       	}
    	
    	if (pegCount3 >=2) {
    		S++;
    	}
    	
    	return S + A - X;
    	
    }
    
    //Helper method to check if the 'previousBoards' array contains a copy of the board being assessed
    public boolean containsCopy(int[][] board) {
    	boolean contains = false;
    	for (int[][] x : previousBoards) {
    		if (Arrays.deepEquals(x, board) == true) {
    			contains = true;
    		}
    	}
    	
    	return contains;
    }
    
    //Helper method to easily return a distinct copy of a board
    //Helps get around passed-by-reference shenaningans
    public int[][] copyBoard(int[][] board) {
    	int [][] myInt = new int[board.length][];
    	for(int i = 0; i < board.length; i++)
    	    myInt[i] = board[i].clone();
    	
    	
    	return myInt;
    }
    
    //Helper method to reduce a board to a single string for lookup capabilities.
    //The string can be parsed as follows:
    //Create a triangle board from top to bottom, left to right, using the values in the string to fill positions.
    public String generateCodeLookup() {
    	String s = "";
    	for (int i = 0; i < board.length; i++) {	
            for (int j = 0; j <= i; j++) {
            	s = s+Integer.toString(board[i][j]);
            }
        }
    	return s;
    }
    
    //Helper method to print all previous searched boards.
    //Used mainly for debugging.
    public void printPrevious() {
    	System.out.println("\n\nBeginning print previous.");
    	for (int[][] x : previousBoards) {
    		printBoard(x);
    	}
    	System.out.println("\n\n\n\nFinished print previous.");
    }
    
}