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


/*This is the Pyromancer Enforcer Class has a ranged: Can
attack any enemy on the board

*/
public class Pyromancer extends Unit {

    public Pyromancer(){
        super.BASE_ATTACK_RANGE = 15;
    }


    //It attacks the ranged enemy and plays the projectile animation. 
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
            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			unit.takeDamage(this.getAttack(), gameState, out);
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            unit.counter(this, gameState, out);
			hasAttacked = true;
			hasMoved = true;
		}
		else System.out.println("Unit already attacked!");
    }
    //This overrides the standard diplay movement tiles method to show ranged enemies. 
    @Override
    public void displayMovementTiles(ActorRef out, Tile tile, GameState gameState) {
        super.displayMovementTiles(out, tile, gameState);
        if(!this.getHasAttacked())
            this.displayInRangeAttackTiles(out, tile, gameState.getBoard());
    }
    //Overrides the attack move so that this unit does not move to perform its ranged attack. 
    @Override
    public void attackMoveUnit(Tile tile, ActorRef out, GameState gameState){
        this.attack(tile.getUnit(), gameState, out);
        System.out.println("AttackMove");
    }


}


