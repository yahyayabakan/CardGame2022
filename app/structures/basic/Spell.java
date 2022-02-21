package structures.basic;

import akka.actor.ActorRef;
import structures.GameState;

public class Spell{

    /** All spell card classes implement Spell class and
     * @override this method with their unique effect
     * by default does nothing
     */
    public void spell(ActorRef out, GameState gameState, Tile tile){
    }
}