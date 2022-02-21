package structures.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Spell;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class TrueStrike extends Spell{
    public void spell(ActorRef out, GameState gameState, Tile tile){
        //Deals 2 damage to enemy unit
        tile.getUnit().setHealth((tile.getUnit().getHealth()-2));

        //Setting front end unit health
        BasicCommands.setUnitHealth(out, tile.getUnit(), tile.getUnit().getHealth());
        
        //if unit is an avatar then player health will be updated and mana front end will change
        if(gameState.clickable){
            BasicCommands.setPlayer1Health(out, gameState.getPlayerOne());
            BasicCommands.setPlayer1Mana(out, gameState.getPlayerOne());
        }else{
            BasicCommands.setPlayer2Health(out, gameState.getPlayerTwo());
            BasicCommands.setPlayer2Mana(out, gameState.getPlayerTwo());
        }

        //Truestrike animation
        BasicCommands.playEffectAnimation(out, BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation), tile);
        try {Thread.sleep(250);} catch (InterruptedException e) {e.printStackTrace();}

        if(tile.getUnit().getHealth() <= 0){
            //Delete unit from tile
            //I am not sure whether it is necessary or not
            BasicCommands.deleteUnit(out, tile.getUnit());
        }


    }
}