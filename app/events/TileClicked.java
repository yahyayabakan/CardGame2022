package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

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

		//reference to the clicked tile.
		Tile clickedTile = gameState.getBoard().getTile(tilex, tiley);

		if (gameState.something == true) {
			// do some logic
		}

		/**
		 * If a friendly unit is clicked, then display the potential movement tiles.
		 * It first runs the drawDefaultTilesGrid() to refresh the board tiles.
		 * This doesn't allow the unit to move to that tile. It simply display available tiles.
		 * It clears the highlighted tiles each time it is called. This is to avoid bugs where previous tiles are
		 * still highlighted in the list.
		 * @see GameState check the drawDefaultTilesGrid()
		 */
		if(clickedTile.getUnit() != null &&
				gameState.getBoard().getPlayer1Units()
						.contains(clickedTile.getUnit())){
			gameState.getBoard().clearHighlightedTiles();
			gameState.drawDefaultTilesGrid(out);
			clickedTile.getUnit().displayMovementTiles(out, clickedTile, gameState.getBoard());
			//Lets the board know which tile was clicked last
			gameState.getBoard().setLastTile(clickedTile);
		}

		/**
		 * If an empty tile is clicked right after a unit, then the unit is moved to that tile
		 * if it is a valid tile. It then calls the drawDefaultTilesGrid method to unhighlight all the tiles
		 */

		if(clickedTile.getUnit()==null && gameState.getBoard().getLastTile().getUnit()!=null){
			Boolean movePerformed=false;
			movePerformed = gameState.getBoard().getLastTile().getUnit().moveUnit(clickedTile,out,gameState.getBoard());
			// if the move was perfromed as per the validation rules, the tiles need to be updated
			if(movePerformed){
				clickedTile.addUnit(gameState.getBoard().getLastTile().getUnit());
				gameState.getBoard().getLastTile().removeUnit();
				gameState.drawDefaultTilesGrid(out);
			}
		}
	}
}
