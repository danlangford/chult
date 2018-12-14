package dan.langford.chult.service;

import com.bernardomg.tabletop.dice.parser.DefaultDiceNotationExpressionParser;
import com.bernardomg.tabletop.dice.parser.DiceNotationExpressionParser;
import com.bernardomg.tabletop.dice.roller.DefaultRoller;
import dan.langford.chult.repo.FileRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class DiceService {

    @Autowired
    FileRepo fileRepo;

    DiceNotationExpressionParser diceNotation = new DefaultDiceNotationExpressionParser(new DefaultRoller());

    public Integer roll(String expression) {
        return diceNotation.parse(expression).getValue();
    }

    final int maxAttempts=50;

    public Integer rollWithMemory(String expr, String memoryId) {

        Integer roll = roll(expr);

        Set<Integer> priorRolls = fileRepo.loadRollCache(memoryId);

        for(int i=0; i<maxAttempts; i++){
            if(priorRolls.contains(roll)) {
                roll = roll(expr);
            } else {
                break;
            }
        }
        priorRolls.add(roll);
        fileRepo.storeRollCache(memoryId, priorRolls);

        return roll;

    }

}
