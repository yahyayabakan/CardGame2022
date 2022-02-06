package structures;

import structures.basic.Board;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 * Extended by the team.
 */
public class GameState {
	private Board board;

	public boolean gameInitalised = false;
	public boolean something = false;

	public GameState(){
		board = new Board();
	}

	public Board getBoard() {
		return board;
	}
}
