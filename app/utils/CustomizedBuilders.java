package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import scala.util.parsing.json.JSON;
import scala.util.parsing.json.JSONObject;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Spell;
import structures.basic.Unit;
import structures.spells.*;

import java.lang.reflect.Field;

/* This class contains customized builders to create different objects */

public class CustomizedBuilders {

    /**
     * A unit builder integrated with health and attack in assoicating BigCard
     * @param unitConfig unit configuration file
     * @param cardConfig card configuration file
     * @param unitID new unit id
     * @param unitClassType unit subclass
     * @return unit
     */
    public static Unit loadSummon(String unitConfig, String cardConfig, int unitID, Class<? extends Unit> unitClassType){

        Unit unit = BasicObjectBuilders.loadUnit(unitConfig, unitID, unitClassType);
        Card card = BasicObjectBuilders.loadCard(cardConfig, 0, Card.class);

        assert unit != null;
        assert card != null;
        unit.setHealthWithMax(card.getBigCard().getHealth());
        unit.setAttack(card.getBigCard().getAttack());

        return unit;
    }


    /**
     * A unit builder only intake card name and find out the corresponding config files and subclass.
     * @param cardName card name
     * @param gameState State of Game
     * @return unit
     */
    public static Unit loadSummonByName(String cardName, GameState gameState) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {

        String className = "structures.units." + cardName.replace(" ", "");
        cardName = cardName.replace(" ", "_").toLowerCase();
        Class<StaticConfFiles> configFiles = StaticConfFiles.class;
        Field unitField;
        Field cardField;

        // player 2 summon a reflected Hailstone Golem
        if(cardName.equals("hailstone_golem") && !gameState.clickable){
            unitField = configFiles.getDeclaredField("u_" + cardName + "R");
        } else {
            unitField = configFiles.getDeclaredField("u_" + cardName);
        }
        cardField = configFiles.getDeclaredField("c_" + cardName);

        String unitConfig = unitField.get(configFiles).toString();
        String cardConfig = cardField.get(configFiles).toString();

        Unit unit;
        Card card = BasicObjectBuilders.loadCard(cardConfig, 0, Card.class);
        try {
            unit = BasicObjectBuilders.loadUnit(unitConfig, gameState.getNewUnitID(),
                    Class.forName(className).asSubclass(Unit.class));
        } catch (ClassNotFoundException e) {
            unit = BasicObjectBuilders.loadUnit(unitConfig, gameState.getNewUnitID(), Unit.class);
        }

        assert unit != null;
        assert card != null;
        unit.setHealthWithMax(card.getBigCard().getHealth());
        unit.setAttack(card.getBigCard().getAttack());

        return unit;
    }


    /**
     * A spell builder using the card name.
     * @param cardName card name
     * @return spell
     */
    public static Spell loadSpellByName(String cardName){
        cardName = cardName.replace(" ","").replace("'", "");
        String className = "structures.spells." + cardName;
        Spell spell = null;
        try {
            spell = new ObjectMapper().readValue("{}", Class.forName(className).asSubclass(Spell.class));

        } catch (ClassNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return spell;
    }

}
