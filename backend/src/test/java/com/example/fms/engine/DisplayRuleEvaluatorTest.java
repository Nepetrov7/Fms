package com.example.fms.engine;

import com.example.fms.entity.DisplayRule;
import com.example.fms.entity.Migrant;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DisplayRuleEvaluatorTest {

    private final DisplayRuleEvaluator evaluator = new DisplayRuleEvaluator();

    @Test
    void emptyRules_hiddenUntilConfigured() {
        Migrant migrant = sampleMigrant();
        assertFalse(evaluator.isTaskVisible(migrant, List.of()));
    }

    @Test
    void allRule_matchesEveryone() {
        Migrant migrant = sampleMigrant();
        DisplayRule all = rule(1, "ALL", "EQ", "true");
        assertTrue(evaluator.isTaskVisible(migrant, List.of(all)));
    }

    @Test
    void citizenshipEq_matches() {
        Migrant migrant = sampleMigrant();
        migrant.setCitizenship("Беларусь");
        DisplayRule rule = rule(1, "CITIZENSHIP", "EQ", "Беларусь");
        assertTrue(evaluator.isTaskVisible(migrant, List.of(rule)));
    }

    @Test
    void citizenshipNe_excludes() {
        Migrant migrant = sampleMigrant();
        migrant.setCitizenship("Узбекистан");
        DisplayRule rule = rule(1, "CITIZENSHIP", "NE", "Беларусь");
        assertTrue(evaluator.isTaskVisible(migrant, List.of(rule)));
    }

    @Test
    void andGroup_requiresAllConditions() {
        Migrant migrant = sampleMigrant();
        migrant.setCitizenship("Беларусь");
        migrant.setArrivalDate(LocalDate.now().minusDays(10));
        DisplayRule days = rule(1, "DAYS_SINCE_ENTRY", "GTE", "5");
        days.setRuleGroupId(1);
        DisplayRule country = rule(1, "CITIZENSHIP", "EQ", "Беларусь");
        country.setRuleGroupId(1);
        assertTrue(evaluator.isTaskVisible(migrant, List.of(days, country)));
    }

    @Test
    void orBetweenGroups_oneMatchEnough() {
        Migrant migrant = sampleMigrant();
        migrant.setCitizenship("Узбекистан");
        DisplayRule belarus = rule(1, "CITIZENSHIP", "EQ", "Беларусь");
        belarus.setRuleGroupId(1);
        DisplayRule uzbek = rule(1, "CITIZENSHIP", "EQ", "Узбекистан");
        uzbek.setRuleGroupId(2);
        assertTrue(evaluator.isTaskVisible(migrant, List.of(belarus, uzbek)));
    }

    @Test
    void hasPatent_matchesWhenFlagSet() {
        Migrant migrant = sampleMigrant();
        migrant.setHasPatent(true);
        DisplayRule rule = rule(1, "HAS_PATENT", "EQ", "true");
        assertTrue(evaluator.isTaskVisible(migrant, List.of(rule)));
    }

    private static Migrant sampleMigrant() {
        Migrant m = new Migrant();
        m.setArrivalDate(LocalDate.now().minusDays(3));
        m.setCountryOfArrival("Узбекистан");
        m.setCitizenship("Узбекистан");
        return m;
    }

    private static DisplayRule rule(int groupId, String key, String op, String value) {
        DisplayRule r = new DisplayRule();
        r.setRuleGroupId(groupId);
        r.setParameterKey(key);
        r.setOperator(op);
        r.setRuleValue(value);
        return r;
    }
}
