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
	protected int BASE_MOVEMENT = 2;
	protected int BASE_ATTACK_RANGE = 2;
	protected boolean isSummoned = false;
	private boolean hasMoved = false;
	private boolean hasAttacked = false;
	private int health;
	private int MAX_HEALTH;
	private int attack;
	
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

	public int getHealth() {
		return health;
	}

	/**
	 * Should only be used the first time the unit is set. Do not use when decrementing health.
	 * @param health card's health.
	 */
	public void setHealthWithMax(int health) {
		this.health = health;
		this.MAX_HEALTH = health;
	}

	public void setHealth(int health){
		this.health = health;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
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
	 * Method to increase to health a unit. It cannot go over the MAX_HEALTH value.
	 * This method should be called for any ability that would heal a unit.
	 * @param amount the amount by which to increase health
	 */
	public void heal(int amount){
		if(health + amount > MAX_HEALTH) health = MAX_HEALTH;
		else health+=amount;
	}
	/**
	 * Method to call whenever a unit takes damage. It decreases the health according to damage.
	 * If the unit health reaches below 1, then the unit is destroyed and removed from the board.
	 * Otherwise, if it survives it will update the front-end to display the new change.
	 * @param damage amount of damage
	 * @param gameState the current state of the game.
	 * @param out game actor reference
	 */
	public void takeDamage(int damage, GameState gameState, ActorRef out){
		health -= damage;
		if(health < 1){
			List<Unit> playerOneUnits = gameState.getBoard().getPlayer1Units();
			List<Unit> playerTwoUnits = gameState.getBoard().getPlayer2Units();
			//it will remove from either list one or two. The remove() method checks whether it contains an object, so there is no need to check manually.
			playerOneUnits.remove(this);
			playerTwoUnits.remove(this);
			BasicCommands.setUnitHealth(out, this, 0);
			//remove it from tile.
			gameState.getBoard().getTile(position.tilex, position.tiley).addUnit(null);
			//delete the unit from the front-end.
			BasicCommands.playUnitAnimation(out, this, UnitAnimationType.death);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.deleteUnit(out,this);
		}
		else{
			BasicCommands.setUnitHealth(out, this, health);
		}
	}

	/**
	 * Summon a unit on the front end on a specific tile.
	 * It will generate the attack and health according to unit.
	 * It also adds the unit to the tile in the backend and to the correct unit list of player.
	 * @param out game actor reference
	 * @param tile the tile on which to display.
	 * @param player the player to who the unit belongs to.
	 * @param board current state of the board.
	 */
	public void summon(ActorRef out, Tile tile, Player player, Board board){
		this.setPositionByTile(tile);
		tile.addUnit(this);
		if(player.getPlayerNumber() == 1){
			board.getPlayer1Units().add(this);
		}
		else {
			board.getPlayer2Units().add(this);
		}
		BasicCommands.drawUnit(out, this, tile);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, this, attack);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitHealth(out, this, health);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	}

	/**
	 * Reset the has attacked boolean to default false.
	 * Can be used after the end of each turn.
	 */
	public void resetAttack(){
		hasAttacked = false;
	}

	/**
	 * The method used to attack another unit. Called on the unit that wants to attack and passing another unit as parameter.
	 * Highlighting should be performed first.
	 * @param unit the unit to attack.
	 * @param gameState current state of the game.
	 * @param out reference to the game actor.
	 */
	public void attack(Unit unit, GameState gameState, ActorRef out){
		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		unit.takeDamage(attack, gameState, out);
		unit.counter(this, gameState, out);
	}

	/**
	 * This method is used by the attack method. If a unit is able to counter, as per rules, then it will do so.
	 * @param unit the unit that attacked this unit.
	 * @param gameState the current state of the game
	 * @param out actor reference.
	 */
	private void counter(Unit unit, GameState gameState, ActorRef out) {
		if (health > 1) {
			int X = position.tilex;
			int Y = position.tiley;
			for (int x = X - (BASE_ATTACK_RANGE - 1); x < X + BASE_ATTACK_RANGE; x++) {
				for (int y = Y - (BASE_ATTACK_RANGE - 1); y < Y + BASE_ATTACK_RANGE; y++) {
					if(gameState.getBoard().getTile(x,y).getUnit() == unit){
						BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);
						try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
						unit.takeDamage(attack, gameState, out);
						break;
					}
				}
			}
		}
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
						try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}
						board.getHighlightedTiles().add(highlightedTile);
					}
					//else leave default colour.
				}
			}
			else { //if no unit is present then draw the move tile.
				Tile highlightedTile = board.getTile(x,y);
				board.getHighlightedTiles().add(highlightedTile);
				BasicCommands.drawTile(out, highlightedTile, 1);
				try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		//if a unit hasn't attacked check the border cases.
		try {
			if (!hasAttacked && board.getTile(x,y).getUnit() == null) {
				displayInRangeAttackTiles(out, board.getTile(x, y), board);
				try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		catch (IndexOutOfBoundsException ignored){}
	}

	//Handles the movement of the unit to a tile
	public Boolean moveUnit(Tile tile, ActorRef out, Board board){
		Boolean validMove=false;
		Boolean attackMove=false;
		Tile attackMoveTile = new Tile();
		Unit unit;
		for(Tile vt:board.getHighlightedTiles()){
			if(tile.tilex==vt.tilex && tile.tiley==vt.tiley){
				unit=tile.getUnit();
				if(!board.getPlayer2Units().contains(unit))
					validMove=true;
				else{
					attackMove=true;
				}
		}	}
		if(validMove){ // Only moves if it is a valid move
			BasicCommands.moveUnitToTile(out,this,tile);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			this.setPositionByTile(tile);
			board.clearHighlightedTiles();
			hasMoved=true;
			return true;
		}else return false;

		// TODO This needs to be updated with the logic for attacking after moving
		//if(attackMove){
			// for(Tile vt:board.getHighlightedTiles()){
			// 	try {
			// 		if((tile.tilex-1==vt.tilex && tile.tiley==vt.tiley)||
			//   		 (tile.tilex==vt.tilex && tile.tiley+1==vt.tiley)||
			//   		 (tile.tilex+1==vt.tilex && tile.tiley==vt.tiley)||
			// 	     (tile.tilex==vt.tilex && tile.tiley-1==vt.tiley)){
			// 	  	 attackMoveTile=vt;
			//    		}
			// 	} catch (IndexOutOfBoundsException ignored ) {}
			// }
			// ALTERNATE
			// Tile attacker= board.getLastTile();
			// if(tile.tiley==attacker.tiley){
			// 	if(attacker.tilex<tile.tilex)
			// 		attackMoveTile=board.getTile(attacker.tilex+1,tile.tiley);
			// 	else	
			// 		attackMoveTile=board.getTile(attacker.tilex-1,tile.tiley);
			// }else if(tile.tilex==attacker.tilex){
			// 	if(attacker.tiley<tile.tiley)
			// 		attackMoveTile=board.getTile(attacker.tilex,tile.tiley+1);
			// 	else	
			// 		attackMoveTile=board.getTile(attacker.tilex,tile.tiley-1);
			// }
			// BasicCommands.moveUnitToTile(out,this,attackMoveTile);
			// try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			// this.setPositionByTile(attackMoveTile);
			// board.clearHighlightedTiles();
			// hasMoved=true;
			// return true;
		 
	}

}
