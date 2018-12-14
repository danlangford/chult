package dan.langford.chult.service;

import dan.langford.chult.bean.TableDirectory;
import dan.langford.chult.model.Table;
import dan.langford.chult.repo.FileRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.text.MessageFormat.format;

@Service
@Slf4j
public class TableService {

    @Autowired
    TableDirectory tableDirectory;

    @Autowired
    DiceService dice;

    @Autowired
    FileRepo fileRepo;

    private int maxAttempts=50;

    public String roll(String tableName) {

        String rollResult;
        Table table = tableDirectory.getTables().get(tableName);
        if (table==null) {
            rollResult = format("[table {0} not found]", tableName);
        } else {
            String expr = table.getRoll();
            int roll = dice.roll(expr);
            log.info("rolled {}={} for table {}", expr, roll, tableName);
            String rollGroup = findRollGroup(table, roll);

            Set<String> priorGroups = fileRepo.loadGroupCache(tableName);
            if (priorGroups.size()>=table.getResults().size()) {
                priorGroups.clear();
            }

            for(int i=0; i<maxAttempts; i++){
                if(priorGroups.contains(rollGroup)) {
                    rollGroup = findRollGroup(table, roll);
                } else {
                    break;
                }
            }
            if(table.getCache()) {
                priorGroups.add(rollGroup);
                fileRepo.storeGroupCache(tableName, priorGroups);
            }

            rollResult = table.getResults().get(rollGroup);
        }

        return rollResult;
    }

    private boolean isGroupMatch(int roll, Map.Entry<String,String> e) {
        String[] parts = e.getKey().split("-");
        int min = Integer.parseInt(parts[0]);
        int max = parts.length==1?min:Integer.parseInt(parts[1]);
        return roll >= min && roll <= max;
    }

    private String findRollGroup(Table table, int roll) {
        return table.getResults().entrySet().stream()
                .filter(e->isGroupMatch(roll,e))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow(RuntimeException::new);
    }

}
