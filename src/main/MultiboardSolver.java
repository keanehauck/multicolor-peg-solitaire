package main;

//Necessary imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


//Class containing the necessary algorithm for solving all permutations of boards in 3 colors.
//This is streamlined as compared to MulticolorSolver:
//			Explicit coordinate logic
//			Explicit array storage of moves for less object overhead
//			Removed unnecessary methods.
//Methods work similarly to their counterparts in MulticolorSolver, so detailed explanations are not included.
public class MultiboardSolver {

	// Replace the board initialization with your starting board configuration
	// Triangle is stored from the left, which each successive row containing one more element than the last
	public int[][] board = {
			{0, 0, 0, 0, 0},
            {1, 1, 0, 0, 0},
            {1, 2, 1, 0, 0},
            {1, 1, 1, 1, 0},
            {1, 1, 1, 1, 1}
        };

	public List<int[][]> previousBoards = new ArrayList<int[][]>();
	
	private boolean solved = false;
	
	private String fileName;
	
    public MultiboardSolver(int[][] pass, String file) throws IOException {
    	board = pass;
    	fileName = file;
    	filePrintBoard(board);
    	
    	List<List<int[]>> solution = initializeSolver();
    	
    	if (solution != null) {
    		filePrintSolution(solution);
    	} else {
    		filePrintSolution(null);
    	}
    	
    }

    public List<List<int[]>> initializeSolver() {
    	
    	int[][] boardCopy = new int[board.length][board.length];
    	for(int x = 0; x < board.length; x++) {
    		for (int y = 0; y < board.length; y++) {
    			boardCopy[x][y] = board[x][y];
    		}
    	}
    	
    	List<List<int[]>> solution = recursiveSolve(boardCopy);
    	
    	
    	
    	
    	return solution;
    }
    
    
    public List<List<int[]>> recursiveSolve(int[][] board) {
    	previousBoards.add(copyBoard(board));
    	List<List<int[]>> path = new ArrayList<>();
    	
    	if (!anyPegsLeft(board)) {
    		solved = true;
    		return path;
    	}
    	
    	
    	for (int i = 0; i < board.length; i++) {
            for (int j = 0; j <= i; j++) {
                if (board[i][j] != 0) {
                	int[] c1 = {j-1, i-1};
                	int[] c2 = {j, i-1};
                	int[] c3 = {j + 1, i};
                	int[] c4 = {j+1, i+1};
                	int[] c5 = {j, i+1};
                	int[] c6 = {j-1, i};
                	int[] jumped = {j,i};
                	
                	//begin jump checks
                	if (isValidMove(board, c1, jumped, c4)) {
                		
                		int sStart = board[c1[1]][c1[0]];
                		int sJumped = board[jumped[1]][jumped[0]];
                		int sEnd = board[c4[1]][c4[0]];
                		
                		applyMove(board, c1, jumped, c4);
                		List<int[]> temp = new ArrayList<>();
                		temp.add(c1);
                		temp.add(jumped);
                		temp.add(c4);
                		path.add(temp);
                		
                		
                		
                		if (containsCopy(board) == false) {
                			List<List<int[]>> subPath = recursiveSolve(board);
                    		
                			if (subPath != null) {
                    			pathAdd(path, subPath);
                    			return path;
                    		}
                			
                		}
                		
                		
                		// Undo the move if no solution found in the subpath
                		
                  		path.remove(path.size() - 1);
                		board[c1[1]][c1[0]] = sStart;
                		board[jumped[1]][jumped[0]] = sJumped;
                		board[c4[1]][c4[0]] = sEnd;
                		
                	}
                	
                	
                	
                	if (isValidMove(board, c2, jumped, c5)) {
                		
                		int sStart = board[c2[1]][c2[0]];
                		int sJumped = board[jumped[1]][jumped[0]];
                		int sEnd = board[c5[1]][c5[0]];
                		
                		applyMove(board, c2, jumped, c5);
                		List<int[]> temp = new ArrayList<>();
                		temp.add(c2);
                		temp.add(jumped);
                		temp.add(c5);
                		path.add(temp);
                		
                		if (containsCopy(board) == false) {
                			List<List<int[]>> subPath = recursiveSolve(board);
                    		
                			if (subPath != null) {
                    			pathAdd(path, subPath);
                    			return path;
                    		}
                		}
                		
                		// Undo the move if no solution found in the subpath
                		
                		path.remove(path.size() - 1);
                		board[c2[1]][c2[0]] = sStart;
                		board[jumped[1]][jumped[0]] = sJumped;
                		board[c5[1]][c5[0]] = sEnd;
                		
                	}
                	
                	
                	if (isValidMove(board, c3, jumped, c6)) {
                		//System.out.println("Maybe?");
                		int sStart = board[c3[1]][c3[0]];
                		int sJumped = board[jumped[1]][jumped[0]];
                		int sEnd = board[c6[1]][c6[0]];
                		
                		applyMove(board, c3, jumped, c6);
                		List<int[]> temp = new ArrayList<>();
                		temp.add(c3);
                		temp.add(jumped);
                		temp.add(c6);
                		path.add(temp);
                		
                		if (containsCopy(board) == false) {
                			List<List<int[]>> subPath = recursiveSolve(board);
                    		
                			if (subPath != null) {
                    			pathAdd(path, subPath);
                    			return path;
                    		}
                		}
                		
                		
                		// Undo the move if no solution found in the subpath
                		
                		path.remove(path.size() - 1);
                		board[c3[1]][c3[0]] = sStart;
                		board[jumped[1]][jumped[0]] = sJumped;
                		board[c6[1]][c6[0]] = sEnd;
                		
                	}
                	
                	if (isValidMove(board, c4, jumped, c1)) {
                		
                		int sStart = board[c4[1]][c4[0]];
                		int sJumped = board[jumped[1]][jumped[0]];
                		int sEnd = board[c1[1]][c1[0]];
                		
                		applyMove(board, c4, jumped, c1);
                		List<int[]> temp = new ArrayList<>();
                		temp.add(c4);
                		temp.add(jumped);
                		temp.add(c1);
                		path.add(temp);
                		
                		if (containsCopy(board) == false) {
                			List<List<int[]>> subPath = recursiveSolve(board);
                    		
                			if (subPath != null) {
                    			pathAdd(path, subPath);
                    			return path;
                    		}
                		}
                		                		
                		// Undo the move if no solution found in the subpath
                		
                		path.remove(path.size() - 1);
                		board[c4[1]][c4[0]] = sStart;
                		board[jumped[1]][jumped[0]] = sJumped;
                		board[c1[1]][c1[0]] = sEnd;
                		
                	}
                	
                	if (isValidMove(board, c5, jumped, c2)) {
                		
                		int sStart = board[c5[1]][c5[0]];
                		int sJumped = board[jumped[1]][jumped[0]];
                		int sEnd = board[c2[1]][c2[0]];
                		
                		applyMove(board, c5, jumped, c2);
                		List<int[]> temp = new ArrayList<>();
                		temp.add(c5);
                		temp.add(jumped);
                		temp.add(c2);
                		path.add(temp);
                		
                		if (containsCopy(board) == false) {
                			List<List<int[]>> subPath = recursiveSolve(board);
                    		
                			if (subPath != null) {
                    			pathAdd(path, subPath);
                    			return path;
                    		}
                		}
                		
                		// Undo the move if no solution found in the subpath
                		
                		path.remove(path.size() - 1);
                		board[c5[1]][c5[0]] = sStart;
                		board[jumped[1]][jumped[0]] = sJumped;
                		board[c2[1]][c2[0]] = sEnd;
                		
                	}
                	
                	
                	if (isValidMove(board, c6, jumped, c3)) {
                		
                		int sStart = board[c6[1]][c6[0]];
                		int sJumped = board[jumped[1]][jumped[0]];
                		int sEnd = board[c3[1]][c3[0]];
                		
                		applyMove(board, c6, jumped, c3);
                		List<int[]> temp = new ArrayList<>();
                		temp.add(c6);
                		temp.add(jumped);
                		temp.add(c3);
                		path.add(temp);
                		
                		if (containsCopy(board) == false) {
                			List<List<int[]>> subPath = recursiveSolve(board);
                    		
                			if (subPath != null) {
                    			pathAdd(path, subPath);
                    			return path;
                    		}
                		}
                		
                		
                		// Undo the move if no solution found in the subpath
                		
                		path.remove(path.size() - 1);
                		board[c6[1]][c6[0]] = sStart;
                		board[jumped[1]][jumped[0]] = sJumped;
                		board[c3[1]][c3[0]] = sEnd;
                		
                	}
                	
                    // Similar checks for other possible moves...
                }
            }
        }

    	//No solution path found.
        return null;
    	
    	
    }
    
    
    

    //Helper method, checks if position is on board
    public boolean isOnBoard(int board[][], int x, int y) {
    	if (y >= 0 && y < board.length && x >= 0 && x <= y) {
    		return true;
    	}
    	
    	return false;
    }
    
    //passed arrays of the positions
    public boolean isValidMove(int board[][], int[] start, int[] jumped, int[] end) {
    	//makes sure that a move is all on the board
    	
    	if (isOnBoard(board, start[0], start[1]) && isOnBoard(board, jumped[0], jumped[1]) && isOnBoard(board, end[0], end[1])) {
    		
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
    	
    	if (board[coord[1]][coord[0]] == 1 || board[coord[1]][coord[0]] == 2) {
    		return true;
    	}
    	return false;
    }
    
    //this is bugging out
    public void applyMove(int[][] board, int[] start, int[] jumped, int[] end) {
    	
    	
    	board[end[1]][end[0]] = board[start[1]][start[0]];
    	board[start[1]][start[0]] = 0;
    	
    	board[jumped[1]][jumped[0]] = (board[end[1]][end[0]] + board[jumped[1]][jumped[0]]) % 3;
    	
    	
    }
    
    
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
    
    
    public void filePrintSolution(List<List<int[]>> solution) throws IOException {
    	BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
    	
    	if(solution == null) {
    		writer.write("No solution found.");
    		writer.newLine();
    		writer.close();
    		return;
    	}
    	
    	writer.write("Solution found.");
    	writer.newLine();
    	
    	
    	
    	if (solution != null) {
    		for (List<int[]> x : solution) {
        		writer.write("[");
        		for (int[] y : x) {
        			writer.write("(");
        			for(int z : y) {
        				writer.write(z + ", ");
        			
        			}
        			writer.write(")");
        		}
        		writer.write("]");
        		writer.newLine();
        	}
    	}
    	
    	writer.write("End of this one.");
    	writer.newLine();
    	writer.close();
    }
    
    public void pathAdd(List<List<int[]>> path, List<List<int[]>> subPath) {
    	
    	for(List<int[]> temp : subPath) {
    		path.add(temp);
    	}
    	
    	
    }
    
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
    
    public boolean containsCopy(int[][] board) {
    	boolean contains = false;
    	for (int[][] x : previousBoards) {
    		if (Arrays.deepEquals(x, board) == true) {
    			contains = true;
    		}
    	}
    	
    	return contains;
    }
    
    public int[][] copyBoard(int[][] board) {
    	int [][] myInt = new int[board.length][];
    	for(int i = 0; i < board.length; i++)
    	    myInt[i] = board[i].clone();
    	
    	
    	return myInt;
    }
    public String generateCodeLookup() {
    	String s = "";
    	for (int i = 0; i < board.length; i++) {
        	
            for (int j = 0; j <= i; j++) {
            	s = s+Integer.toString(board[i][j]);
            	
            }
            
        }
    	return s;
    	
    }
}