package structures;

import akka.actor.ActorRef;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.units.Avatar;

import java.util.*;

/**
 * This class is used to implement AI opponent.
 *
 * @author the team
 */
public class AI {

    private static class Action{
        Tile move; double score;

        public Action(Tile move, double score){
            this.move = move; this.score = score;
        }
    }

    public static void makeMove(ActorRef out, GameState gameState){
        Board board = gameState.getBoard();
        List<Unit> AIunits = board.getPlayer2Units();
        List<Action> actions = new ArrayList<>();;

        for(Unit unit : AIunits){
            actions.clear();
            gameState.drawDefaultTilesGrid(out);
            board.getHighlightedTiles().clear();
            Tile unitTile = board.getTile(unit.getPosition().getTilex(), unit.getPosition().getTiley());
            board.setLastTile(unitTile);
            unit.displayMovementTiles(out, unitTile, board);
            List<Tile> highlightedTiles = board.getHighlightedTiles();
            for(Tile move : highlightedTiles){
                double score = calculateScore(unit, move);
                actions.add(new Action(move, score));
            }
            Action action = actions.parallelStream()
                    .max(Comparator.comparing(a -> a.score))
                    .orElse(null);

            if(action != null){
                if(action.move.getUnit() != null){
                    unit.attackMoveUnit(action.move, out, gameState);
                }
                else unit.moveUnit(action.move, out, gameState);
            }
        }
        gameState.drawDefaultTilesGrid(out);
    }

    private static double calculateScore(Unit unit, Tile tile) {
        double score = 0.0;
        if (unit instanceof Avatar) {
            if (tile.getUnit() != null) {
                Unit enemy = tile.getUnit();
                if (enemy instanceof Avatar) {
                    score = (enemy.getHealth() - unit.getAttack() < 1) ?
                            100.0 : ((unit.getHealth() - enemy.getAttack() < 1) ?
                            -100.0 : ((unit.getAttack() * 0.2) + (enemy.getAttack() * 0.2)));
                } else {
                    score = (enemy.getHealth() - unit.getAttack() < 1) ?
                            (enemy.getAttack() + enemy.getMAX_HEALTH()) / 2.0 : ((unit.getHealth() - enemy.getAttack() < 1) ?
                            -100.0 : (enemy.getAttack() * 0.2));
                }
            }
        } else {
            if (tile.getUnit() != null) {
                Unit enemy = tile.getUnit();
                if (enemy instanceof Avatar) {
                    score = (enemy.getHealth() - unit.getAttack() < 1) ?
                            100.0 : ((unit.getHealth() - enemy.getAttack() < 1) ?
                            (unit.getAttack() + unit.getMAX_HEALTH()) / 2.0 : (enemy.getAttack() * 0.2));
                } else {
                    score = (enemy.getHealth() - unit.getAttack() < 1) ?
                            (enemy.getAttack() + enemy.getHealth()) / 2.0 : ((unit.getHealth() - enemy.getAttack() < 1) ?
                            (unit.getAttack() + unit.getMAX_HEALTH()) / 2.0 : (enemy.getAttack() * 0.2));
                }
            }
        }
        return score;
    }
}