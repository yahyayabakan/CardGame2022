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


			gameState.resetHighlight(out);
            Card card = gameState.getPlayerOne().getHand().get(handPosition-1);
            BasicCommands.drawCard(out, card, handPosition, 1);
            try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace(); }
            
	
		}
	}

}
