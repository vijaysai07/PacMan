package pacman;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class Board {
    private char[][] board;
    public static final Point UP = new Point(0,-1);
    public static final Point DOWN = new Point(0,1);
    public static final Point LEFT = new Point(-1,0);
    public static final Point RIGHT = new Point(1,0);
    private boolean gameOver;
    
    public Board() {
        // Initialize the 'gameOver' variable.
        gameOver = false;
        // Initialize the 'board' variable.
        board = getNewBoard();
    }

    // This code is used in the Board constructor, and also in the Window
    // class to determine dimensions. getNewBoard() returns a character array
    // representing a clean board from board.txt
    public static char[][] getNewBoard() {
        
        // Load board.txt into an array and return it. InputStreams are
        // annoying; see the note at convertStreamToString()
        InputStream boardIS = Board.class.getResourceAsStream("board.txt");
        String boardStr = "";
        try {
            boardStr = convertStreamToString(boardIS);
        } catch (IOException ex) {
            System.out.println("convertStreamToString() failed!");
        }

        String[] lines = boardStr.split("\r\n|\r|\n");

        int maxLineLength = 0;
        for(int i=0;i<lines.length;i++) {
            if(maxLineLength < lines[i].length())
                maxLineLength = lines[i].length();
        }

        char[][] result = new char[maxLineLength][lines.length];

        for(int i=0;i<lines.length;i++) {
            for(int k=0;k<lines[i].length();k++) {
                result[k][i] = lines[i].charAt(k);
            }
        }

        return result;
    }

    // An InputStream presents an awkward, slightly annoying way of
    // getting data from a file. Unfortunately, Java doesn't have anything
    // better, so we use it. This method "reads" data from an InputStream
    // and returns the contents of the file as a String.
    private static String convertStreamToString(InputStream is)
            throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    // Return a text-based "picture" of what the board looks like.
    public String render() {
        if(gameOver)
            return "GAME OVER!";

        // Since our internal representation of the game board, the 'board'
        // variable, is already pretty good looking, we'll just turn it into
        // a string and display it.
        int maxLineLength = 0;
        for(int i=0;i<board.length;i++) {
            if(maxLineLength < board[i].length)
                maxLineLength = board[i].length;
        }

        StringBuffer result = new StringBuffer();
        for(int i=0;i<maxLineLength;i++) {
            for(int k=0;k<board.length;k++) {
                result.append(board[k][i]);
            }
            result.append('\n');
        }
        return result.toString();
    }

    // Move the thing (presumably either Pac-Man or a ghost) at the given
    // coordinates one step in the given direction. If there's a wall in the
    // way, don't do anything.
    public void move(int x, int y, Point direction) {
        if(gameOver)
            return; // Game is already over; do nothing.
        
        int newX = x+direction.x;
        int newY = y+direction.y;

        char thing = getThingAt(x,y);
        char thingAtNewPosition = getThingAt(newX,newY);

        if(thingAtNewPosition!=' ' && thingAtNewPosition!='.') {
            // Our thing has bumped into something. Silly thing!
            if(isPacManAt(x,y) && thingAtNewPosition=='*') {
                // Our Pac-Man has bumped into a ghost; whoops!
                gameOver = true;
            }
            return;
        }

        setThingAt(newX,newY,thing);
        setThingAt(x,y,' ');
    }

    // Return whatever there is at the given coordinates. Encapsulation, see?
    public char getThingAt(int x, int y) {
        return board[x][y];
    }

    // Change the thing at the given coordinates to the other thing.
    private void setThingAt(int x, int y, char thing) {
        board[x][y] = thing;
    }

    // Return true if there's a wall of any sort at the given coordinates.
    public boolean isPacManAt(int x, int y) {
        char c = getThingAt(x,y);
        return (c=='<' ||
                c=='>' ||
                c=='ʌ' ||
                c=='v');
    }

    // Find Pac-Man and return his location. If he's nowhere to be found,
    // return null.
    public Point getPacManLocation() {
        for(int i=0;i<board.length;i++) {
            for(int k=0;k<board[i].length;k++) {
                if(isPacManAt(i,k))
                    return new Point(i,k);
            }
        }
        return null;
    }
}
