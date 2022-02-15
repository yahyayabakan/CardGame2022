package utils;

import structures.basic.Card;
import structures.basic.Unit;

/* This class contains customized builders to create different objects */

public class CustomizedBuilders {

    // Build a summoning unit associating health and attack in BigCard
    public static Unit loadSummon(String unitConfig, String cardConfig, int unitID, Class<? extends Unit> unitClassType){

        Unit unit = BasicObjectBuilders.loadUnit(unitConfig, unitID, unitClassType);
        Card card = BasicObjectBuilders.loadCard(cardConfig, 0, Card.class);

        assert unit != null;
        assert card != null;
        unit.setHealthWithMax(card.getBigCard().getHealth());
        unit.setAttack(card.getBigCard().getAttack());

        return unit;
    }
}
