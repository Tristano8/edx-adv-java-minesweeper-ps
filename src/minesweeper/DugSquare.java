package minesweeper;


/**
 * 
 * An immutable data type representing a dug square in the minesweeper grid. Each square will be in a
 * column and a row, and can either contain a bomb or no bomb.  
 *
 */
public class DugSquare implements Square {
    
    private final int adjacentBombs;
    private final boolean hasBomb;
    
    // Abstraction function: Represents an uncovered grid square at point p.
    //
    // Rep invariant: adjacentBombs >= 0
    //                hasBomb != null
    //
    // Rep exposure: Immutable datatype
    //
    // Threadsafe: This datatype is threadsafe because it's immutable: all fields are final

    
    public void checkRep() {
        assert this.adjacentBombs >= 0;
    }
    
    public DugSquare(int adjacentBombs) {
        this.adjacentBombs = adjacentBombs;
        this.hasBomb = false;
        checkRep();
    }
    
    //Alternate constructor for exploded Bomb
    public DugSquare(boolean hasBomb) {
        this.adjacentBombs = 0;
        this.hasBomb = hasBomb; 
        checkRep();
    }

    /**
     * @return true if square contains a bomb
     */
    public boolean hasBomb() {
        return this.hasBomb;
        
    }
    
    /**
     * @return true if square is flagged
     */
    public boolean hasFlag() {
        return false;
    }
    
    /**
     * @return true is square is dug
     */
    public boolean isDug() {
        return true;
    }
    
    /**
     * @return a String representation of the current square
     */
    @Override
    public String toString() {
        checkRep();
        if (this.hasBomb) return "*";
        //Dug squares indicate how many bombs they are adjacent to
        if (this.adjacentBombs > 0) return String.valueOf(this.adjacentBombs);
        else return " ";
    }
        
}


