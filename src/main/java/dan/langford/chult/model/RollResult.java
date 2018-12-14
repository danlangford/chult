package dan.langford.chult.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RollResult {

    private String expr;
    private Integer result;

    public String toString(){
        return expr+'='+result;
    }
}
