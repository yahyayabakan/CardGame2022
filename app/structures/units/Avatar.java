package structures.units;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

import java.util.List;

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
        int healthAfterDamage = this.getHealth()-damage;
        if(healthAfterDamage < 1){
            List<Unit> playerOneUnits = gameState.getBoard().getPlayer1Units();
            List<Unit> playerTwoUnits = gameState.getBoard().getPlayer2Units();
            //it will remove from either list one or two. The remove() method checks whether it contains an object, so there is no need to check manually.
            playerOneUnits.remove(this);
            playerTwoUnits.remove(this);
            //remove it from tile.
            gameState.getBoard().getTile(this.getPosition().getTilex(),this.getPosition().getTiley()).addUnit(null);
            //delete the unit from the front-end.
            BasicCommands.playUnitAnimation(out, this, UnitAnimationType.death);
            try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
            BasicCommands.deleteUnit(out,this);
            //Declare whether the human player has won, or lost.
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
            gameState.clickable = false;
        }
        else{
            //update unit health.
            BasicCommands.setUnitHealth(out, this, healthAfterDamage);
            setHealth(healthAfterDamage);
            //link the damage to player.
            player.setHealth(healthAfterDamage);
            if(player.getPlayerNumber() == 1){
                BasicCommands.setPlayer1Health(out, player);
            }
            else {
                BasicCommands.setPlayer2Health(out, player);
            }
        }
    }
}
