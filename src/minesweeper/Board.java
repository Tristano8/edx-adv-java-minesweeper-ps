/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A board object representing a grid of squares. Each square is either flagged, dug, or untouched.
 * Each square also either contains a bomb, or does not contain a bomb - this is unknown to the player.
 * A dug square will indicate how many untouched bomb squares it borders (orthagonally and diagonally).
 */
public class Board {
    
    private final Map<Point, Square> grid;
    private final int MAXCOLUMN;
    private final int MAXROW;
    
    private static final int[][] adjacencies = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{-1,1},{1,-1},{-1,-1}};
    //
    // Abstraction function:
    //  A board mapping containing x * y Square objects, each of which is a value mapping at a unique point P,
    // where p = (x,y).
    //
    //
    // rep invariant:  0 <= MAXCOLUMN
    //                 0 <= MAXROW
    //                 boardState size == MAXCOLUMN*MAXROW
    //
    // rep exposure: Fields are private and final. Mutable boardState contains immutable Square objects
    // that are updated in place.
    //
    // thread safety: This datatype is threadsafe:
    // - Uses ConcurrentMap data structure for board state, which establishes happens-before
    // relationships between atomic replace/put operations. 
    // - Grid squares are all mappings between immutable Point objects and immutable implementations of the Square interface. 
    // - Mutator methods only affect value mappings, never keys.
    // - Mutators replace the mapping of keys with new immutable datatype, rather than altering state
    //   of existing value objects.
    // 
    // 
    //
    
    /** Creates a board object which is a grid of squares from (0,0) to (MAXROW, MAXCOLUMN) exclusive.
     *  Assumes board dimensions (rows, cols) are greater than zero. Each square is untouched and
     *  has a 25% chance of containing a bomb
     * 
     */
    
    public void checkRep() {
        assert this.grid != null;
        for (Point p:this.grid.keySet()) {
            assert this.grid.get(p) != null;
        }
        assert this.MAXCOLUMN >= 0;
        assert this.MAXROW >= 0;
        assert (this.MAXCOLUMN * this.MAXROW) == this.grid.keySet().size();
    }
    
    
    public Board (int rows, int cols) {
        this.MAXCOLUMN = cols;
        this.MAXROW = rows;
        this.grid = new ConcurrentHashMap<Point, Square>(); 
        
        for (int i = 0; i < this.MAXROW; i++) {
            for (int j = 0; j < this.MAXCOLUMN; j++) {
                this.grid.put(new Point(i,j), new UntouchedSquare());
                int rngVal = new Random().nextInt(4);
                if (rngVal == 0) {
                    placeBomb(i, j);
                    }
                
            }
        } checkRep();
    }
    
    /**
     * Alternate constructor for board from file. File should conform to the format laid out
     * in the ps2 notes. 
     * @param file Board file
     * @throws FileNotFoundException
     * @throws IllegalArgumentException 
     */
    public Board (File boardFile) throws FileNotFoundException {
        try (Scanner boardInput = new Scanner(new BufferedReader(new FileReader(boardFile)))) {
            
            this.MAXCOLUMN = boardInput.nextInt();
            this.MAXROW = boardInput.nextInt();
            this.grid = new ConcurrentHashMap<Point, Square>();
                
            for (int row = 0; row < this.MAXROW; row++) {
                for (int col = 0; col < this.MAXCOLUMN; col++) {
                    if (boardInput.hasNextInt()) {
                        Point position = new Point(row,col);
                        this.grid.put(position, new UntouchedSquare());
                        if (boardInput.nextInt() == 1) {
                            placeBomb(row, col);
                            }
                    }
                }
            } checkRep();        
        }
    }
    
    /**
     * Constructor for testing purposes
     */
    public Board (int rows, int cols, boolean hasBombs) {
        this.MAXCOLUMN = cols;
        this.MAXROW = rows;
        this.grid = new ConcurrentHashMap<Point, Square>(); 
        
        for (int i = 0; i < this.MAXROW; i++) {
            for (int j = 0; j < this.MAXCOLUMN; j++) {
                this.grid.put(new Point(i,j), new UntouchedSquare());     
            }
        } checkRep();
    }
    
    
    /**
     * Places a bomb in a particular grid square
     * @param row A grid row
     * @param col A grid column
     * 
     */
    public void placeBomb(int row, int column) {
        Point position = new Point(row, column);
        if (this.grid.containsKey(position)) {
            this.grid.replace(position, new UntouchedSquare(true));
        }
    }
    
    /**
     * @return The size of the current board
     */
    public int boardSize() {
        return this.grid.keySet().size();
    }
    
    /**
     * @return A representation of the current board state, without mutation.
     */
    public String look() {
        checkRep();
        return this.toString();
    }
    
    /**
     * Digs a square, reavealing either a bomb or an empty square
     * @param row The row containing the square to be dug
     * @param col The column containing the square to be dug
     * @return A new dugSquare object with the same coordinates
     */
    public void dig(int row, int col) {
        Point currentSquare = new Point(row,col);
        
        //base case: square has bomb
        if (grid.get(currentSquare).hasBomb()) {
            grid.replace(currentSquare,  new DugSquare(adjacentBombs(row,col)));
            return; 
        }
        
        //base case: square has flag or is already dug
        if (grid.get(currentSquare).isDug() || grid.get(currentSquare).hasFlag()) return;
        
        //base case: adjacent bombs
        int adjacentBombs = adjacentBombs(row, col);
        if (adjacentBombs(row, col) > 0) { 
            grid.replace(currentSquare, new DugSquare(adjacentBombs));
            return; 
            }
        
        //Recursively calls dig on adjacent squares
        else grid.replace(currentSquare, new DugSquare(0)); 
            for (int[] square: adjacencies) {
            Point adjacentPosition = new Point(row+square[0], col + square[1]); 
            if (this.grid.containsKey(adjacentPosition)) dig(row + square[0], col + square[1]);            
        }
        checkRep();
    }
    
    /**
     * @param row The grid row
     * @param col The grid column
     * @return The number of adjacent squares to the current row, column which contain a bomb.
     */
    public int adjacentBombs(int row, int col) {
        int bombCount = 0;
        //Checks all adjacencies for bombs
        for (int[] squares:adjacencies) {
            Point adjacentSquare = new Point(squares[0]+row, squares[1]+col);
            if (this.grid.containsKey(adjacentSquare) && this.grid.get(adjacentSquare).hasBomb()) bombCount++;
        }
        checkRep();
        return bombCount;
    }
    
    /**
     * 
     * @return Number of rows in the current grid
     */
    public int getRows() {
        return this.MAXROW;
    }
    
    /**
     * @return Number of columns in the current grid
     */
    public int getColumns() {
        return this.MAXCOLUMN;
    }
    
    /**
     * Checks for a bomb in the grid
     * @param row A grid row
     * @param col A grid column
     */
    public boolean checkBomb(int row, int col) {
        Point position = new Point(row, col);
        if (!this.grid.containsKey(position)) return false;
        else return this.grid.get(position).hasBomb();
    }

    /**
     * Places a flag on a square
     * @param row The row containing the square to be flagged.
     * @param col The column containing the square to be flagged.
     * @return A new FlaggedSquare object
     */
    public void flag(int row, int col) {
        Point position = new Point(row,col);
        if (this.grid.containsKey(position)) {
            Square currentSquare = this.grid.get(position);
            if (!currentSquare.hasFlag()) {
                this.grid.replace(position, new FlaggedSquare(currentSquare.hasBomb()));
            }
        } checkRep();
    }
    
    /**
     * Removes a flag from a square
     * @param row The row containing the square to be flagged.
     * @param col The column containing the square to be flagged.
     * @return A new UncoveredSquare object
     */
    public void deflag(int row, int col) {
        Point position = new Point(row,col);
        if (this.grid.containsKey(position)) {
            Square currentSquare = this.grid.get(position);
            if (currentSquare.hasFlag()) {
                this.grid.replace(position, new UntouchedSquare(currentSquare.hasBomb()));
            }
        } checkRep();
    }
    
    /**
     * @return A string representation of the current board state
     */
    public String toString() { 
        String repr = "";
        for (int i = 0; i < this.MAXROW; i++) {
            for (int j = 0; j < this.MAXCOLUMN; j++) {
                Point position = new Point(i,j);
                repr += grid.get(position).toString();
                //Spacing added if not end of row
                if (j != MAXCOLUMN-1) repr += " ";
                //Newline added at end of row, except for final row
                if (j == this.MAXCOLUMN-1 && i != this.MAXROW-1) repr += "\n";
            }
        } checkRep();
        return repr;
    }
    
    
}
