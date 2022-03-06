package structures.units;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;


/*This is the WindShrike Class Flying: Can move
anywhere on the board When this unit dies, its owner draws a card
*/
public class WindShrike extends Unit {
    //constructor adds the BASE_MOVEMENT.
    public WindShrike(){
        super.BASE_MOVEMENT = 15;
    }


    /**
     * Highlights all the movable and enemy tiles in the whole board
     * @param tile clicked tile
     * @param gameState the current state of the game.
     * @param out game actor reference
     */
    @Override
    public void displayMovementTiles(ActorRef out, Tile tile, GameState gameState) {
        if(hasAttacked) hasMoved = true;
        if(nexToProvokeUnit(tile,gameState.getBoard(), out)){}
        else if(!hasMoved) {
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 5; y++) {
                    Tile theTile = gameState.getBoard().getTile(x, y);
                    if (theTile.getUnit() == null) {
                        gameState.getBoard().getHighlightedTiles().add(theTile);
                    }
                    if (gameState.getBoard().getEnemyUnits(this).contains(theTile.getUnit())) {
                        gameState.getBoard().getHighlightedTiles().add(theTile);
                    }
                }
            }
        }
        // Display highlighted tiles
        for(Tile t: gameState.getBoard().getHighlightedTiles()){
            if(gameState.getBoard().getEnemyUnits(tile.getUnit()).contains(t.getUnit())){
                BasicCommands.drawTile(out,t, 2);
                try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
            } else {
                BasicCommands.drawTile(out,t, 1);
                try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
            }
        }
    }

    /**
     * Performs the same tasks as the parent class, yet at the end adds a card to player if it dies.
     * @param damage amount of damage
     * @param gameState the current state of the game.
     * @param out game actor reference
     */
    @Override
    public void takeDamage(int damage, GameState gameState, ActorRef out){
        Player player = null;
        if(gameState.getBoard().getPlayer1Units().contains(this)){
            player = gameState.getPlayerOne();
        }
        else player = gameState.getPlayerTwo();
        super.takeDamage(damage, gameState, out);
        if(this.getHealth() < 1) {
            player.draw(out);
            if (player.getPlayerNumber() == 1) gameState.displayCurrentHandCards(out, player);
        }
    }
}
