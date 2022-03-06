package structures.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Spell;
import structures.basic.Tile;
import structures.units.Avatar;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class StaffofYKir extends Spell{
    public void spell(ActorRef out, GameState gameState, Tile tile){

        //Buff animation
        BasicCommands.playEffectAnimation(out, BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff), tile);
        try {Thread.sleep(250);} catch (InterruptedException e) {e.printStackTrace();}

        //Checks first whether unit is an avatar or not
        //Then it increases the attacak attribute +2
        if(tile.getUnit() instanceof Avatar){
            tile.getUnit().setAttack((tile.getUnit().getAttack()) + 2);

            //Update front end avatar attack
            BasicCommands.setUnitAttack(out, tile.getUnit(), tile.getUnit().getAttack());

        }


    }
}