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