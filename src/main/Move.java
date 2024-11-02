package main;

//Class defined to describe a given move on a board.
public class Move {
    int[] start;
    int[] jumped;
    int[] end;

    //Parameterized constructor.
    //Takes in the coordinates for the starting peg, the peg jumped over, and the ending peg.
    //Jumps represented as array of integers.
    //Technically, we could represent the jumps with only 2 of the 3 coordinates, but having all three
    //in the constructor allows for easier bug testing.
    public Move(int[] start, int[] jumped, int[] end) {
        this.start = start;
        this.jumped = jumped;
        this.end = end;
    }
}