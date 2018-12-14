package dan.langford.chult.model;

import java.util.HashMap;

public class TableMap extends HashMap<String, Table> {

    public Table get(String key) {
        Table table = super.get(key);
        if(table != null) {
            table.setName("key");
        }
        return table;
    }

    public Table getOrDefault(String key, Table defaultValue) {
        Table table = super.getOrDefault(key, defaultValue);
        if(table!=null && table.getName()==null) {
            table.setName(key);
        }
        return table;
    }
}
