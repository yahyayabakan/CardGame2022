package structures.units;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Player;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

import java.util.List;

//This is the Avatar class.

public class Avatar extends Unit {
    private Player player;

    /**
     * Set both the attack and health of avatar using player's attack.
     * This will also set the MAX_HEALTH to player's initial health which by default is 20.
     * Then, it binds the player reference to this avatar class.
     * @param player the player who this avatar represents
     * @see Player
     */
    public void setupAvatar(Player player){
        super.setHealthWithMax(player.getHealth());
        super.setAttack(2);
        this.player = player;
    }

    public Player getPlayer(){
        return player;
    }
    /**
     * Whenever the avatar is healed, the player should be healed too.
     * @param amount the amount by which to increase health
     */
    @Override
    public void heal(int amount){
        super.heal(amount);
        player.setHealth(this.getHealth());
    }
    /**
     * The avatar's health is linked to player, thus it has to considered when it takes damage.
     * Otherwise it is the same implementation as unit.
     * @param damage amount of damage
     * @param gameState the current state of the game.
     * @param out game actor reference
     * @see Unit
     */
    @Override
    public void takeDamage(int damage, GameState gameState, ActorRef out){
        super.takeDamage(damage, gameState, out);
        if(this.getHealth() < 1){
            player.setHealth(0);
            if(player.getPlayerNumber() == 1){
                BasicCommands.setPlayer1Health(out, player);
                BasicCommands.addPlayer1Notification(out, "YOU LOSE!", 10000);
            }
            else {
                BasicCommands.setPlayer2Health(out, player);
                BasicCommands.addPlayer1Notification(out, "YOU WIN!", 10000);
            }
            //game ends and thus should no longer be clickable
            gameState.gameOver = true;
            gameState.clickable = false;
            //stop all units on board
            gameState.stopAllUnit();
        }
        else{
            player.setHealth(this.getHealth());
            if(player.getPlayerNumber() == 1){
                BasicCommands.setPlayer1Health(out, player);
                //SilverguardKnight effect
                gameState.getBoard().getPlayer1Units().parallelStream()
                        .filter(unit -> unit instanceof SilverguardKnight)
                        .forEach(unit -> {
                            SilverguardKnight knight = (SilverguardKnight) unit;
                            unit.setAttack(unit.getAttack()+2);
                            try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
                            BasicCommands.setUnitAttack(out, knight, knight.getAttack());
                        });
            }
            else {
                BasicCommands.setPlayer2Health(out, player);
            }
        }
    }
}
