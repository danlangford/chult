package dan.langford.chult.service;

import dan.langford.chult.model.Terrain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Singleton
// try to decide if we still need to ask for input on command line.
// i think we do need some easy way to select a table to be rolled
public class DmInputService {

    private final Scanner scanner = new Scanner(System.in);

    @Inject
    public DmInputService(){}

    @Deprecated
    private <T extends Enum<T>> T enumMenu(Class<T> enumType) {
        for (T c : enumType.getEnumConstants()) {
            log.info("{0}: {1}", c.ordinal()+1, c.name());
        }
        int response = Integer.valueOf(scanner.nextLine().trim());
        return enumType.getEnumConstants()[response-1];
    }

    public String promptFor(List<String> items){

        List<String> filteredItems = items;
        String input = "";

        // continue the loop if we didnt get a number, or if the number is too large
        while (!NumberUtils.isDigits(input) || NumberUtils.toInt(input)>filteredItems.size()-1) {
            if(StringUtils.isBlank(input)) {
                say("clearing all filters");
                filteredItems = items;
            } else if (StringUtils.isAlphaSpace(input)) {
                say("applying filter: "+input);
                String $input = input;
                filteredItems = items.stream()
                        .filter(s -> StringUtils.containsIgnoreCase(s, $input))
                        .collect(toList());
                if(filteredItems.size()==0) {
                    say("no items match the filter of "+input);
                    say("clearing all filters");
                    filteredItems = items;
                }
            } else {
                say("invalid input");
            }
            say("\nEnter a Number to select an Item. Enter Text to filter your options.\n");
            for (int i = 0; i < filteredItems.size(); i++) {
                say(i+". "+filteredItems.get(i));
            }
            input = ask("please select an item");
        }
        String selection = filteredItems.get(NumberUtils.toInt(input));
        say("your selection: "+selection);
        return selection;
    }

    private void say(String msg) {
        System.out.println(msg);
    }

    public String ask(String prompt) {
        say(prompt);
        return scanner.nextLine();
    }

    @Deprecated
    public Terrain askTerrain() {
        log.info("terrain of target hex?");
        return enumMenu(Terrain.class);
    }

}
