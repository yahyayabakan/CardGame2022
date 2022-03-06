package structures.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Spell;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import structures.basic.Unit;

public class Truestrike extends Spell{
    public void spell(ActorRef out, GameState gameState, Tile tile){

        //Truestrike animation
        BasicCommands.playEffectAnimation(out, BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation), tile);
        try {Thread.sleep(250);} catch (InterruptedException e) {e.printStackTrace();}

        //Deals 2 damage to enemy unit
        tile.getUnit().takeDamage(2, gameState, out);

        //Setting front end unit health will be handled by takeDamage()
        
        //if unit is an avatar then player health will be updated and mana front end will change
        //according to the Avatar.takeDamage()

        //delete unit logic will be handled by Unit.takaDamage()

    }
}