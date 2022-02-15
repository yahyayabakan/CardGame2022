package structures.units;

import structures.basic.Unit;

public class Avatar extends Unit {
    private int health = 20;
    private int attack = 2;

    //override health & attack getters and setters as no BigCard is mapped for Avatar
    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {

        this.health = health;
    }

    @Override
    public int getAttack() {
        return attack;
    }

    @Override
    public void setAttack(int attack) {
        this.attack = attack;
    }
}
