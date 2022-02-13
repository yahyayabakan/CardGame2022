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
    private static final int X_AXIS = 9;
    private static final int Y_AXIS = 5;

    private Tile[][] tiles;
    private List<Unit> player1Units;
    private List<Unit> player2Units;
    private List<Tile> highlightedTiles;

     // Tile to store the last tile that was clicked
     private Tile lastTile;

    //Constructor
    public Board() {
        tiles = constructTiles();
        player1Units = new LinkedList<>();
        player2Units = new LinkedList<>();
        highlightedTiles = new LinkedList<>();
    }

    /**
     * Helper method that the constructor uses to generate all the tiles required.
     * Helper function that the constructor uses to generate all the tiles required.
     * @return List of Tile object.
     */
    private Tile[][] constructTiles(){
        Tile[][] tiles = new Tile[X_AXIS][Y_AXIS];
        for(int x = 0; x < X_AXIS; x++){
            for(int y = 0; y < Y_AXIS; y++){
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
    public int getX(){
        return X_AXIS;
    }

    public int getY(){
        return Y_AXIS;
    }

    public void addUnitToPlayer1List(Unit unit){
        player1Units.add(unit);
    }

    public void removeUnitFromPlayer1List(Unit unit){
        player1Units.remove(unit);
    }

    public void addUnitToPlayer2List(Unit unit){
        player2Units.add(unit);
    }

    public void removeUnitFromPlayer2List(Unit unit){
        player2Units.remove(unit);
    }

    public List<Unit> getPlayer1Units() {
        return player1Units;
    }

    public List<Unit> getPlayer2Units() {
        return player2Units;
    }

    /**
     * Get the currently highlighted tiles.
     * @return highlightedTiles list.
     */
    public List<Tile> getHighlightedTiles() {
        return highlightedTiles;
    }

    /**
     * Clears the highlighted tiles list. Should be used whenever an action has been taken that should clear the
     * currently highlighted tiles list.
     */
    public void clearHighlightedTiles(){
        highlightedTiles.clear();
    }

    public Tile getLastTile(){
        return lastTile;
    }

    public void setLastTile(Tile clickedTile) {
        lastTile = clickedTile;
    }
}
