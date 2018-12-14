package dan.langford.chult;

import dagger.Component;
import dan.langford.chult.model.Method;
import dan.langford.chult.model.Pace;
import dan.langford.chult.model.Terrain;
import dan.langford.chult.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// TODO: rename all references to chult to something more generic

@Component
@Singleton
interface ChultComponent {
    ChultApplication getChult();
}

@Slf4j
@Singleton
public class ChultApplication {

    public static void main(String ... args) throws IOException {
        ChultComponent component = DaggerChultComponent.create();
        component.getChult().run();
    }

    private final DiceService dice;
    private final TemplateService tmplt;
    private final DirectoryService dir;
    private final DmInputService dm;

    @Inject
    public ChultApplication(DiceService dice, TemplateService tmplt, DirectoryService dir, DmInputService dm) {
        this.dice = dice;
        this.tmplt = tmplt;
        this.dir = dir;
        this.dm = dm;
    }

    private void run() throws IOException {
        log.debug("Test Roll {}", dice.roll("1d20"));
        Map<String, String> vars = new HashMap<>();
        vars.put("terrain", Terrain.JUNGLE_NO_UNDEAD.name());
        log.debug("vars={}", vars);
        log.info("Results {}", tmplt.processNamed("encounter_roll", vars));


        while(true) {
            String decision = dm.promptFor(new ArrayList<>(dir.getTableNames()));
            log.info("you selected {}", decision);
            log.info("Roll on Table {} is {}", decision, tmplt.rollTable(decision));
        }

        // there is a full file run over in the Tests
    }
}
