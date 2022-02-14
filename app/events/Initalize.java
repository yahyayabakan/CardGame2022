package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.units.Avatar;
import utils.BasicObjectBuilders;

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
		final int STARTING_HAND = 3;
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

		//draw the initial 3 cards for both players.
		for(int i = 0; i < STARTING_HAND; i++){
			playerOne.draw(out);
			playerTwo.draw(out);
		}

		//show the hand to player one on the front end.
		gameState.displayCurrentHandCards(out, playerOne);

		//create avatars for both players and add them to tiles
		Unit avatarOne = BasicObjectBuilders.loadUnit(
				"conf/gameconfs/avatars/avatar1.json",
				gameState.getNewUnitID(),
				Avatar.class);
		Unit avatarTwo = BasicObjectBuilders.loadUnit(
				"conf/gameconfs/avatars/avatar2.json",
				gameState.getNewUnitID(),
				Avatar.class);
		gameState.getBoard().addUnitToPlayer1List(avatarOne);
		gameState.getBoard().addUnitToPlayer2List(avatarTwo);
		gameState.getBoard().getTile(1,2).addUnit(avatarOne);
		gameState.getBoard().getTile(7,2).addUnit(avatarTwo);

		//display the avatars to frontend
		avatarOne.setPositionByTile(gameState.getBoard().getTile(1,2));
		avatarTwo.setPositionByTile(gameState.getBoard().getTile(7,2));
		BasicCommands.drawUnit(out, avatarOne, gameState.getBoard().getTile(1,2));
		BasicCommands.drawUnit(out, avatarTwo, gameState.getBoard().getTile(7,2));

		//game initialised and clickable should be set to true only at the end.
		gameState.gameInitalised = true;
		gameState.clickable = true;
	}
}


