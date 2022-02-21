package structures.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.units.Avatar;

public class EntropicDecay{
    public void spell(ActorRef out, GameState gameState, Tile tile){
        //First check whethet it is not an avatar
        //Then set the health to 0
        if(!(tile.getUnit() instanceof Avatar)){
            tile.getUnit().setHealth(0);           
        }
    }
}