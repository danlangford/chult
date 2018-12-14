package dan.langford.chult.model;

import lombok.Data;

import java.util.Map;

@Data
public class Table {
    String roll, name;
    Map<String,String> results;
}
