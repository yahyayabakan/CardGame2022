package structures.units;

import structures.GameState;
import structures.basic.Unit;
import akka.actor.ActorRef;

public class AzuriteLion extends Unit {

    /**
     * Can attack twice per turn
     * @param unit the unit to attack.
     * @param gameState current state of the game.
     * @param out reference to the game actor.
     */
    @Override
    public void attack(Unit unit, GameState gameState, ActorRef out) {
        super.attack(unit, gameState, out);
        super.attack(unit, gameState, out);
    }
}
