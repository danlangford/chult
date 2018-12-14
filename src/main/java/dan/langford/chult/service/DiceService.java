package dan.langford.chult.service;

import com.bernardomg.tabletop.dice.parser.DefaultDiceNotationExpressionParser;
import com.bernardomg.tabletop.dice.parser.DiceNotationExpressionParser;
import com.bernardomg.tabletop.dice.roller.DefaultRoller;
import dan.langford.chult.model.RollResult;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class DiceService {

    @Inject
    public DiceService() {
    }

    private final DiceNotationExpressionParser diceNotation = new DefaultDiceNotationExpressionParser(new DefaultRoller());

    public RollResult roll(String expression) {
        return new RollResult(expression, diceNotation.parse(expression).roll());
    }

}
