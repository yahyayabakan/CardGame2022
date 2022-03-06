package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;

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
		//only allow clicking once the game is initialised.
		if (gameState.gameInitalised && gameState.clickable) {
			int tilex = message.get("tilex").asInt();
			int tiley = message.get("tiley").asInt();

			//reference to the clicked tile.
			Tile clickedTile = gameState.getBoard().getTile(tilex, tiley);
			//previously clicked tile.
			Tile previouslyClicked = gameState.getBoard().getLastTile();

			// Reset clicked card when player clicked on tile without highlighting
			if(clickedTile.getUnit() != null &&
					!gameState.getBoard().getHighlightedTiles().contains(clickedTile)){
				gameState.setClickedHandPosition(-1);
			}

			// If no card is clicked, perform move actions
			if(gameState.getClickedHandPosition() == -1) {
			/**
			 * If a friendly unit is clicked, then display the potential movement tiles.
			 * It first runs the drawDefaultTilesGrid() to refresh the board tiles.
			 * This doesn't allow the unit to move to that tile. It simply display available tiles.
			 * It clears the highlighted tiles each time it is called. This is to avoid bugs where previous tiles are
			 * still highlighted in the list.
			 * @see GameState check the drawDefaultTilesGrid()
			 */
			if (clickedTile.getUnit() != null &&
					gameState.getBoard().getPlayer1Units()
							.contains(clickedTile.getUnit())) {
				gameState.getBoard().clearHighlightedTiles();
				gameState.drawDefaultTilesGrid(out);
				Unit unit = clickedTile.getUnit();

                if(unit.getHasMoved() && !unit.getHasAttacked())
					unit.displayInRangeAttackTiles(out,clickedTile, gameState.getBoard());
				else
                	unit.displayMovementTiles(out, clickedTile, gameState);

				//Lets the board know which tile was clicked last
				gameState.getBoard().setLastTile(clickedTile);
			}

			/**
			 * If an empty tile is clicked right after a unit, then the unit is moved to that tile.
			 * If an enemy tile is clicked right after a unit, then the unit is moved in front of the enemy unit as per the validation rules.
			 * if it is a valid tile. It then calls the drawDefaultTilesGrid method to unhighlight all the tiles
			 */

			 // Runs when an Empty tile is clicked
			if (clickedTile.getUnit() == null && previouslyClicked != null) {
				if(previouslyClicked.getUnit() != null && gameState.getBoard().getPlayer1Units().contains(previouslyClicked.getUnit())) {
					gameState.getBoard().getLastTile().getUnit().moveUnit(clickedTile, out, gameState);
					gameState.drawDefaultTilesGrid(out);
				} // Runs when a Tile right next to the unit is clicked
			}else if((previouslyClicked != null) && gameState.getNearbyTiles(previouslyClicked).contains(clickedTile)){
					if(gameState.getBoard().getPlayer2Units().contains(clickedTile.getUnit())){
						previouslyClicked.getUnit().attack(clickedTile.getUnit(), gameState, out);
						gameState.drawDefaultTilesGrid(out);
					}
				// Runs when a Tile at the edge of the highlightedtiles is clicked
			}else if(gameState.getBoard().getPlayer2Units().contains(clickedTile.getUnit()) && gameState.getBoard().getHighlightedTiles().contains(clickedTile)){
					gameState.getBoard().getLastTile().getUnit().attackMoveUnit(clickedTile, out, gameState);
					gameState.drawDefaultTilesGrid(out);
			}
		} else {
				// Execute the card if a card is selected before clicking the tile
				if (gameState.getBoard().getHighlightedTiles().contains(clickedTile)) {
					Card clickedCard = gameState.getPlayerOne().getHand().get(gameState.getClickedHandPosition() - 1);
					clickedCard.execute(out, gameState, clickedTile);
				}
			}

			gameState.getBoard().setLastTile(clickedTile);
		}
	}


}
