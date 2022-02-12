package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import utils.BasicObjectBuilders;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile. 
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Unit {

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	private int id;
	private UnitAnimationType animation;
	private Position position;
	private UnitAnimationSet animations;
	private ImageCorrection correction;
	private int BASE_MOVEMENT = 2;
	private int BASE_ATTACK_RANGE = 2;
	private boolean hasMoved = false;
	private boolean hasAttacked = false;
	
	public Unit() {}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
	}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
	}
	
	
	
	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UnitAnimationType getAnimation() {
		return animation;
	}
	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}
	
	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
	}

	/**
	 * Reset the has attacked boolean to default false.
	 * Can be used after the end of each turn.
	 */
	public void resetAttack(){
		hasAttacked = false;
	}

	/**
	 * Whenever this method is called, it first calls the drawDefaultTilesGrid. This will clear the previously
	 * highlighted tiles.
	 * Afterwards, it will check whether this unit has attacked. If not, it will start the search.
	 * It searches through a square of size BASE_ATTACK_RANGE which defines how far it can reach.
	 * If it finds enemy units on any of those tiles, it will highlight them in red. Then, it will add this tile
	 * to the GameState highlighted tiles list.
	 * @param out game actor reference.
	 * @param tile the tile that was clicked.
	 * @param gameState the current state of the game.
	 * @see GameState
	 */
	public void displayInRangeAttackTiles(ActorRef out, Tile tile, GameState gameState) {
		gameState.drawDefaultTilesGrid(out);
		int X = tile.getTilex();
		int Y = tile.getTiley();
		Board board = gameState.getBoard();
		if (!hasAttacked) {
			for (int x = X - (BASE_ATTACK_RANGE - 1); x < X + BASE_ATTACK_RANGE; x++) {
				for (int y = Y - (BASE_ATTACK_RANGE - 1); y < Y + BASE_ATTACK_RANGE; y++) {
					if (!(x == X && y == Y) &&
							x < board.getX() &&
							y < board.getY()
							&& x >= 0 && y >= 0) {
							Unit unit = board.getTile(x,y).getUnit();
							if(unit != null){
								if(board.getPlayer2Units().contains(unit)){
									Tile highlightedTile = BasicObjectBuilders.loadTile(x,y);
									BasicCommands.drawTile(out, highlightedTile, 2);
									gameState.getHighlightedTiles().add(highlightedTile);
								}
							}
					}
				}
			}
		}
	}

	/**
	 * Reset the has moved variable to default false.
	 * Can be used after the end of each turn.
	 */
	public void resetMovement(){
		hasMoved = false;
	}

	// FIXME: 10/02/2022 Need to draw diamond shape.
	// TODO
	//Responsible for displaying the movement tiles. Uses helper function to check validity.
	public void displayMovementTiles(ActorRef out, int X, int Y, GameState gameState){
		Board board = gameState.getBoard();
		//if a unit has attacked, then it forfeits it's ability to move.
		if(hasAttacked) hasMoved = true;
		//movement base logic
		if(!hasMoved){
			for(int x = X - (BASE_MOVEMENT - 1); x < X+BASE_MOVEMENT; x++){
				for(int y = Y - (BASE_MOVEMENT - 1); y < Y+BASE_MOVEMENT; y++){
					drawValidTilesForMovement(x, X, y, Y, board, out);
				}
			}
			//additional tiles to cover.
			int[][] extraTiles = {{X+BASE_MOVEMENT, Y},{X-BASE_MOVEMENT, Y}, {X, Y+BASE_MOVEMENT},{X, Y-BASE_MOVEMENT}};
			for(int[] extraTile : extraTiles){
				drawValidTilesForMovement(extraTile[0], X, extraTile[1], Y, board, out);
			}
		}
	}

	//Handles the main drawing of the tiles according to validation rules.
	private void drawValidTilesForMovement(int x, int X, int y, int Y, Board board, ActorRef out){
		if(!(x == X && y == Y) &&
				x < board.getX() &&
				y < board.getY()
				&& x >= 0 && y >= 0){
			if(board.getTile(x,y).unit != null){
				Unit unit = board.getTile(x,y).getUnit();
				if(board.getPlayer1Units().contains(unit)){
					//leave default colour.
				}
				else if(board.getPlayer2Units().contains(unit)){
					if(!hasAttacked){
						BasicCommands.drawTile(out, BasicObjectBuilders.loadTile(x,y), 2);
					}
					//else leave default colour.
				}
			}
			else { //if no unit is present then draw the move tile.
				BasicCommands.drawTile(out, BasicObjectBuilders.loadTile(x, y), 1);
			}
		}
	}
}
