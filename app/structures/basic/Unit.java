package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import utils.BasicObjectBuilders;

import java.util.LinkedList;
import java.util.List;

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
	 * Afterwards, it will check whether this unit has attacked. If not, it will start the search.
	 * It searches through a square of size BASE_ATTACK_RANGE which defines how far it can reach.
	 * If it finds enemy units on any of those tiles, it will highlight them in red. Then, it will add this tile
	 * to the GameState highlighted tiles list.
	 * @param out game actor reference.
	 * @param tile the tile that was clicked.
	 * @param board the current state of the board.
	 * @see Board
	 */
	public void displayInRangeAttackTiles(ActorRef out, Tile tile, Board board) {
		int X = tile.getTilex();
		int Y = tile.getTiley();
		if (!hasAttacked) {
			for (int x = X - (BASE_ATTACK_RANGE - 1); x < X + BASE_ATTACK_RANGE; x++) {
				for (int y = Y - (BASE_ATTACK_RANGE - 1); y < Y + BASE_ATTACK_RANGE; y++) {
					if (x < board.getX() &&
							y < board.getY()
							&& x >= 0 && y >= 0) {
							Unit unit = board.getTile(x,y).getUnit();
							if(unit != null){
								if(board.getPlayer2Units().contains(unit)){
									try{
										Tile highlightedTile = board.getTile(x,y);
										BasicCommands.drawTile(out, highlightedTile, 2);
										board.getHighlightedTiles().add(highlightedTile);
									}catch (IndexOutOfBoundsException ignored){}
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

	/**
	 * Displays a unit's movement tiles. If the unit has not yet attacked, it will display the enemy's that it can target.
	 * @param out reference to game actor
	 * @param tile the unit's tile.
	 * @param board current state of the game board.
	 */
	public void displayMovementTiles(ActorRef out, Tile tile, Board board){
		int X = tile.getTilex();
		int Y = tile.getTiley();
		//if a unit has attacked, then it forfeits it's ability to move.
		if(hasAttacked) hasMoved = true;
		//movement base logic
		if(!hasMoved){
			//Creating the default diamond shape highlight using points dinstance from center.
			for(int x = X - BASE_MOVEMENT; x <= X+BASE_MOVEMENT; x++){
				for(int y = Y - BASE_MOVEMENT; y <= Y+BASE_MOVEMENT; y++){
					int a = Math.abs(x-X);
					int b = Math.abs(y-Y);
					if(a+b <= BASE_MOVEMENT){
						drawValidTilesForMovement(x, y, tile, board, out);
					}
				}
			}
		}
	}

	/**
	 * Helper function for displayMovementTiles().
	 * If there are enemy units that can be targeted, it will highlight them.
	 * It ignores friendly units.
	 * Then, at the end checks border cases where a unit can move, then attack.
	 * @param x - tile to consider, coordinate x.
	 * @param y - tile to consider, coordinate y.
	 * @param tile - the original unit's tile.
	 * @param board - current state of the board.
	 * @param out - game actor reference
	 */
	private void drawValidTilesForMovement(int x, int y, Tile tile, Board board, ActorRef out){
		int X = tile.getTilex();
		int Y = tile.getTiley();
		if(!(x == X && y == Y) &&
				x < board.getX() &&
				y < board.getY()
				&& x >= 0 && y >= 0){
			if(board.getTile(x,y).unit != null){
				Unit unit = board.getTile(x,y).getUnit();
				//if there is an enemy unit and this unit can attack it, highlight it in red.
				if(board.getPlayer2Units().contains(unit)){
					if(!hasAttacked){
						Tile highlightedTile = board.getTile(x,y);
						BasicCommands.drawTile(out, highlightedTile, 2);
						board.getHighlightedTiles().add(highlightedTile);
					}
					//else leave default colour.
				}
			}
			else { //if no unit is present then draw the move tile.
				Tile highlightedTile = board.getTile(x,y);
				board.getHighlightedTiles().add(highlightedTile);
				BasicCommands.drawTile(out, highlightedTile, 1);
			}
		}
		//if a unit hasn't attacked check the border cases.
		try {
			if (!hasAttacked) {
				displayInRangeAttackTiles(out, board.getTile(x, y), board);
			}
		}
		catch (IndexOutOfBoundsException ignored){}
	}
}
