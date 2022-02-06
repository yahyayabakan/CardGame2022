package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CheckMoveLogic;
import demo.CommandDemo;
import structures.GameState;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		//gameState.gameInitalised = true;
		
		// User 1 makes a change
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//CheckMoveLogic.executeDemo(out);
		createTileGrid(out, gameState);
	}

	/**
	 * Helper method used to print all the Tiles to the front-end grid.
	 * @param gameState - Holds information on the game state.
	 * @param out - Reference to the event.
	 */
	private void createTileGrid(ActorRef out, GameState gameState){
		int height = gameState.getBoard().getHeight();
		int width = gameState.getBoard().getWidth();
		for(int x = 0; x < height; x++){
			for(int y = 0; y < width; y++){
				BasicCommands.drawTile(out,
						gameState.getBoard().getTile(x,y),
						0);
			}
		}
	}
}


