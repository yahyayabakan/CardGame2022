package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		if(gameState.gameInitalised && gameState.clickable) {
			int handPosition = message.get("position").asInt();
			Card clickedCard = gameState.getPlayerOne().getHand().get(handPosition-1);

			// Highlight clicked card in hand
			gameState.resetHighlight(out);
            BasicCommands.drawCard(out, clickedCard, handPosition, 1);
            try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace(); }

			// Save clicked hand position to game state
			gameState.setClickedHandPosition(handPosition);

			// Highlight clicked card tiles
			clickedCard.displayCardValidTiles(out, gameState);
		}
	}

}
