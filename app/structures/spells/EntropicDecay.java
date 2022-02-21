package structures.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.units.Avatar;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class EntropicDecay{
    public void spell(ActorRef out, GameState gameState, Tile tile){
        //First check whethet it is not an avatar
        //Then set the health to 0
        if(!(tile.getUnit() instanceof Avatar)){
            tile.getUnit().setHealth(0); 
            
            //Modifiying front end
            BasicCommands.setUnitHealth(out, tile.getUnit(), tile.getUnit().getHealth());
            
            //Entropic Decay animation
            BasicCommands.playEffectAnimation(out, BasicObjectBuilders.loadEffect(StaticConfFiles.f1_martyrdom), tile);
            try {Thread.sleep(250);} catch (InterruptedException e) {e.printStackTrace();}

            //Delete unit from tile
            //I am not sure whether it is necessary or not
            BasicCommands.deleteUnit(out, tile.getUnit());
        }
    }
}