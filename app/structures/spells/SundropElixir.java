package structures.spells;

import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Player;
import structures.basic.Spell;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/* This is the Sundrop Elixir Class that will add +5 health to a
Unit. This cannot take a unit over its starting health value.
*/
public class SundropElixir extends Spell{
    public void spell(ActorRef out, GameState gameState, Tile tile){

        //Buff animation
        BasicCommands.playEffectAnimation(out, BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff), tile);
        try {Thread.sleep(250);} catch (InterruptedException e) {e.printStackTrace();}

        tile.getUnit().heal(5);

        //Sets unit health in front end
        BasicCommands.setUnitHealth(out, tile.getUnit(), tile.getUnit().getHealth());

        //if unit is an avatar then player health will be updated and mana front end will change
        if(gameState.clickable){
            BasicCommands.setPlayer1Health(out, gameState.getPlayerOne());
            BasicCommands.setPlayer1Mana(out, gameState.getPlayerOne());
        }else{
            BasicCommands.setPlayer2Health(out, gameState.getPlayerTwo());
            BasicCommands.setPlayer2Mana(out, gameState.getPlayerTwo());
        }

    }   
}