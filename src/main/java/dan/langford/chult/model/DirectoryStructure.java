package dan.langford.chult.model;

import lombok.Data;

import java.util.Map;

@Data
public class DirectoryStructure {
    private Map<String, Table> tables;
    private Map<String, String> templates;
    private Map<String, String> descriptions;
    private String allEncounterTest;
}
