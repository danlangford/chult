package dan.langford.chult.service;

import dan.langford.chult.model.Terrain;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
@Deprecated
// try to decide if we still need to ask for input on command line.
// i think we do need some easy way to select a table to be rolled
public class DmInputService {

    private final Scanner scanner = new Scanner(System.in);

    private <T extends Enum<T>> T enumMenu(Class<T> enumType) {
        for (T c : enumType.getEnumConstants()) {
            log.info("{0}: {1}", c.ordinal()+1, c.name());
        }
        int response = Integer.valueOf(scanner.nextLine().trim());
        return enumType.getEnumConstants()[response-1];
    }


    public String askGeneric(String prompt) {
        log.info(prompt);
        return scanner.nextLine();
    }

    public Terrain askTerrain() {
        log.info("terrain of target hex?");
        return enumMenu(Terrain.class);
    }

}
