package dan.langford.chult;

import dan.langford.chult.model.Method;
import dan.langford.chult.model.Pace;
import dan.langford.chult.model.Terrain;
import dan.langford.chult.service.DiceService;
import dan.langford.chult.service.DirectoryService;
import dan.langford.chult.service.TableService;
import dan.langford.chult.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
class ChultApplicationTests {

    private final DirectoryService dir = new DirectoryService();
    private final DiceService dice = new DiceService();
    private final TableService tables = new TableService(dir, dice);
    private final TemplateService tmplt = new TemplateService(dir, tables, dice);

	@Test
    void testEntireTemplateStructure() throws IOException {

		// now lets test the entire file!!!!
		InputStream chultFile = this.getClass().getResourceAsStream("/chult.yml");
		String fileContents = IOUtils.toString(chultFile, "UTF-8");

		Map<String,String> vars = new HashMap<>();
		vars.put("terrain", Terrain.RUINS.name());
		vars.put("pace", Pace.FAST.name());
		vars.put("method", Method.CANOE.name());

		String entireFileResults = tmplt.processRaw(fileContents, vars);

		assertThat(entireFileResults).doesNotContain("404");

	    log.info("\n\n**********\n\n{}\n\n**********\n\n", entireFileResults);
	    log.info("\n\n**********\n\n{}\n\n**********\n\n", tmplt.processRaw(dir.getDoc().getAllEncounterTest(), vars));
	    log.info("\n\n**********\n\n{}\n\n**********\n\n", tmplt.processRaw("Welcome to the <var terrain>! Encounter roll … <table encounter_<var terrain>>", vars));

	}

}
