package minesweeper;

/**
 * 
 * An immutable cartesian Point. Contains fields for positions on the x and y axis.
 *
 */
public class Point implements Comparable<Point> {

    private final int x;
    private final int y;
    
    //Abstraction function: The cartesian product p of two points x and y, such that p = (x,y)
    // Supports natural ordering, such that (0,0) < (0,1) < (1,0)
    //
    //Rep invariant: None
    //
    //Rep exposure: All fields are private and final
    //
    //Threadsafe: This datatype is threadsafe because it's immutable: all fields are final
    
    
    /**
     * @param x Coordinate along the x-axis
     * @param y Coordinate along the y-axist
     * @return A new Point with dimensions (x,y)
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    
    /**
     * @return The x coordinate of this Point.
     */
    public int getX() {
        return this.x;
    }
    
    /**
     * @return The y coordinate of this Point/
     */
    public int getY() {
        return this.y;
    }
    
    /**
     * @param thatObject Any object
     * @return true if both points have matching coordinates, false otherwise
     */
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Point)) return false;
        Point thatPoint = (Point) thatObject;
        return (thatPoint.x == this.x) && (thatPoint.y == this.y);
    }
    
    /**
     * @return A hash code value consistent with the equals() definition of structural equality.
     */
    public int hashCode() {
        int hash = 373;
        hash = 37 * hash + x;
        hash = 37 * hash + y;
        return hash;
    }
    
    /**
     * @return A string representation
     */
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    @Override
    public int compareTo(Point other) {
        if (this.getX() < other.getX()) return -1;
        else if ((this.getX() == other.getX()) && (this.getY() < other.getY())) return -1;
        else return 1;
    }
}
