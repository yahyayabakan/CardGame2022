package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.units.Avatar;
import java.util.ArrayList;
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
	protected boolean hasProvoked = false;
	protected int BASE_MOVEMENT = 2;
	protected int BASE_ATTACK_RANGE = 2;
	protected boolean hasMoved = false;
	protected boolean hasAttacked = false;
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

	public int getMAX_HEALTH(){
		return MAX_HEALTH;
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

	public boolean getHasMoved(){
		return hasMoved;
	}

	public boolean getHasAttacked(){
		return hasAttacked;
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
			try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
			//clear highlighted tiles
			gameState.drawDefaultTilesGrid(out);
			gameState.getBoard().clearHighlightedTiles();
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
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, this, attack);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitHealth(out, this, health);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		
		if(!(this instanceof Avatar)){
			hasMoved = true;
			hasAttacked = true;
		}
		
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
		if(!this.hasAttacked) {
			BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			unit.takeDamage(attack, gameState, out);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			unit.counter(this, gameState, out);
			hasAttacked = true;
			hasMoved = true;
		}
		// Clear all highlighted tiles
		gameState.drawDefaultTilesGrid(out);
		gameState.getBoard().clearHighlightedTiles();
	}

	/**
	 * This method is used by the attack method. If a unit is able to counter, as per rules, then it will do so.
	 * @param unit the unit that attacked this unit.
	 * @param gameState the current state of the game
	 * @param out actor reference.
	 */
	public void counter(Unit unit, GameState gameState, ActorRef out) {
		if (health >= 1) {
			int X = position.tilex;
			int Y = position.tiley;
			Board board= gameState.getBoard();
			for (int x = X - (BASE_ATTACK_RANGE - 1); x < X + BASE_ATTACK_RANGE; x++) {
				for (int y = Y - (BASE_ATTACK_RANGE - 1); y < Y + BASE_ATTACK_RANGE; y++) {
					if (x < board.getX() &&
						y < board.getY()
						&& x >= 0 && y >= 0) {
					if(board.getTile(x,y).getUnit() == unit){
						BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);
						try {Thread.sleep(250);} catch (InterruptedException e) {e.printStackTrace();}
						unit.takeDamage(attack, gameState, out);
						break;
					}
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
		if (!hasAttacked) {
			if(nexToProvokeUnit(tile, board, out)){}
			else {
				attackDisplayHelper(out, tile, board);
			}
		}
	}

	/**
	 * Helper function to display attack tiles.
	 * @param out reference to game actor.
	 * @param tile the tile clicked.
	 * @param board the current state of the board.
	 */
	private void attackDisplayHelper(ActorRef out, Tile tile, Board board){
		int X = tile.getTilex();
		int Y = tile.getTiley();
		List<Unit> enemyList = board.getEnemyUnits(this);
		for (int x = X - (BASE_ATTACK_RANGE - 1); x < X + BASE_ATTACK_RANGE; x++) {
			for (int y = Y - (BASE_ATTACK_RANGE - 1); y < Y + BASE_ATTACK_RANGE; y++) {
				if (x < board.getX() &&
						y < board.getY()
						&& x >= 0 && y >= 0) {
					Unit unit = board.getTile(x, y).getUnit();
					if (unit != null) {
						if (enemyList.contains(unit)) {
							try {
								Tile highlightedTile = board.getTile(x, y);
								BasicCommands.drawTile(out, highlightedTile, 2);
								try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace(); }
								board.getHighlightedTiles().add(highlightedTile);

							} catch (IndexOutOfBoundsException ignored) {
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
	 * @param gameState current state of the game.
	 */
	public void displayMovementTiles(ActorRef out, Tile tile, GameState gameState){
		int X = tile.getTilex();
		int Y = tile.getTiley();
		Unit tileUnit = null;

		if(tile.getUnit() != null){
			tileUnit = tile.getUnit();
		}

		List<Tile> highlightedTiles = gameState.getBoard().getHighlightedTiles();
		List<Unit> friendlyUnits = gameState.getBoard().getFriendlyUnits(tileUnit);
		List<Unit> enemyUnits = gameState.getBoard().getEnemyUnits(tileUnit);
		List<Tile> tilesToClear = new ArrayList<>();
		List<Tile> tilesToAdd = new ArrayList<>();

		//if a unit has attacked, then it forfeits its ability to move.
		if(hasAttacked) hasMoved = true;
		if(nexToProvokeUnit(tile,gameState.getBoard(), out)){}
		//movement base logic
		else if(!hasMoved){
			//Creating the default diamond shape highlight using points distance from center.
			for(int x = X - BASE_MOVEMENT; x <= X+BASE_MOVEMENT; x++){
				for(int y = Y - BASE_MOVEMENT; y <= Y+BASE_MOVEMENT; y++){
					int a = Math.abs(x-X);
					int b = Math.abs(y-Y);
					if(a + b <= 2) {
						try {
							Tile tileToAdd = gameState.getBoard().getTile(x, y);
							gameState.getBoard().getHighlightedTiles().add(tileToAdd);
						} catch (Exception ignored) {
						}
					}
				}
			}

			// Stage 1: clear tile with no friendly neighbour
			for(int i = 0; i < highlightedTiles.size(); i++){
				int htx = highlightedTiles.get(i).getTilex();
				int hty = highlightedTiles.get(i).getTiley();
				boolean hasEmptyNeighbour = false;
				boolean hasFriendlyNeighbour = false;
				for(int x = htx-1; x <= htx+1; x++){
					for(int y = hty-1; y <= hty+1; y++){
						try {
							Tile t = gameState.getBoard().getTile(x, y);
							int ax = Math.abs(htx - x);
							int ay = Math.abs(hty - y);
							if(ax + ay == 1){
								if (highlightedTiles.contains(t) && t.getUnit() == null){
									hasEmptyNeighbour = true;
								}
								if (highlightedTiles.contains(t) && friendlyUnits.contains(t.getUnit())) {
									hasFriendlyNeighbour = true;
								}
							}
						} catch (Exception ignored){}
					}
				}
				if(!hasEmptyNeighbour && !hasFriendlyNeighbour){
					tilesToClear.add(highlightedTiles.get(i));
				}
			}

			highlightedTiles.removeAll(tilesToClear);
			tilesToClear.clear();

			// Stage 2: Clear tile with friendly unit
			for(int i = 0; i < highlightedTiles.size(); i++){
				if(tileUnit != null){
					if(friendlyUnits.contains(highlightedTiles.get(i).getUnit())){
						tilesToClear.add(highlightedTiles.get(i));
					}
				}
			}

			highlightedTiles.removeAll(tilesToClear);
			tilesToClear.clear();

			// Stage 3: Highlight enemy around the border
			for(int i = 0; i < highlightedTiles.size(); i++){
				int htx = highlightedTiles.get(i).getTilex();
				int hty = highlightedTiles.get(i).getTiley();
				int ax = Math.abs(X - htx);
				int ay = Math.abs(Y - hty);
				if(highlightedTiles.get(i).getUnit() == null){
					if(ax+ay == 2){
						for(int x = htx-1; x <= htx+1; x++){
							for(int y = hty-1; y <= hty+1; y++){
								try{
									Tile t = gameState.getBoard().getTile(x, y);
									if (!highlightedTiles.contains(t) && enemyUnits.contains(t.getUnit())){
										tilesToAdd.add(t);
									}
								} catch (Exception ignored){}
							}
						}
					}
				}

			}

			highlightedTiles.addAll(tilesToAdd);
			tilesToAdd.clear();

		}

		// Display highlighted tiles
		for(Tile t: gameState.getBoard().getHighlightedTiles()){
			if(gameState.getBoard().getEnemyUnits(tile.getUnit()).contains(t.getUnit())){
				BasicCommands.drawTile(out,t, 2);
				try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
			} else {
				BasicCommands.drawTile(out,t, 1);
				try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}

	}

	/**
	 * Checks whether a unit is next to another unit with Provoke ability.
	 * @param tile tile on which this unit is on.
	 * @param out reference to game actor
	 * @param board the current state of the board.
	 * @return true if next to a unit with provoke. False otherwise.
	 */
	protected boolean nexToProvokeUnit(Tile tile, Board board, ActorRef out) {
		int player = 1;
		if (board.getPlayer2Units().contains(this)) player = 2;
		for (int x = tile.getTilex() - 1; x <= tile.getTilex() + 1; x++) {
			for (int y = tile.getTiley() - 1; y <= tile.getTiley() + 1; y++) {
				if (x < board.getX() &&
						y < board.getY()
						&& x >= 0 && y >= 0) {
					Tile tileToCheck = board.getTile(x, y);
					if (tileToCheck.getUnit() != null && tileToCheck.getUnit().hasProvoked) {
						Unit unit = tileToCheck.getUnit();
						if (player == 1) {
							if (board.getPlayer2Units().contains(unit)) {
								board.getHighlightedTiles().add(tileToCheck);
								BasicCommands.drawTile(out, tileToCheck, 2);
								return true;
							}
						} else {
							if (board.getPlayer1Units().contains(unit)) {
								board.getHighlightedTiles().add(tileToCheck);
								BasicCommands.drawTile(out, tileToCheck, 2);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
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
	private void findActionableTiles(int x, int y, Tile tile, Board board, ActorRef out){
		int X = tile.getTilex();
		int Y = tile.getTiley();
		List<Unit> enemyList = board.getEnemyUnits(this);
		if(!(x == X && y == Y) &&
				x < board.getX() &&
				y < board.getY()
				&& x >= 0 && y >= 0){
			if(board.getTile(x,y).unit != null){
				Unit unit = board.getTile(x,y).getUnit();
				//if there is an enemy unit and this unit can attack it, add to highlighted tiles
				if(enemyList.contains(unit)){
					if(!hasAttacked){
						Tile highlightedTile = board.getTile(x,y);
						board.getHighlightedTiles().add(highlightedTile);
					}
				}
			}
			else {
				//also add the movable tiles to highlighted tiles
				Tile highlightedTile = board.getTile(x,y);
				board.getHighlightedTiles().add(highlightedTile);
			}
		}
		//if a unit hasn't attacked check the border cases.
		try {
			if (!hasAttacked && board.getTile(x,y).getUnit() == null) {
				attackDisplayHelper(out, board.getTile(x, y), board);
			}
		}
		catch (IndexOutOfBoundsException ignored){}

	}

	//Handles the movement of the unit to a tile. Will need to update how the unit moves based on the units in its path. Future Update
	public void moveUnit(Tile tile, ActorRef out, GameState gameState){
		if(gameState.getBoard().getHighlightedTiles().contains(tile)){
			Tile tilePath = gameState.getBoard().getTile(tile.getTilex(), gameState.getBoard().getLastTile().getTiley());
			if(tilePath.getUnit()==null){
				BasicCommands.moveUnitToTile(out,this,tile);
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			}else{
				Boolean yfirst=true;
				BasicCommands.moveUnitToTile(out,this,tile,yfirst);
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			}
				this.setPositionByTile(tile);
				gameState.getBoard().clearHighlightedTiles();
				tile.addUnit(this);
				gameState.getBoard().getLastTile().removeUnit();
				hasMoved=true;
		}
	}

	// Handles the movement and attack of a unit if a enemy unit was clicked.
	public void attackMoveUnit(Tile tile, ActorRef out, GameState gameState){	
			Tile attackMoveTile = null;
			Tile attacker= gameState.getBoard().getLastTile();
		// This checks the primary pathways before checking alternate ones
		try{
		if(tile.tiley==attacker.tiley){
				if(tile.tilex-attacker.tilex==2)
					attackMoveTile=gameState.getBoard().getTile(attacker.tilex+1,tile.tiley);
				else if(attacker.tilex-tile.tilex==2)	
					attackMoveTile=gameState.getBoard().getTile(attacker.tilex-1,tile.tiley);
					else if(tile.tilex-attacker.tilex==3)
						attackMoveTile=gameState.getBoard().getTile(attacker.tilex+2,tile.tiley);
						else if(attacker.tilex-tile.tilex==3)	
							attackMoveTile=gameState.getBoard().getTile(attacker.tilex-2,tile.tiley);
			}else if(tile.tilex==attacker.tilex){
				if(attacker.tiley-tile.tiley==2)
					attackMoveTile=gameState.getBoard().getTile(attacker.tilex,tile.tiley+1);
				else if(tile.tiley-attacker.tiley==2)	
					attackMoveTile=gameState.getBoard().getTile(attacker.tilex,tile.tiley-1);
					else if(attacker.tiley-tile.tiley==3)
						attackMoveTile=gameState.getBoard().getTile(attacker.tilex,tile.tiley+2);
						else if(tile.tiley-attacker.tiley==3)	
							attackMoveTile=gameState.getBoard().getTile(attacker.tilex,tile.tiley-2);
			}
		}catch(IndexOutOfBoundsException ingored){}	 

		//finds an alternate tile if the tile calcualted from the earlier step had a unit on it
			if(attackMoveTile==null){
				List<Tile> tileList = gameState.getNearbyTiles(tile);
					for(int j=0; j< gameState.getBoard().getHighlightedTiles().size();j++){
							if(tileList.contains(gameState.getBoard().getHighlightedTiles().get(j))){
								if(gameState.getBoard().getHighlightedTiles().get(j).getUnit()==null){
									if(gameState.getBoard().getHighlightedTiles().get(j).getUnit()==null)
										attackMoveTile=gameState.getBoard().getHighlightedTiles().get(j);
								}
							}
					}
				}else if(attackMoveTile.getUnit()!=null){
					List<Tile> tileList = gameState.getNearbyTiles(tile);
					for(int j=0; j< gameState.getBoard().getHighlightedTiles().size();j++){
							if(tileList.contains(gameState.getBoard().getHighlightedTiles().get(j))){
								if(gameState.getBoard().getHighlightedTiles().get(j).getUnit()==null){
									if(gameState.getBoard().getHighlightedTiles().get(j).getUnit()==null)
										attackMoveTile=gameState.getBoard().getHighlightedTiles().get(j);
								}
							}
					}
				}


		moveUnit(attackMoveTile, out, gameState);

		gameState.getBoard().clearHighlightedTiles();
		hasMoved=true;
		//Call the Attack Method here
		try {Thread.sleep(250);} catch (InterruptedException e) {e.printStackTrace();}

		this.attack(tile.getUnit(), gameState, out);

}
		 
	

}
