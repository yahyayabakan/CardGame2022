package structures.units;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

import java.util.ArrayList;
import java.util.List;

/*This is the Blaze Hound Class which When this unit is
summoned, both players draw a card.
*/
public class BlazeHound extends Unit {

    /**
     * When this unit is summoned, both player's draw a card.
     * @param out game actor reference
     * @param tile the tile on which to display.
     * @param player the player to who the unit belongs to.
     * @param board current state of the board.
     */
    @Override
    public void summon(ActorRef out, Tile tile, Player player, Board board) {
        super.summon(out, tile, player, board);
        List<Unit> units = new ArrayList<>(board.getPlayer1Units());
        units.addAll(board.getPlayer2Units());
        units.stream().parallel()
                .filter(unit -> unit instanceof Avatar)
                .forEach(unit -> {
                    Player n = ((Avatar) unit).getPlayer();
                    n.draw(out);
                    if(n.getPlayerNumber() == 1) new GameState().displayCurrentHandCards(out, n);
                });
    }
}
