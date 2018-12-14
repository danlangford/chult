package dan.langford.chult.service;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;

@Slf4j
@Singleton
public class TemplateService {

    private final DirectoryService dir;
    private final TableService tableService;
    private final DiceService diceService;

    private final Pattern pattern = Pattern.compile("(<[\\w\\d\\s+-]+>)");

    @Inject
    public TemplateService(DirectoryService dir, TableService tableService, DiceService diceService) {
        this.dir = dir;
        this.tableService = tableService;
        this.diceService = diceService;
    }

    public String processNamed(String templateName, Map<String,String> vars){
        String template = dir.getTemplate(templateName);
        if(template==null) {
            throw new NullPointerException(format("cannot find {0} in templateDirectory 404_NOT_FOUND", templateName));
        }
        return processRaw(template, vars);
    }

    public String processRaw(String templateExpr, Map<String,String> vars){
        String fullyResolved;
        Matcher m = pattern.matcher(templateExpr);
        if (m.find()) {
            String fragment = m.group();
            String resolved = resolve(fragment, vars);
            String newTemplateExpr = m.replaceFirst(resolved);
            fullyResolved = processRaw(newTemplateExpr, vars);
        } else {
            fullyResolved = templateExpr;
        }
        return fullyResolved;
    }

    private String resolve(String fragment, Map<String, String> vars) {
        if (vars==null) {
            vars = new HashMap<>();
        }
        String[] parts = fragment.replaceAll("[<>]","").split("\\s");
        log.debug("fragment={}", asList(parts));
        String resolved;
        switch (parts[0]) {
            case "table":
                resolved = tableService.roll(parts[1]);
                break;
            case "roll":
                resolved = format("({0})", diceService.roll(parts[1]));
                break;
            case "var":
                resolved = vars.getOrDefault(parts[1], format("[var {0} NOT FOUND 404_NOT_FOUND]", parts[1]));
                break;
            case "desc":
                resolved = dir.getDescription(parts[1], format("[desc {0} NOT FOUND 404_NOT_FOUND]", parts[1]));
                break;
            default:
                resolved = format("[{0} {1} CMD NOT FOUND 404_NOT_FOUND]", parts[0], parts[1]);
                break;
        }
        return resolved;
    }

    public String processNamed(String templateName) {
        return processNamed(templateName, Collections.EMPTY_MAP);
    }
}
