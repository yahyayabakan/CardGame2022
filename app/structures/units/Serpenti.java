package structures.units;

import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import akka.actor.ActorRef;

public class Serpenti extends Unit{

    final int MAX_ATTACK_TURN = 2;
    int attackTurn = 0;

    /**
     * Can attack twice per turn
     * @param unit the unit to attack.
     * @param gameState current state of the game.
     * @param out reference to the game actor.
     */
    @Override
    public void attack(Unit unit, GameState gameState, ActorRef out) {
        super.attack(unit, gameState, out);
        attackTurn++;
        //if the attack turn is less than 2, reset hasAttack
        if(attackTurn < MAX_ATTACK_TURN){
            hasAttacked = false;
            hasMoved = true;
        }
    }

}