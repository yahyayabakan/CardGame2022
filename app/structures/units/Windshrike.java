package structures.units;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Unit;

public class Windshrike extends Unit {
    //constructor adds the BASE_MOVEMENT.
    public Windshrike(){
        super.BASE_MOVEMENT = 15;
    }

    /**
     * Performs the same tasks as the parent class, yet at the end adds a card to player if it dies.
     * @param damage amount of damage
     * @param gameState the current state of the game.
     * @param out game actor reference
     */
    @Override
    public void takeDamage(int damage, GameState gameState, ActorRef out){
        Player player = null;
        if(gameState.getBoard().getPlayer1Units().contains(this)){
            player = gameState.getPlayerOne();
        }
        else player = gameState.getPlayerTwo();
        super.takeDamage(damage, gameState, out);
        if(this.getHealth() < 1) {
            player.draw(out);
            if (player.getPlayerNumber() == 1) gameState.displayCurrentHandCards(out, player);
        }
    }
}
