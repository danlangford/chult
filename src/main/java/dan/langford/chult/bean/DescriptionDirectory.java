package dan.langford.chult.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties("descriptiondirectory")
public class DescriptionDirectory {
    Map<String, String> descriptions;
}
