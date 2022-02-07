package structures.basic;

import java.util.Deque;

/**
 * Represents a deck of cards. It is used to create a separate deck for player 1 and player 2.
 * It allows to create different decks and can be easier changed as per requirements later.
 * Additional deck types can be added to constructor.
 * @author The Team.
 */
public class Deck {
    private Deque<Card> cards;

    public Deck(int deckType){
        if(deckType == 1){
            cards = createDeckOne();
        }
        else if(deckType == 2){
            cards = createDeckTwo();
        }
        else cards = null;
    }

    /**
     * The method that creates deck one.
     * @return Deck of cards.
     */
    private Deque<Card> createDeckOne(){
        //TODO
        return null;
    }

    /**
     * The method that creates deck two.
     * @return Deck of cards.
     */
    private Deque<Card> createDeckTwo(){
        //TODO
        return null;
    }

    /**
     * Draw a card from the deck.
     * If there are still cards in the deck, it draws it. Otherwise, returns null.
     * @return Card - last card or null.
     */
    public Card draw() {
        if (!cards.isEmpty()) {
            return cards.getLast();
        }
        return null;
    }
}
