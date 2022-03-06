package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
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

        for(int i = 0; i < AIunits.size(); i++){
            actions.clear();
            gameState.drawDefaultTilesGrid(out);
            board.getHighlightedTiles().clear();
            Tile unitTile = board.getTile(AIunits.get(i).getPosition().getTilex(), AIunits.get(i).getPosition().getTiley());
            board.setLastTile(unitTile);
            AIunits.get(i).displayMovementTiles(out, unitTile, gameState);
            List<Tile> highlightedTiles = board.getHighlightedTiles();
            for(Tile move : highlightedTiles){
                double score = calculateScore(AIunits.get(i), move);
                actions.add(new Action(move, score));
            }
            Action action = actions.stream()
                    .max(Comparator.comparing(a -> a.score))
                    .orElse(null);

            if(action != null){
                if(action.move.getUnit() != null){
                    if(gameState.getNearbyTiles(unitTile).contains(action.move)){
                        if(gameState.getBoard().getPlayer1Units().contains(action.move.getUnit())){
                            AIunits.get(i).attack(action.move.getUnit(), gameState, out);
                            System.out.println("Here1");
                        }
                    }else{
                        AIunits.get(i).attackMoveUnit(action.move, out, gameState);
                        System.out.println("Here 2");
                    }    
                }
                else AIunits.get(i).moveUnit(action.move, out, gameState);
            }
            try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
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

    public static void executeCard(ActorRef out, GameState gameState){
        int[] cardComboIndex = AI.findOptimalCardCombo(gameState);
        Tile executionTile;

        if(!gameState.gameOver) {
            if (cardComboIndex != null) {
                for (int index : cardComboIndex) {
                    Card theCard = gameState.getPlayerTwo().getHand().get(index);
                    if (theCard.getBigCard().getHealth() != -1) {
                        // If it is a unit
                        executionTile = AI.findUnitSummoningTile(gameState, theCard);
                        if (executionTile != null) theCard.execute(out, gameState, executionTile);
                    } else {
                        // If it is a spell
                        if (theCard.getCardname().equals("Entropic Decay")) {
                            executionTile = AI.findEntropicDecayTile(gameState);
                            if (executionTile != null) theCard.execute(out, gameState, executionTile);
                        }
                        if (theCard.getCardname().equals("Staff of Y'Kir'")) {
                            executionTile = AI.findStaffOfYkirTile(gameState);
                            if (executionTile != null) theCard.execute(out, gameState, executionTile);
                        }
                    }
                    // Deduct mana
                    gameState.getPlayerTwo().setMana(gameState.getPlayerTwo().getMana() - theCard.getManacost());
                    BasicCommands.setPlayer2Mana(out, gameState.getPlayerTwo());
                }
                for (Card card : gameState.getPlayerTwo().getHand()) {
                    System.out.println(card.getCardname());
                }
                // Remove all executed cards in hand
                for (int i = 0; i < cardComboIndex.length; i++) {
                    gameState.getPlayerTwo().getHand().remove(cardComboIndex[i]);
                    for (int j = i + 1; j < cardComboIndex.length; j++) {
                        cardComboIndex[j]--;
                    }
                }
            }
        }
    }

    private static Tile findEntropicDecayTile(GameState gameState){
        List<Unit> enemyUnits;
        enemyUnits = gameState.getBoard().getPlayer1Units();

        for(int i=0;i<enemyUnits.size();i++){
            if(!(enemyUnits.get(i) instanceof Avatar) && enemyUnits.get(i).getHealth()>=8){
                return gameState.getBoard().getTile(enemyUnits.get(i).getPosition().getTilex(), enemyUnits.get(i).getPosition().getTiley());
            }
        }
        return null;
    }

    private static Tile findStaffOfYkirTile(GameState gameState){
        List<Unit> enemyUnits = new ArrayList<Unit>();
        enemyUnits = gameState.getBoard().getPlayer1Units();

        List<Unit> friendlyUnits = new ArrayList<Unit>();
        friendlyUnits = gameState.getBoard().getPlayer2Units();

        for(int i=0;i<friendlyUnits.size();i++){
            if(friendlyUnits.get(i) instanceof Avatar) {
                if(enemyUnits.size()<4){
                    return gameState.getBoard().getTile(friendlyUnits.get(i).getPosition().getTilex(), friendlyUnits.get(i).getPosition().getTiley());        
                }      
            }
        }return null; 
    }
    

    /**
	 * Prototype for placing the unit on a tile.
     * */

    private static Tile findUnitSummoningTile(GameState gameState, Card card){
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
            List<Tile> tileList = new ArrayList<Tile>();
            for(int i=0;i<friendlyUnits.size();i++){
                    Position friendlyPos= friendlyUnits.get(i).getPosition();
                    refTile = gameState.getBoard().getTile(friendlyPos.getTilex(), friendlyPos.getTiley());
                    //tileList = gameState.getNearbyTiles(refTile);
                    tileList.addAll(gameState.getNearbyTiles(refTile));
                    
                }
                Tile maxTile = null;
                for(int i=0;i<tileList.size(); i++){
                    if(maxTile == null){
                        if(tileList.get(i).getUnit() == null){
                            maxTile = tileList.get(i);
                        }
                    }
                    if(tileList.get(i).getUnit() == null){
                        if(tileList.get(i).getTilex() > maxTile.getTilex()) {
                            maxTile = tileList.get(i);
                            
                        }
                    }
                }
                return maxTile;
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

    // Calculate the card score
    private static double calculateCardScore(GameState gameState, Card card){
        double score;
        // Prioritize Entropic Decay if there is an enemy with health greater than or equal to 8
        if(card.getCardname().equals("Entropic Decay")){
            for(Unit unit: gameState.getBoard().getPlayer1Units()){
                if(unit.getHealth() >= 8) return 99;
            }
        }
        // Prioritize Staff of Y'Kir' if there is less than or equal to 4 cards in the deck
        if(card.getCardname().equals("Staff of Y'Kir'")){
            if(gameState.getPlayerTwo().getDeck().getCards().size() >= 4){
                return 99;
            }
        }
        // Unit score is the sum of health and attack point
        return card.getBigCard().getHealth() + card.getBigCard().getAttack();
    }

    // Find the optimal combination of cards to be executed based on mana and score using algorithm for 0-1 Knapsack Problem
    private static int[] findOptimalCardCombo(GameState gameState){

        int currentMana = gameState.getPlayerTwo().getMana();
        List<Card> currentHand = gameState.getPlayerTwo().getHand();

        // Matrix to store card combination scores
        double[][] cardComboScores = new double[gameState.getPlayerTwo().getHand().size() + 1][gameState.getPlayerTwo().getMana() + 1];
        // Matrix to store card combination indexes
        int[][][] cardComboIndexes = new int[gameState.getPlayerTwo().getHand().size() + 1][gameState.getPlayerTwo().getMana() + 1][];

        // Iterate the cards in the hand
        for(int i = 0; i <= currentHand.size(); i++){
            // Iterate the current mana
            for(int j = 0; j <= currentMana; j++){
                if(i == 0 || j == 0){
                    cardComboScores[i][j] = 0;
                } else if(currentHand.get(i-1).getManacost() <= j){
                    // Option 1: use the previous score
                    double option1Score = cardComboScores[i-1][j];
                    // Option 2: use the current score + the max score of reminding mana
                    double option2Score = calculateCardScore(gameState, currentHand.get(i-1)) +
                            cardComboScores[i-1][j-currentHand.get(i-1).getManacost()];
                    // Their max is the current score
                    cardComboScores[i][j] = Math.max(option1Score, option2Score);
                    // Save the combo indexes
                    if(option1Score > option2Score){
                        cardComboIndexes[i][j] = cardComboIndexes[i-1][j];
                    } else {
                        int[] previousMaxIndexes = cardComboIndexes[i-1][j-currentHand.get(i-1).getManacost()];
                        int[] currentIndexes;
                        if(previousMaxIndexes != null){
                            currentIndexes = new int[previousMaxIndexes.length + 1];
                            for(int k = 0; k < previousMaxIndexes.length; k++){
                                currentIndexes[k] = previousMaxIndexes[k];
                            }
                            currentIndexes[previousMaxIndexes.length] = i-1;
                        } else {
                            currentIndexes = new int[]{i-1};
                        }
                        cardComboIndexes[i][j] = currentIndexes;
                    }
                } else {
                    // If mana is not enough for current card, use the previous max score
                    cardComboScores[i][j] = cardComboScores[i-1][j];
                    cardComboIndexes[i][j] = cardComboIndexes[i-1][j];
                }
            }
        }
        // Return the indexes with maximum score
        return cardComboIndexes[gameState.getPlayerTwo().getHand().size()][gameState.getPlayerTwo().getMana()];
    }


}