package utils;

import structures.GameState;
import structures.basic.Card;
import structures.basic.Unit;

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
        Object obj = new StaticConfFiles();
        Field unitField;
        Field cardField;

        // player 2 summon a reflected Hailstone Golem
        if(cardName.equals("hailstone_golem") && gameState.clickable == false){
            unitField = obj.getClass().getDeclaredField("u_" + cardName + "2");
        } else {
            unitField = obj.getClass().getDeclaredField("u_" + cardName);
        }
        cardField = obj.getClass().getDeclaredField("c_" + cardName);

        String unitConfig = (String)unitField.get(obj);
        String cardConfig = (String)cardField.get(obj);

        Class<? extends Unit> unitClass = (Class<? extends Unit>) Class.forName(className).asSubclass(Unit.class);

        Unit unit = BasicObjectBuilders.loadUnit(unitConfig, gameState.getNewUnitID(), unitClass);
        Card card = BasicObjectBuilders.loadCard(cardConfig, 0, Card.class);

        assert unit != null;
        assert card != null;
        unit.setHealthWithMax(card.getBigCard().getHealth());
        unit.setAttack(card.getBigCard().getAttack());

        return unit;
    }

}
