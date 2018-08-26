package dan.langford.chult.service;

import dan.langford.chult.bean.TableDirectory;
import dan.langford.chult.model.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.text.MessageFormat.format;

@Service
@Slf4j
public class TableService {

    @Autowired
    TableDirectory tableDirectory;

    @Autowired
    DiceService dice;

    public String roll(String tableName) {

        String rollResult;
        Table table = tableDirectory.getTables().get(tableName);
        if (table==null) {
            rollResult = format("[table {0} not found]", tableName);
        } else {
            String expr = table.getRoll();
            int roll = dice.rollWithMemory(expr, tableName);
            log.info("rolled {}={} for table {}", expr, roll, tableName);
            rollResult = table.read(roll);
        }

        return rollResult;
    }
}
