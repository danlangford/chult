package dan.langford.chult.service;

import dan.langford.chult.bean.DescriptionDirectory;
import dan.langford.chult.bean.TemplateDirectory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;

@Service
@Slf4j
public class TemplateService {

    @Autowired TemplateDirectory templateDirectory;
    @Autowired DescriptionDirectory descriptionDirectory;
    @Autowired TableService tableService;
    @Autowired DiceService diceService;

    Pattern pattern = Pattern.compile("(<[\\w\\d\\s+-]+>)");

    public String processNamed(String templateName, Map<String,String> vars){
        String template = templateDirectory.getTemplates().get(templateName);
        if(template==null) {
            throw new NullPointerException(format("cannot find {0} in templateDirectory", templateName));
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
                resolved = vars.getOrDefault(parts[1], format("[var {0} NOT FOUND]", parts[1]));
                break;
            case "desc":
                resolved = descriptionDirectory.getDescriptions().getOrDefault(parts[1], format("[desc {0} NOT FOUND]", parts[1]));
                break;
            default:
                resolved = format("[{0} {1} CMD NOT FOUND]", parts[0], parts[1]);
                break;
        }
        return resolved;
    }

}
