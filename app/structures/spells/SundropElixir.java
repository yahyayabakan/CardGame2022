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

public class SundropElixir extends Spell{
    public void spell(ActorRef out, GameState gameState, Tile tile){

        tile.getUnit().heal(5);

        //Sets unit health in front end
        BasicCommands.setUnitHealth(out, tile.getUnit(), tile.getUnit().getHealth());

        //if unit is an avatar then player health will be updated and mana front end will change
        if(gameState.getPlayerOne().getPlayerNumber() == 1){
            BasicCommands.setPlayer1Health(out, gameState.getPlayerOne());
            BasicCommands.setPlayer1Mana(out, gameState.getPlayerOne());
        }else{
            BasicCommands.setPlayer2Health(out, gameState.getPlayerTwo());
            BasicCommands.setPlayer1Mana(out, gameState.getPlayerTwo());
        }    
    }   
}