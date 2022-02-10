package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		Tile clickedTile = gameState.getBoard().getTile(tilex, tiley);

		if (gameState.something == true) {
			// do some logic
		}

		/**
		 * If a friendly unit is clicked, then display the potential movement tiles.
		 * This doesn't allow the unit to move to that tile. It simply display available tiles.
		 * @TODO Remember that displayMovementTiles is a prototype, thus not working fully.
		 */
		if(clickedTile.getUnit() != null &&
				gameState.getBoard().getPlayer1Units()
						.contains(clickedTile.getUnit())){
			clickedTile.getUnit().displayMovementTiles(out, tilex, tiley, gameState);
		}

	}

}
