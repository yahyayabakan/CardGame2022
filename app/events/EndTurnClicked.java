package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Player;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {













		//This should be the last step of the end turn event. It gives both player one and player two their new mana.
		Player playerOne = gameState.getPlayerOne();
		Player playerTwo = gameState.getPlayerTwo();
		gameState.incrementTurn();
		gameState.incrementPlayerMana(playerOne);
		gameState.incrementPlayerMana(playerTwo);
		BasicCommands.setPlayer1Mana(out, playerOne);
		BasicCommands.setPlayer2Mana(out, playerTwo);
		System.out.println(playerOne.getMana());
	}

}
