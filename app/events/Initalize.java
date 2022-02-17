package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.units.*;
import utils.BasicObjectBuilders;
import utils.CustomizedBuilders;
import utils.StaticConfFiles;

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

		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		//draw the initial 3 cards for both players.
		for(int i = 0; i < STARTING_HAND; i++){
			playerOne.draw(out);
			playerTwo.draw(out);
		}

		//show the hand to player one on the front end.
		gameState.displayCurrentHandCards(out, playerOne);

		//create avatars for both players.
		Avatar avatarOne = (Avatar) BasicObjectBuilders.loadUnit(
				StaticConfFiles.humanAvatar,
				gameState.getNewUnitID(),
				Avatar.class);
		try {Thread.sleep(250);} catch (InterruptedException e) {e.printStackTrace();}
		assert avatarOne != null;
		avatarOne.setupAvatar(playerOne);

		Avatar avatarTwo = (Avatar) BasicObjectBuilders.loadUnit(
				StaticConfFiles.aiAvatar,
				gameState.getNewUnitID(),
				Avatar.class);
		try {Thread.sleep(250);} catch (InterruptedException e) {e.printStackTrace();}
		assert avatarTwo != null;
		avatarTwo.setupAvatar(playerTwo);
		//designated tiles for each avatar.
		Tile avatarOneTile = gameState.getBoard().getTile(1,2);
		Tile avatarTwoTile = gameState.getBoard().getTile(7,2);

		//display the avatars to frontend and add them to the tiles.
		avatarOne.summon(out, avatarOneTile, gameState.getPlayerOne(), gameState.getBoard());
		avatarTwo.summon(out, avatarTwoTile, gameState.getPlayerTwo(), gameState.getBoard());

		//game initialised and clickable should be set to true only at the end.
		gameState.gameInitalised = true;
		gameState.clickable = true;
	}
}


