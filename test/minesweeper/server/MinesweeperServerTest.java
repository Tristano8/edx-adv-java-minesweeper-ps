/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper.server;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Optional;
import java.util.Random;

import org.junit.Test;

import minesweeper.Board;


/**
 * Testing strategy:
 * 
 * 
 * 
 */
public class MinesweeperServerTest {
    
    private static final String LOCALHOST = "127.0.0.1";
    private static final int PORT = 4000 + new Random().nextInt(1 << 15);
    
    private static final int MAX_CONNECTION_ATTEMPTS = 10;
    
    /**
     * Start a MinesweeperServer in debug mode
     * 
     * @return thread running the server
     * @throws IOException if the board file cannot be found
     */
    private static Thread startMinesweeperServer(String boardFile) {
        final String[] args = new String[] {
                "--debug",
                "--port", Integer.toString(PORT),
                "--file", boardFile

        };
        Thread serverThread = new Thread(() -> MinesweeperServer.main(args));
        serverThread.start();
        return serverThread;
    }
    
    /**
     * Connect to a MinesweeperMultiServer and return the connected socket.
     * 
     * @param server abort connection attempts if the thread dies
     * @return socket connected to the server
     * @throws IOException if the connection fails
     */
    private static Socket connectToMinesweeperServer(Thread server) throws IOException {
        int attempts = 0;
        while (true) {
            try {
                Socket socket = new Socket(LOCALHOST, PORT);
                socket.setSoTimeout(3000);
                return socket;
            } catch (ConnectException ce) {
                if ( ! server.isAlive()) {
                    throw new IOException("Server thread not running");
                }
                if (++attempts > MAX_CONNECTION_ATTEMPTS) {
                    throw new IOException("Exceeded max connection attempts", ce);
                }
                try { Thread.sleep(attempts * 10); } catch (InterruptedException ie) {}
            }
        }
    }
    
    @Test(timeout = 10000)
    public void TestSingleConnection() throws IOException {
        MinesweeperServer server = new MinesweeperServer(PORT + 5, true);
        Thread thread = startMinesweeperServer("testboard1");
        Socket client = connectToMinesweeperServer(thread);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        
        out.println("");

        
        
    }
    @Test(timeout = 10000)
    public void TestMultiConnection() throws IOException {
        MinesweeperServer server = new MinesweeperServer(PORT + 1, true);
        Thread thread = startMinesweeperServer("testboard1");
        Socket client = connectToMinesweeperServer(thread);
        Socket client2 = connectToMinesweeperServer(thread);
        Socket client3 = connectToMinesweeperServer(thread);
    }
    
    @Test
    public void TestRandomBoard() throws IOException {
        Board gameBoard = new Board(3,3);
        assertEquals(gameBoard.boardSize(), 9);
        
        
    }
    
    @Test(timeout = 5000)
    public void TestBoardInputFromFile() throws IOException {
        Thread thread = startMinesweeperServer("testboard1");

        Socket socket = connectToMinesweeperServer(thread);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        Optional<File> file  = Optional.of(new File("testboard1"));
        MinesweeperServer.runMinesweeperServer(true, file, 10, 10, 2000);
        
        assertTrue("expected HELLO message", in.readLine().startsWith("Welcome"));
        

    }
    
    @Test
    public void TestBoardInputFromUser() throws IOException {
        MinesweeperServer.runMinesweeperServer(true, Optional.empty(), 4, 3, PORT);
    }
    
    @Test(timeout = 10000)
    public void publishedTest() throws IOException {

        Thread thread = startMinesweeperServer("testboard2");

        Socket socket = connectToMinesweeperServer(thread);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        assertTrue("expected HELLO message", in.readLine().startsWith("Welcome"));

        out.println("look");
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());

        out.println("dig 3 1");
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - 1 - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());

        out.println("dig 4 1");
        assertEquals("BOOM!", in.readLine());

        out.println("look"); // debug mode is on
        assertEquals("             ", in.readLine());
        assertEquals("             ", in.readLine());
        assertEquals("             ", in.readLine());
        assertEquals("             ", in.readLine());
        assertEquals("             ", in.readLine());
        assertEquals("1 1          ", in.readLine());
        assertEquals("- 1          ", in.readLine());

        out.println("bye");
        socket.close();
    }
    
}
