package dan.langford.chult;

import dan.langford.chult.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ChultApplicationTests {

    @Autowired
    TemplateService templateService;

	@Test
	public void testEntireTemplateStructure() throws IOException {

        Resource file = new ClassPathResource("application-default.yml");
	    String fileContents = StreamUtils.copyToString(file.getInputStream(), Charset.forName("UTF-8"));

	    Map<String,String> vars = new HashMap<>();
	    vars.put("terrain","beach");

	    log.info("\n\n**********\n\n{}\n\n**********\n\n", templateService.processRaw(fileContents, vars));



	}

}
