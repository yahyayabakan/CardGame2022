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
import akka.actor.ActorRef;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.constraints.AssertTrue;

import com.fasterxml.jackson.databind.node.ObjectNode;



public class SpellCardTest {
    @Test
    public void SundropElixirTest(){    
        //Created a test unit;
        Unit testUnit = new Unit();
        
        //Create a tile for putting the unit in it;
        Tile tile = new Tile();
        tile.setTilex(3);
        tile.setTiley(3);
        tile.addUnit(testUnit);

        //Set the max health attribute to 10;
        testUnit.setHealthWithMax(10);

    }

}
