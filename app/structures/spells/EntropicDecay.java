package structures.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.units.Avatar;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import structures.basic.Unit;

public class EntropicDecay{
    public void spell(ActorRef out, GameState gameState, Tile tile){
        //First check whethet it is not an avatar
        //Then use damage method
        int healthToBeDeleted = tile.getUnit().getHealth();
        GameState gs = new GameState();
        gs = gameState;
            if(!(tile.getUnit() instanceof Avatar)){            
            tile.getUnit().takeDamage(healthToBeDeleted, gs, out);
            
            //Modifiying front end will be handled by takeDamage()            
            
            //Entropic Decay animation
            BasicCommands.playEffectAnimation(out, BasicObjectBuilders.loadEffect(StaticConfFiles.f1_martyrdom), tile);
            try {Thread.sleep(250);} catch (InterruptedException e) {e.printStackTrace();}

            //Delete unit from tile will be handled by takeDamage()         
        }
    }
}