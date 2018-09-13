package minesweeper;

/**
 * 
 * An immutable data type representing a square in the minesweeper grid. Each square will be in a
 * column and a row, and can be either untouched, flagged or dug. A square will also either contain
 * a bomb or no bomb.  
 *
 */
public interface Square {
    
    // Square = UntouchedSquare(Point(x,y)) + DugSquare(Point(x,y)) + FlagSquare(Point(x,y))
    //
    // Abstraction function: Represents a grid square at point p, with dimensions x and y.
    //
    // Rep exposure: Immutable datatype

    
    /**
     * @return true if square contains a bomb
     */
    public boolean hasBomb();
    
    /**
     * @return true if square is flagged
     */
    public boolean hasFlag();
    
    /**
     * @return true is square is dug
     */
    public boolean isDug();

    /**
     * @return a String representation of the current square
     */
    @Override
    public String toString();
    
}
