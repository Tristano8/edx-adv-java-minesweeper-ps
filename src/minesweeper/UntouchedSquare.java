package minesweeper;

/**
 * 
 * An immutable data type representing an untouched square in the minesweeper grid. Each square will be in a
 * column and a row, and can either contain a bomb or no bomb.  
 *
 */
public class UntouchedSquare implements Square {
    
    private final boolean bomb;
    private final boolean flag;
    
    // Abstraction function: Represents a grid square at point p, with or without a bomb.
    //
    // Rep exposure: None
    //
    // Threadsafe: This datatype is threadsafe because it's immutable: all fields are final

    public UntouchedSquare() {
        this.flag = false;
        this.bomb = false;
    }
    
    //Alternate constructor for placing bomb squares
    public UntouchedSquare(boolean bomb) {
        this.flag = false;
        this.bomb = bomb;
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
     * @return a String represenation of the current square
     */
    @Override
    public String toString() {
        return "-";
    }
}


