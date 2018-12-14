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

    String roll, name;
    Map<String,String> results;
    Boolean cache = true;

}
