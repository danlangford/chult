package dan.langford.chult.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties("templatedirectory")
public class TemplateDirectory {
    Map<String, String> templates;
}
