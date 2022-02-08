package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Player;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 * Extended by team.
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		gameState.gameInitalised = true;
		Player playerOne = gameState.getPlayerOne();
		Player playerTwo = gameState.getPlayerTwo();
		
		// User 1 makes a change
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//CheckMoveLogic.executeDemo(out);

		//Generate the default grid tiles.
		gameState.drawDefaultTilesGrid(out);
		//start first turn.
		gameState.incrementTurn();
		//increment both player's mana according to turn.
		gameState.incrementPlayerMana(playerOne);
		gameState.incrementPlayerMana(playerTwo);
		//set the mana and health on the front-end.
		BasicCommands.setPlayer1Health(out, playerOne);
		BasicCommands.setPlayer2Health(out, playerTwo);
		BasicCommands.setPlayer1Mana(out, playerOne);
		BasicCommands.setPlayer2Mana(out, playerTwo);
	}
}


