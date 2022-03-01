package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.AI;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;

import java.util.Arrays;

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
		//only allow the end turn to be pressed when both game is initialised and clickable.
		if (gameState.gameInitalised && gameState.clickable) {
			//don't allow the user to click while the end turn process performs.
			gameState.clickable = false;


			for(int i = 0; i < gameState.getBoard().getPlayer1Units().size(); i++) {
				gameState.getBoard().getPlayer1Units().get(i).resetMovement();
				gameState.getBoard().getPlayer1Units().get(i).resetAttack();
		}
			for(int i = 0; i < gameState.getBoard().getPlayer2Units().size(); i++) {
				gameState.getBoard().getPlayer2Units().get(i).resetMovement();
				gameState.getBoard().getPlayer2Units().get(i).resetAttack();
				
		}

			// Perform AI actions
			AI.makeMove(out, gameState);
			AI.executeCard(out, gameState);

			//draw the new cards for both players on the backend, then display the player one's cards on front-end.
			gameState.getPlayerOne().draw(out);
			gameState.getPlayerTwo().draw(out);
			gameState.displayCurrentHandCards(out, gameState.getPlayerOne());

			//This should be the last step of the end turn event. It gives both player one and player two their new mana.
			Player playerOne = gameState.getPlayerOne();
			Player playerTwo = gameState.getPlayerTwo();
			gameState.incrementTurn();
			gameState.incrementPlayerMana(playerOne);
			gameState.incrementPlayerMana(playerTwo);
			BasicCommands.setPlayer1Mana(out, playerOne);
			BasicCommands.setPlayer2Mana(out, playerTwo);

			// Buffer to allow AI actions displaying before player actions
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

			//allow the user to click once the process ends.
			gameState.clickable = true;
			BasicCommands.addPlayer1Notification(out, "Your turn", 2);
		}
	}

}
