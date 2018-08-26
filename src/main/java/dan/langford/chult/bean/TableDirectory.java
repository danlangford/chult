package dan.langford.chult.bean;

import dan.langford.chult.model.Table;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties("tabledirectory")
public class TableDirectory {
    Map<String,Table> tables;
}