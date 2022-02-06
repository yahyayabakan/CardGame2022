package structures;

import commands.BasicCommands;
import structures.basic.Board;
import akka.actor.ActorRef;

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
}
