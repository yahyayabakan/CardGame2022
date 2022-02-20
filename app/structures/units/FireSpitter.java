package structures.units;


import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.EffectAnimation;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class FireSpitter extends Unit {

    /**
     * Has the ranged attack Ability
     * @param unit the unit to attack.
     * @param gameState current state of the game.
     * @param out reference to the game actor.
     */

    public FireSpitter(){
        super.BASE_ATTACK_RANGE = 15;
    }

    @Override
    public void attack(Unit unit, GameState gameState, ActorRef out) {
        if(!this.hasAttacked) {
			
            EffectAnimation projectile = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
            Position attacker, enemy;
            attacker = this.getPosition();
            enemy = unit.getPosition();
            Tile attackerTile = gameState.getBoard().getTile(attacker.getTilex(), attacker.getTiley());
            Tile enemyTile = gameState.getBoard().getTile(enemy.getTilex(), enemy.getTiley());

            BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);
            BasicCommands.playProjectileAnimation(out, projectile, 0, attackerTile, enemyTile);
            try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			unit.takeDamage(this.getAttack(), gameState, out);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            unit.counter(this, gameState, out);
			hasAttacked = true;
			hasMoved = true;
		}
		else System.out.println("Unit already attacked!");
    }

    @Override
    public void displayMovementTiles(ActorRef out, Tile tile, Board board) {
        super.displayMovementTiles(out, tile, board);
        if(!this.getHasAttacked())
            this.displayInRangeAttackTiles(out, tile, board); 
    }

    @Override
    public void attackMoveUnit(Tile tile, ActorRef out, GameState gameState){
        this.attack(tile.getUnit(), gameState, out);
        System.out.println("AttackMove");
    }

    

}
