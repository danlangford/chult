package dan.langford.chult.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;

import static java.text.MessageFormat.format;

@Data
@Slf4j
public class Table {

    String roll;
    Map<String,String> results;

    public String read(int roll){
        return results.entrySet().stream()
                .filter(e -> {
                    String[] parts = e.getKey().split("-");
                    int min = Integer.parseInt(parts[0]);
                    int max = parts.length==1?min:Integer.parseInt(parts[1]);
                    return roll >= min && roll <= max;
                })
                .map(Entry::getValue)
                .findAny()
                .orElse(format("[table entry not found for roll {0}]", roll));
    }

}
