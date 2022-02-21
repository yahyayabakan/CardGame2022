import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Spell;
import structures.basic.Tile;
import structures.units.Avatar;

public class StaffOfYkir extends Spell{
    public void spell(ActorRef out, GameState gameState, Tile tile){
        //Checks first whether unit is an avatar or not
        //Then it increases the attacak attribute +2
        if(tile.getUnit() instanceof Avatar){
            tile.getUnit().setAttack((tile.getUnit().getAttack()) + 2);
        }

        
    }
}