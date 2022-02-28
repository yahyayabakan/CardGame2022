package structures;

import akka.actor.ActorRef;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Position;
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
            Action action = actions.stream()
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

    /**
	 * Prototype for placing the unit on a tile.
     * */

    public Tile placeUnit(GameState gameState, Card card){
        List<Tile> range = new ArrayList<Tile>();
        List<Unit> enemyUnits = new ArrayList<Unit>();
        List<Unit> friendlyUnits = new ArrayList<Unit>();
        Position avatarPos = null;
        enemyUnits = gameState.getBoard().getPlayer1Units();
        friendlyUnits = gameState.getBoard().getPlayer2Units();

        Tile refTile = null;

        //Places the airdrop unit near the enemy avatar
        if(card.getCardname().equals("Planar Scout")){
            for(int i=0;i<enemyUnits.size();i++){
                if(enemyUnits.get(i) instanceof Avatar){
                    avatarPos= enemyUnits.get(i).getPosition();
                    refTile = gameState.getBoard().getTile(avatarPos.getTilex(), avatarPos.getTiley());
                }
            }

            range = gameState.getNearbyTiles(refTile);

            for(int i=0;i<range.size();i++){
                if(range.get(i).getUnit()==null)
                    return range.get(i);
            }
        }
        
        // Places the provoke unit near the AI avatar
        if(card.getCardname().equals("Rock Pulveriser")){
            for(int i=0;i<friendlyUnits.size();i++){
                if(friendlyUnits.get(i) instanceof Avatar){
                    avatarPos= friendlyUnits.get(i).getPosition();
                    refTile = gameState.getBoard().getTile(avatarPos.getTilex(), avatarPos.getTiley());
                }
            }

            range = gameState.getNearbyTiles(refTile);

            for(int i=0;i<range.size();i++){
                if(range.get(i).getUnit()==null)
                    return range.get(i);
            }
        }

        // places the ranged attack unit on the board
        if(card.getCardname().equals("Pyromancer")){
            //call Paul's Method
        }
        // places the remaining units toward the left end of the board 
        for(int x = 0; x < gameState.getBoard().getX(); x++) {
			for (int y = 0; y < gameState.getBoard().getY(); y++) {
                Unit unit = gameState.getBoard().getTile(x, y).getUnit();
                if(friendlyUnits.contains(unit)){
                    range=gameState.getNearbyTiles(gameState.getBoard().getTile(x, y));
                }

                for(int i=0;i<range.size();i++){
                    if(range.get(i).getUnit()==null)
                        return range.get(i);
                }
            }
        }    

        return null;


    }
}