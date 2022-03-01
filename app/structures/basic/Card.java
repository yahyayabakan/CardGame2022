package structures.basic;


import commands.BasicCommands;
import structures.GameState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import akka.actor.ActorRef;
import structures.spells.*;
import structures.units.Avatar;
import utils.BasicObjectBuilders;
import utils.CustomizedBuilders;
import utils.StaticConfFiles;

/**
 * This is the base representation of a Card which is rendered in the player's hand.
 * A card has an id, a name (cardname) and a manacost. A card then has a large and mini
 * version. The mini version is what is rendered at the bottom of the screen. The big
 * version is what is rendered when the player clicks on a card in their hand.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Card {
	
	int id;
	
	String cardname;
	int manacost;
	
	MiniCard miniCard;
	BigCard bigCard;

	double score;
	
	public Card() {};
	
	public Card(int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard) {
		super();
		this.id = id;
		this.cardname = cardname;
		this.manacost = manacost;
		this.miniCard = miniCard;
		this.bigCard = bigCard;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCardname() {
		return cardname;
	}
	public void setCardname(String cardname) {
		this.cardname = cardname;
	}
	public int getManacost() {
		return manacost;
	}
	public void setManacost(int manacost) {
		this.manacost = manacost;
	}
	public MiniCard getMiniCard() {
		return miniCard;
	}
	public void setMiniCard(MiniCard miniCard) {
		this.miniCard = miniCard;
	}
	public BigCard getBigCard() {
		return bigCard;
	}
	public void setBigCard(BigCard bigCard) {
		this.bigCard = bigCard;
	}



	/**
	 * Method to execute the card when clicking on a valid tile
	 * @param out game actor reference
	 * @param gameState the current state of the game.
	 * @param tile tile clicked
	 */
	public void execute(ActorRef out, GameState gameState, Tile tile){
		// If the player has enough mana
		if((gameState.clickable && gameState.getPlayerOne().getMana() >= manacost) ||
				(!gameState.clickable && gameState.getPlayerTwo().getMana() >= manacost)){
			// If it is not a spell card, summon a new unit
			if(bigCard.getHealth() != -1){
				Unit newUnit;
				// Summon animation
				BasicCommands.playEffectAnimation(out, BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon), tile);
				try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
				try {
					newUnit = CustomizedBuilders.loadSummonByName(cardname, gameState);
					// If it is player one
					if(gameState.clickable){
						newUnit.summon(out,tile, gameState.getPlayerOne(), gameState.getBoard());
					} else {
						newUnit.summon(out,tile, gameState.getPlayerTwo(), gameState.getBoard());
					}
				} catch (Exception e){
					e.printStackTrace();
				}
			} else {
				// Execute spell
				Spell spellToCast = CustomizedBuilders.loadSpellByName(cardname);
				spellToCast.spell(out, gameState, tile);
				gameState.PurebladeEnforcerEffect(out);
			}
			// Deduct mana, and delete the executed card in hand
			if(gameState.clickable){
				gameState.getPlayerOne().setMana(gameState.getPlayerOne().getMana() - manacost);
				BasicCommands.setPlayer1Mana(out, gameState.getPlayerOne());
				BasicCommands.deleteCard(out, gameState.getClickedHandPosition());
				gameState.clearCurrentHandCards(out,gameState.getPlayerOne());
				gameState.getPlayerOne().getHand().removeFirstOccurrence(this);
				gameState.displayCurrentHandCards(out,gameState.getPlayerOne());
			} else {
				gameState.getPlayerTwo().setMana(gameState.getPlayerTwo().getMana() - manacost);
				BasicCommands.setPlayer2Mana(out, gameState.getPlayerTwo());
				gameState.getPlayerTwo().getHand().removeFirstOccurrence(this);
			}
			// Reset clicked card
			gameState.setClickedHandPosition(-1);
			// Refresh the board
			gameState.getBoard().clearHighlightedTiles();
			gameState.drawDefaultTilesGrid(out);
		} else {
			if(gameState.clickable){
				BasicCommands.addPlayer1Notification(out, "Insufficient mana", 2);
				try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}

	}


	/**
	 * Method to highlight all the tiles where the clicked card can execute on.
	 * @param out game actor reference
	 * @param gameState the current state of the game.
	 */
	public void displayCardValidTiles(ActorRef out, GameState gameState){

		// refresh the board
		gameState.getBoard().clearHighlightedTiles();
		gameState.drawDefaultTilesGrid(out);

		if(cardname.equals("Ironcliff Guardian") || cardname.equals("Planar Scout")){
			displayAirDropTiles(out, gameState);
		} else if (cardname.equals("Truestrike")) {
			displayTruestrikeTiles(out, gameState);
		} else if (cardname.equals(("Sundrop Elixir"))){
			displaySundropElixirTiles(out, gameState);
		} else if (cardname.equals("Entropic Decay")){
			displayEntropicDecayTiles(out, gameState);
		} else if (cardname.equals("Staff of Y'Kir'")){
			displayStaffOfYKirTiles(out, gameState);
		} else {
			displayNormalSummonTiles(out, gameState);
		}
	}


	// Highlight normal summon tiles
	private void displayNormalSummonTiles(ActorRef out, GameState gameState){

		int[][] directions = new int[][]{{-1,-1}, {-1,0}, {-1,1}, {0,1}, {1,1}, {1,0}, {1,-1}, {0,-1}};

		List<Unit> friendlyUnits;
		if(gameState.clickable == true){
			friendlyUnits = gameState.getBoard().getPlayer1Units();
		} else {
			friendlyUnits = gameState.getBoard().getPlayer2Units();
		}

		for(int x = 0; x < gameState.getBoard().getX(); x++){
			for(int y = 0; y < gameState.getBoard().getY(); y++){
				if(friendlyUnits.contains(gameState.getBoard().getTile(x,y).getUnit())){
					for(int[] direction: directions){
						int nx = x + direction[0];
						int ny = y + direction[1];
						// if the tile is in bound
						if(nx >= 0 && nx < gameState.getBoard().getX()){
							if(ny >= 0 && ny < gameState.getBoard().getY()){
								// if the tile has no unit
								if(gameState.getBoard().getTile(nx, ny).getUnit() == null){
									gameState.getBoard().getHighlightedTiles().add(gameState.getBoard().getTile(nx, ny));
								}
							}
						}
					}
				}
			}
		}
		if(gameState.clickable == true) gameState.getBoard().displayHighlightedTiles(out, 1);
	}


	//Highlight airdrop (all tiles on the board) summon tiles
	private void displayAirDropTiles(ActorRef out, GameState gameState){

		for(int x = 0; x < gameState.getBoard().getX(); x++) {
			for (int y = 0; y < gameState.getBoard().getY(); y++) {
				if(gameState.getBoard().getTile(x, y).getUnit() == null){
					gameState.getBoard().getHighlightedTiles().add(gameState.getBoard().getTile(x, y));
				}
			}
		}
		if(gameState.clickable == true) gameState.getBoard().displayHighlightedTiles(out, 1);
	}


	//Highlight Truestrike (all enemy) tiles
	private void displayTruestrikeTiles(ActorRef out, GameState gameState){

		List<Unit> enemyUnits;
		if(gameState.clickable == true){
			enemyUnits = gameState.getBoard().getPlayer2Units();
		} else {
			enemyUnits = gameState.getBoard().getPlayer1Units();
		}

		for(int x = 0; x < gameState.getBoard().getX(); x++) {
			for (int y = 0; y < gameState.getBoard().getY(); y++) {
				if(enemyUnits.contains(gameState.getBoard().getTile(x, y).getUnit())){
					gameState.getBoard().getHighlightedTiles().add(gameState.getBoard().getTile(x, y));
				}
			}
		}
		if(gameState.clickable == true) gameState.getBoard().displayHighlightedTiles(out, 2);
	}


	//Highlight Entropic Decay (enemy besides avatar) tiles
	private void displayEntropicDecayTiles(ActorRef out, GameState gameState){

		List<Unit> enemyUnits;
		if(gameState.clickable == true){
			enemyUnits = gameState.getBoard().getPlayer2Units();
		} else {
			enemyUnits = gameState.getBoard().getPlayer1Units();
		}

		for(int x = 0; x < gameState.getBoard().getX(); x++) {
			for (int y = 0; y < gameState.getBoard().getY(); y++) {
				// If it is Entropic Decay, only highlight units besides Avatar
				if(enemyUnits.contains(gameState.getBoard().getTile(x, y).getUnit())){
					if(gameState.getBoard().getTile(x, y).getUnit() instanceof Avatar == false){
						gameState.getBoard().getHighlightedTiles().add(gameState.getBoard().getTile(x, y));
					}
				}
			}
		}
		if(gameState.clickable == true) gameState.getBoard().displayHighlightedTiles(out, 2);
	}

	//highlight Sundrop Elixir (all friendly units)  tiles
	private void displaySundropElixirTiles(ActorRef out, GameState gameState){

		List<Unit> friendlyUnits;
		if(gameState.clickable == true){
			friendlyUnits = gameState.getBoard().getPlayer1Units();
		} else {
			friendlyUnits = gameState.getBoard().getPlayer2Units();
		}

		for(int x = 0; x < gameState.getBoard().getX(); x++) {
			for (int y = 0; y < gameState.getBoard().getY(); y++) {
				if(friendlyUnits.contains(gameState.getBoard().getTile(x, y).getUnit())){
					gameState.getBoard().getHighlightedTiles().add(gameState.getBoard().getTile(x, y));
				}
			}
		}
		if(gameState.clickable == true) gameState.getBoard().displayHighlightedTiles(out, 1);
	}


	//highlight Staff of Y’Kir (friendly Avatar only) tiles
	private void displayStaffOfYKirTiles(ActorRef out, GameState gameState){

		List<Unit> friendlyUnits;
		if(gameState.clickable == true){
			friendlyUnits = gameState.getBoard().getPlayer1Units();
		} else {
			friendlyUnits = gameState.getBoard().getPlayer2Units();
		}

		for(int x = 0; x < gameState.getBoard().getX(); x++) {
			for (int y = 0; y < gameState.getBoard().getY(); y++) {
				if(friendlyUnits.contains(gameState.getBoard().getTile(x, y).getUnit())){
					if(gameState.getBoard().getTile(x, y).getUnit() instanceof Avatar){
						gameState.getBoard().getHighlightedTiles().add(gameState.getBoard().getTile(x, y));
					}
				}
			}
		}
		if(gameState.clickable == true) gameState.getBoard().displayHighlightedTiles(out, 1);
	}

}
