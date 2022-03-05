import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.json.Json;

import actors.GameActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import events.Initalize;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.spells.SundropElixir;
import structures.spells.Truestrike;
import structures.spells.StaffofYKir;
import structures.spells.EntropicDecay;
import structures.units.Avatar;
import akka.actor.ActorRef;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.validation.constraints.AssertTrue;

import com.fasterxml.jackson.databind.node.ObjectNode;



public class SpellCardTest {
    @Test
    public void SundropElixirTest(){   
        GameState gameState = new GameState(); // create state storage 
        //Created a test unit;
        Unit testUnit = new Unit();
        
        //Create a tile for putting the unit in it;
        Tile tile = new Tile();
        tile.setTilex(3);
        tile.setTiley(3);
        tile.addUnit(testUnit);

        //Set the max health attribute to 10;
        testUnit.setHealthWithMax(10);

        //Take damaged first; and will have 4 health after damage;
        testUnit.takeDamage(6, gameState, null);

        //Use SundropElixir card on testiUnt;
        SundropElixir card = new SundropElixir();        
        card.spell(null, gameState, tile);

        assertTrue(testUnit.getHealth()==9);

    }

    @Test
    public void TrueStrikeTest(){   
        GameState gameState = new GameState(); // create state storage

        //Created a test unit;
        Unit enemyUnit = new Unit();

        //Create a tile for putting the enemy unit in it;
        Tile tile = new Tile();
        tile.setTilex(3);
        tile.setTiley(3);
        tile.addUnit(enemyUnit);

        //Set the max health attribute to 10;
        enemyUnit.setHealthWithMax(10);

        //Use Truestrike card on testiUnt;
        Truestrike card = new Truestrike();        
        card.spell(null, gameState, tile);

        assertTrue(enemyUnit.getHealth()==8);
    }

    @Test
    public void StaffOfYkirTest(){
        GameState gameState = new GameState(); // create state storage

        //Created a test avatar and set its attack power 2;
        Avatar myAvatar = new Avatar();
        myAvatar.setAttack(2);

        //Create a tile for putting the test avatar in it;
        Tile tile = new Tile();
        tile.setTilex(3);
        tile.setTiley(3);
        tile.addUnit(myAvatar);

        //Use StaffOfYkir card on test Avatar;
        StaffofYKir card = new StaffofYKir();        
        card.spell(null, gameState, tile);
        
   }

 
}
