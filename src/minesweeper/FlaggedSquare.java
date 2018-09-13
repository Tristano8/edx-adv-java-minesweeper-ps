package minesweeper;

/**
 * 
 * An immutable data type representing a flagged square in the minesweeper grid. Each square will be in a
 * column and a row, and can either contain a bomb or no bomb.  
 *
 */

public class FlaggedSquare extends UntouchedSquare implements Square {

    
    private final boolean bomb;
    private final boolean flag;
    
    // Abstraction function: Represents a grid square at point p, with or without a bomb.
    //
    // Rep exposure: Immutable datatype
    //
    // Threadsafe: This datatype is threadsafe because it's immutable: all fields are final

    public FlaggedSquare(boolean bomb) {
        this.flag = true;
        this.bomb = bomb; 
        }

    // Alternate constructor for testing purposes
    public FlaggedSquare() {
        this.flag = true;
        this.bomb = false; 
        }
    
    /**
     * @return true if square contains a bomb
     */
    public boolean hasBomb() {
        return this.bomb;
    }
    
    /**
     * @return true if square is flagged
     */
    public boolean hasFlag() {
        return this.flag;
    }
    
    /**
     * @return true is square is dug
     */
    public boolean isDug() {
        return false;
    }
    
    /**
     * Digs the current square
     */
    public Square dig() {
        return this;
    }
    
    /**
     * @return a String represenation of the current square
     */
    @Override
    public String toString() {
        return "F";
    }
}


