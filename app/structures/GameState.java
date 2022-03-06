package structures;

import commands.BasicCommands;
import structures.basic.*;
import akka.actor.ActorRef;
import structures.units.PurebladeEnforcer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 * Extended by the team.
 */
public class GameState {
	private int turn;
	private Board board;
	private Deck playerOneDeck;
	private Deck playerTwoDeck;
	private Player playerOne;
	private Player playerTwo;
	private int unitIdCounter = 0;
	private int clickedHandPosition; // indicate clicked card position (-1 = no card is clicked)

	public boolean gameInitalised = false;
	public boolean clickable = false;

	public GameState() {
		turn = 0;
		board = new Board();
		playerOneDeck = new Deck(1);
		playerTwoDeck = new Deck(2);
		playerOne = new Player(20, 0, playerOneDeck, 1);
		playerTwo = new Player(20, 0, playerTwoDeck, 2);
	}

	public Board getBoard() {
		return board;
	}

	public Player getPlayerOne() {
		return playerOne;
	}

	public Player getPlayerTwo() {
		return playerTwo;
	}

	/**
	 * Method should be called before the next turn.
	 */
	public void incrementTurn() {
		turn++;
	}

	/**
	 * The default grid visual representation of the tiles on the front end.
	 * This creates the full grid with the base colour tiles.
	 * It should be used the first time the game loads.
	 * Afterwards, it can be used anytime the grid needs to be reset to regular color.
	 * For example, if tiles are highlighted to show movement, once movement is complete,
	 * this method can be called to return the grid colours to regular size.
	 *
	 * @param out - Game Actor reference.
	 */
	public void drawDefaultTilesGrid(ActorRef out) {
		int X = getBoard().getX();
		int Y = getBoard().getY();
		for(int x = 0; x < X; x++){
			for(int y = 0; y < Y; y++){
				try {
					Thread.sleep(10);
				} catch (Exception e) {}

				BasicCommands.drawTile(out,
						getBoard().getTile(x, y),
						0);
			}
		}
	}

	/**
	 * Method increments the player's mana to turns plus one.
	 * This should be used after each turn change on both players.
	 *
	 * @param player - Player to set mana.
	 */
	public void incrementPlayerMana(Player player) {
		if(player.getMana() != 9){
			player.setMana(turn + 1);
		}
		if(player.getMana() > 9){
			player.setMana(9);
		}
	}

	/**
	 * Method that displays the hand of a player on the front end.
	 * The game does not need player two cards, yet this method can be still used to check if the player two hand is correct.
	 * @param player the player who's hand to display.
	 * @param out actor reference.
	 */
	public void displayCurrentHandCards(ActorRef out, Player player) {
		LinkedList<Card> hand = player.getHand();
		IntStream.range(0, hand.size())
				.forEach(index ->{
					BasicCommands.drawCard(out, hand.get(index),index+1, 0);
				});
	}

	// Clear all cards in the hand at frontend
	public void clearCurrentHandCards(ActorRef out, Player player){
		LinkedList<Card> hand = player.getHand();
		IntStream.range(0, hand.size())
				.forEach(index ->{
					BasicCommands.deleteCard(out, index+1);
				});
	}

	// Generate next unit ID
	public int getNewUnitID(){
		return ++unitIdCounter;
	}
	
	//Returns enemy Units
	public List<Unit> getEnemyUnits(Unit unit){
		if(getBoard().getPlayer1Units().contains(unit))
			return getBoard().getPlayer2Units();
		else if( getBoard().getPlayer2Units().contains(unit))
			return getBoard().getPlayer1Units();
			else return null; // Should not reach here as all units on the board are either part of player1's or player2's List
	}

	//Returns a list of tiles around a specific tile
	public List<Tile> getNearbyTiles(Tile tile){
		List<Tile> tileList = new ArrayList<Tile>();
		for(int i=-1;i<=1;i++){
			for(int j=-1;j<=1;j++){
				try{
					tileList.add(board.getTile(tile.getTilex()+i,tile.getTiley()+j));
				}catch(Exception ignored){}
			}
		}
		return tileList;		
	}

	// Reset highlighting of cards in hand
	public void resetHighlight(ActorRef out) {
	for(int i = 0; i < getPlayerOne().getHand().size(); i++) {
		Card cardOnScreen = getPlayerOne().getHand().get(i);
		BasicCommands.drawCard(out, cardOnScreen, i + 1, 0);
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}

	public int getClickedHandPosition() {
		return clickedHandPosition;
	}

	public void setClickedHandPosition(int clickedHandPosition) {
		this.clickedHandPosition = clickedHandPosition;
	}

	public void PurebladeEnforcerEffect(ActorRef out) {
		List<Unit> units = getBoard().getPlayer1Units();
		units.stream().filter(unit -> unit instanceof PurebladeEnforcer)
				.forEach(unit -> ((PurebladeEnforcer) unit).spellEffect(out));
	}
}
