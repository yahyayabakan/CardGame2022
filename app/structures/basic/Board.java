package structures.basic;

import utils.BasicObjectBuilders;
import java.util.LinkedList;
import java.util.List;

/**
 * The logical representation of the game board.
 * It is composed of Tile objects which represent each tile on the grid.
 * Then, stores the information about player1 and player2 units on the board.
 *
 * @author The team!
 */
public class Board {
    //the grid size values
    private static final int HEIGHT = 5;
    private static final int WIDTH = 9;

    private Tile[][] tiles;
    private List<Unit> player1Units;
    private List<Unit> player2Units;

    //Constructor
    public Board() {
        tiles = constructTiles();
        player1Units = new LinkedList<>();
        player2Units = new LinkedList<>();
    }

    /**
     * Helper method that the constructor uses to generate all the tiles required.
     * Helper function that the constructor uses to generate all the tiles required.
     * @return List of Tile object.
     */
    private Tile[][] constructTiles(){
        Tile[][] tiles = new Tile[HEIGHT][WIDTH];
        for(int x = 0; x < HEIGHT; x++){
            for(int y = 0; y < WIDTH; y++){
                Tile tile = BasicObjectBuilders.loadTile(x,y);
                tiles[x][y] = tile;
            }
        }
        return tiles;
    }

    /**
     * Get a Tile reference from the board using the coordinates.
     * @param x - height
     * @param y - width
     * @return Tile - Returns the reference to Tile(x,y)
     */
    public Tile getTile(int x, int y){
        return tiles[x][y];
    }
    public int getHeight(){
        return HEIGHT;
    }

    public int getWidth(){
        return WIDTH;
    }
}
