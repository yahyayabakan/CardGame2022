package structures.basic;

import java.util.Deque;
import java.util.LinkedList;

/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 * Extended by team.
 */
public class Player {

	int health;
	int mana;
	Deque<Card> deck;
	LinkedList<Card> hand;
	
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
	public Player(int health, int mana, Deque<Card> deck){
		this.health = health;
		this.mana = mana;
		this.deck = deck;
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
	
	
	
}
