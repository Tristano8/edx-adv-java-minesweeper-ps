/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;


/**
 * Test suite for Minesweeper Board
 */
public class BoardTest {
    
    //Testing strategy
    // 
    // Board
    // 
    // Partitions
    //
    // look()
    //
    // dig()
    // digging an untouched square
    // digging a square with a bomb
    // digging an already dug square
    // digging a square with a flag
    // digging a grid of entirely empty squares 
    //
    //
    // flag()
    // Flagging an untouched square
    // Flagging a dug square
    // Flagging an already flagged square
    //
    // deflag()
    // Unflagging a flagged square
    // Unflagging a dug square
    // Unflagging an already unflagged square
    //
    // adjacentBombs()
    // No adjacent bombs
    // Multiple adjacent bombs
    //
    // Square
    // 
    // Partitions
    //
    // hasBomb()
    // Square has a bomb
    // Square has no bomb
    //
    // hasflag()
    // Square has a flag
    // Square has no flag
    //
    // isDug()
    // Square is already dug
    // Square is untouched without a bomb
    // Square is untouched with a bomb
    //
    // toString()
    //
    // Point
    // getX, getY

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testSquareBomb() {
        //New square with bomb
        Square kaboom = new UntouchedSquare(true);
        assertTrue(kaboom.hasBomb());
        
        Square noBomb = new UntouchedSquare(false);
        assertFalse(noBomb.hasBomb());
        
        Square dugSquare = new DugSquare(0);
        assertFalse(dugSquare.hasBomb());
        
        //New flagged square with bomb
        Square flaggedBomb = new FlaggedSquare(true);
        assertTrue(flaggedBomb.hasBomb());
        
        Square flaggedNoBomb = new FlaggedSquare();
        assertFalse(flaggedNoBomb.hasBomb());
        
    }
    
    @Test
    public void testSquareHasFlag() {
        Square noFlag = new UntouchedSquare();
        assertFalse(noFlag.hasFlag());
        
        Square withFlag = new FlaggedSquare();
        assertTrue(withFlag.hasFlag());
        
        Square dugSquare = new DugSquare(0);
        assertFalse(dugSquare.hasFlag());
    
    }
    
    @Test
    public void testSquareIsDug() {
        
        Square newSquare = new UntouchedSquare();
        assertFalse(newSquare.isDug());
        
        Square dugSquare = new DugSquare(0);
        assertTrue(dugSquare.isDug());
        
        Square withFlag = new FlaggedSquare();
        assertFalse(withFlag.isDug());
    }
    
    
    @Test
    public void testSquareToString() {
        Square newSquare = new UntouchedSquare();
        assertEquals(newSquare.toString(), "-");
        
        Square withFlag = new FlaggedSquare();
        assertEquals(withFlag.toString(), "F");
        
        Square dugSquare = new DugSquare(4);
        assertEquals(dugSquare.toString(), "4");
    }
    
    @Test
    public void testPointXY() {
        Point position = new Point(4,5);
        assertEquals(position.getX(),4);
        assertEquals(position.getY(),5);
        
        Point position2 = new Point(4,5);
        assertEquals(position, position2);
    }
    
    @Test
    public void testPointToString() {
        Point position = new Point(2,3);
        assertEquals(position.toString(), "(2,3)");
    }
    
    @Test
    public void testPointComparator() {
        
        List<Point> toSort = new ArrayList<Point>();
        toSort.add(new Point(1,2));
        toSort.add(new Point(0,0));
        toSort.add(new Point(0,0));
        toSort.add(new Point(1,5));
        
        Collections.sort(toSort);
        
        assertEquals(toSort.get(0), new Point(0,0));
        assertEquals(toSort.get(1), new Point(0,0));
        assertEquals(toSort.get(2), new Point(1,2));
        assertEquals(toSort.get(3), new Point(1,5));
    }
    
    @Test
    public void testBoardStructure() {
        Board testBoard = new Board(1,1);
        assertEquals(testBoard.toString(), "-");
    }
    
    @Test
    public void testBoardFlagging() {
        Board testBoard = new Board(1,1);
        testBoard.flag(0,0);
        assertEquals(testBoard.toString(), "F");
        testBoard.deflag(0,0);
        assertEquals(testBoard.toString(), "-");
    }
    
    @Test
    public void testFlaggingBombSquare() {
        Board testBoard = new Board(3,3);
        testBoard.flag(0,0);
        assertEquals(testBoard.toString(),"F - -\n- - -\n- - -");
        testBoard.deflag(0,0);
        assertEquals(testBoard.toString(),"- - -\n- - -\n- - -");
    }
    
    @Test
    public void testBoardAdjacentBombs() {
        Board testBoard = new Board(3, 3);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                testBoard.placeBomb(i, j);
            }
        }
        assertTrue(testBoard.adjacentBombs(1,1) == 8);
        assertTrue(testBoard.adjacentBombs(0,0) == 3);
    }
    
    @Test
    public void testBoardAdjacentNoBombs() {
        Board testBoard = new Board(3,3, false);
        assertTrue(testBoard.adjacentBombs(1,1) == 0);
        assertTrue(testBoard.adjacentBombs(0, 0) == 0);
    }
    
    @Test
    public void testNoBombsDigging() {
        Board testBoard = new Board(3,3, false);
        testBoard.dig(1,1);
        testBoard.dig(1,1); //Attempts to dig an already cleared square
        assertEquals(testBoard.toString(),"     \n     \n     ");
    }
    
    @Test
    public void testOneBombDigging() {
        //Creates a 3x3 with a single bomb in the corner
        Board testBoard = new Board(3,3, false);
        testBoard.placeBomb(0,0);
        testBoard.dig(1,1);
        assertEquals(testBoard.toString(),"- - -\n- 1 -\n- - -");
        
        testBoard.dig(2,2);
        assertEquals(testBoard.toString(),"- 1  \n1 1  \n     ");
    }
    
    @Test
    public void testDiggingBomb() {
        //Creates a 3x3 with a single bomb in the corner at 0,0
        Board testBoard = new Board(3,3, false);
        testBoard.placeBomb(0,0);
        testBoard.dig(0,0);
        assertFalse(testBoard.checkBomb(0, 0));
        assertEquals(testBoard.toString(),"  - -\n- - -\n- - -");
    }
    
    @Test
    public void testGridHasBomb() {
        Board testBoard = new Board(3,3, false);
        testBoard.placeBomb(0,0);
        assertEquals(testBoard.checkBomb(0,0), true);
        assertEquals(testBoard.checkBomb(1,0), false);
    }
    
    @Test
    public void testBoardFromFile() throws FileNotFoundException {
        File file = new File("testboard1");
        Board testBoard = new Board(file);
        assertTrue(testBoard.checkBomb(1, 0));
        assertTrue(testBoard.checkBomb(2, 2));
    }
    
}
