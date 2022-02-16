package structures.units;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Board;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

import java.util.List;

public class AzureHerald extends Unit {

    /**
     * When this unit is summoned, heal the avatar.
     * @param out game actor reference
     * @param tile the tile on which to display.
     * @param player the player to who the unit belongs to.
     * @param board current state of the board.
     */
    @Override
    public void summon(ActorRef out, Tile tile, Player player, Board board){
        super.summon(out, tile, player, board);
        List<Unit> units = null;
        if(player.getPlayerNumber() == 1){
            units = board.getPlayer1Units();
        }
        else units = board.getPlayer2Units();
        Avatar avatar = null;
        for(Unit unit : units){
            if(unit instanceof Avatar){
                avatar = (Avatar) unit;
            }
        }
        assert avatar != null;
        avatar.heal(3);
        BasicCommands.setUnitHealth(out, avatar, avatar.getHealth());
        if(player.getPlayerNumber() == 1){
            BasicCommands.setPlayer1Health(out, player);
        }
        else BasicCommands.setPlayer2Health(out, player);
    }
}
