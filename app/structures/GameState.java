package structures;

import commands.BasicCommands;
import structures.basic.Board;
import akka.actor.ActorRef;
import structures.basic.Deck;
import structures.basic.Player;

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


	public boolean gameInitalised = false;
	public boolean something = false;

	public GameState(){
		turn = 0;
		board = new Board();
		playerOneDeck = new Deck(1);
		playerTwoDeck = new Deck(2);
		playerOne = new Player(20, 0, playerOneDeck);
		playerTwo = new Player(20, 0, playerTwoDeck);
	}

	public Board getBoard() {
		return board;
	}

	public Player getPlayerOne(){
		return playerOne;
	}

	public Player getPlayerTwo(){
		return playerTwo;
	}

	/**
	 * Method should be called before the next turn.
	 */
	public void incrementTurn(){
		turn++;
	}
	/**
	 * The default grid visual representation of the tiles on the front end.
	 * This creates the full grid with the base colour tiles.
	 * It should be used the first time the game loads.
	 * Afterwards, it can be used anytime the grid needs to be reset to regular color.
	 * For example, if tiles are highlighted to show movement, once movement is complete,
	 * this method can be called to return the grid colours to regular size.
	 * @param out - Game Actor reference.
	 */
	public void drawDefaultTilesGrid(ActorRef out){
		int X = getBoard().getX();
		int Y = getBoard().getY();
		for(int x = 0; x < X; x++){
			for(int y = 0; y < Y; y++){
				BasicCommands.drawTile(out,
						getBoard().getTile(x,y),
						0);
			}
		}
	}

	/**
	 * Method increments the player's mana to turns plus one.
	 * This should be used after each turn change on both players.
	 * @param player - Player to set mana.
	 */
	public void incrementPlayerMana(Player player){
		player.setMana(turn+1);
	}
}

// a test comment
