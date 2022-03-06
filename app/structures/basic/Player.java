package structures.basic;

import java.util.Deque;
import java.util.LinkedList;

import akka.actor.ActorRef;
import commands.BasicCommands;

/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 * Extended by team.
 */
public class Player {

	private int health;
	private int mana;
	private int playerNumber;
	private Deck deck;
	private LinkedList<Card> hand;
	private static final int HandMax = 6;
	
	public Player() {
		super();
		this.health = 20;
		this.mana = 0;
		deck = null;
		hand = null;
	}
	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
		deck = null;
		hand = null;
	}

	//New constructor.
	public Player(int health, int mana, Deck deck, int playerNumber){
		this.health = health;
		this.mana = mana;
		this.deck = deck;
		this.playerNumber = playerNumber;
		hand = new LinkedList<>();
	}

	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getMana() {
		return mana;
	}
	public void setMana(int mana) {
		this.mana = mana;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public LinkedList<Card> getHand() {
		return hand;
	}
	//Adds a card to the players hand. 
	public void draw(ActorRef out){
		if(hand.size()<HandMax)
			hand.add(deck.draw());
		else{
			deck.draw();
			if(playerNumber == 1)BasicCommands.addPlayer1Notification(out, "Hand Full, Card Lost", 5);
		}
	}

	public Deck getDeck() {
		return deck;
	}
}