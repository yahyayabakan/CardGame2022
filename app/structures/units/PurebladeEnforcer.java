package structures.units;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Unit;

public class PurebladeEnforcer extends Unit {
    public void spellEffect(ActorRef out) {
        this.setAttack(this.getAttack()+1);
        this.setHealth(this.getHealth()+1);
        BasicCommands.setUnitAttack(out, this, getAttack());
        try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
        BasicCommands.setUnitHealth(out, this, getHealth());
        try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
    }
}
