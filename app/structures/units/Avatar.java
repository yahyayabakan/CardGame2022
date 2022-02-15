package structures.units;

import structures.basic.Player;
import structures.basic.Unit;

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
        super.setHealth(player.getHealth());
        super.setAttack(2);
        this.player = player;
    }
}
