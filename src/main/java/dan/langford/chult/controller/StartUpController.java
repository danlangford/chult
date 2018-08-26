package dan.langford.chult.controller;

import dan.langford.chult.bean.AppProps;
import dan.langford.chult.fsm.Events;
import dan.langford.chult.fsm.States;
import dan.langford.chult.service.TemplateService;
import dan.langford.chult.service.TimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static dan.langford.chult.fsm.Events.*;
import static dan.langford.chult.fsm.StateAccess.context;
import static org.springframework.messaging.support.MessageBuilder.withPayload;

@Slf4j
@Controller
public class StartUpController implements ApplicationRunner {

    @Autowired
    AppProps appProps;

    @Autowired
    TemplateService templateService;

    @Autowired
    TimeService timeService;

    @Autowired
    StateMachine<States, Events> stateMachine;


    /*

For each day that the party travels through the wilderness, follow these steps:

identify the hex in which the party is currently located. Don’t share this information with the players if the party is lost; otherwise, show the players the party’s location by pointing to the appropriate hex on their map of Chult.
Let the players determine what adjacent hex the party wants to explore, and whether the party plans to explore at a normal pace, a fast pace, or a slow pace
Let the players choose a navigator, then DM makes a Wisdom (Survival) check on the navigator’s behalf to determine if the party becomes lost (see “Navigation” below).
random encounter (16+ on 1d20) throughout the day (see appendix B).
At the end of the day, check to see if any party members are dehydrated (see “Dehydration” below).


     */

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<Entry<Events,States>> possibleEvents;

        stateMachine.sendEvent(withPayload(TRAVEL).setHeader("target","A1").build());

        possibleEvents = getPossibleEvents(stateMachine);
        log.info("possible event/state combos are {}", possibleEvents);

        stateMachine.sendEvent(withPayload(TRAVEL).setHeader("target","A2").build());

        possibleEvents = getPossibleEvents(stateMachine);
        log.info("possible events are {}", possibleEvents);

        stateMachine.sendEvent(withPayload(REST).build());

        possibleEvents = getPossibleEvents(stateMachine);
        log.info("possible events are {}", possibleEvents);

        stateMachine.sendEvent(withPayload(TRAVEL).setHeader("target","B3").build());

        possibleEvents = getPossibleEvents(stateMachine);
        log.info("possible events are {}", possibleEvents);

        stateMachine.sendEvent(withPayload(SLEEP).build());

        possibleEvents = getPossibleEvents(stateMachine);
        log.info("possible events are {}", possibleEvents);

        stateMachine.sendEvent(withPayload(TRAVEL).build());

        possibleEvents = getPossibleEvents(stateMachine);
        log.info("possible events are {}", possibleEvents);

        stateMachine.sendEvent(withPayload(TRAVEL).setHeader("target","A3").build());

        possibleEvents = getPossibleEvents(stateMachine);
        log.info("possible events are {}", possibleEvents);

        timeService.newDay();

        List<String> commands = args.getNonOptionArgs();
        if (commands.isEmpty()) {
            commands=Arrays.asList("terrain","beach");
        }

        Map<String,String> vars = new HashMap<>();
        vars.put(commands.get(0), commands.get(1));

//        for(String s : Arrays.asList("beach","jungle-no-undead","jungle-lesser-undead","jungle-greater-undead","mountains","rivers","ruins","swamps","wasteland")) {
//            vars.put(commands.get(0), s);
//            System.out.print(format("\n**********\n\n{0}\n\n**********\n\n",templateService.processNamed(commands.get(0), vars)));
//        }


    }

    private List<Entry<Events,States>> getPossibleEvents(StateMachine<States, Events> stateMachine) {
        return stateMachine.getTransitions().stream()
//                .peek(t -> log.info("1 transition {}", t))
                .filter(t-> stateMachine.getState().equals(t.getSource()))
                .filter(t -> t.getGuard()==null || t.getGuard().evaluate(context))
//                .peek(t -> log.info("2 transition {}", t))
                .map(t -> new SimpleEntry<>(t.getTrigger().getEvent(), t.getTarget().getId()))
                .collect(Collectors.toList());
    }
}