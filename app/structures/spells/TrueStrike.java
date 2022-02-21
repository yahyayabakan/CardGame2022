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
    }
}