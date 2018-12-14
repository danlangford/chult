package dan.langford.chult.service;

import dan.langford.chult.fsm.Events;
import dan.langford.chult.fsm.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import java.util.Scanner;

import static dan.langford.chult.fsm.StateConfig.say;
import static org.springframework.messaging.support.MessageBuilder.withPayload;

@Service
public class DmInputService {

    @Autowired @Lazy
    StateMachine<States, Events> stateMachine;

    Scanner scanner = new Scanner(System.in);

    public void askAction() {
        say("next action?");
        Action action = enumMenu(Action.class);
        stateMachine.sendEvent(withPayload(action.event).build());
    }

    public Method askMethod() {
        say("method of exploration?");
        return enumMenu(Method.class);
    }

    private <T extends Enum<T>> T enumMenu(Class<T> enumType) {
        for (T c : enumType.getEnumConstants()) {
            say("{0}: {1}", c.ordinal()+1, c.name());
        }
        int response = Integer.valueOf(scanner.nextLine().trim());
        return enumType.getEnumConstants()[response-1];
    }

    public Pace askPace() {
        say("pace of exploration?");
        return enumMenu(Pace.class);
    }

    public String askGeneric(String prompt) {
        say(prompt);
        return scanner.nextLine();
    }

    public Terrain askTerrain() {
        say("terrain of target hex?");
        return enumMenu(Terrain.class);
    }

    public enum Action {
        TRAVEL(Events.TRAVEL), REST(Events.REST), SLEEP(Events.SLEEP);

        public final Events event;
        Action(Events event) {
            this.event = event;
        }
    }

    public enum Method {
        FOOT, CANOE;
    }

    public enum Pace {
        NORMAL, FAST, SLOW;
    }

    public enum Terrain {
        BEACH(10), JUNGLE_NO_UNDEAD(15), JUNGLE_LESSER_UNDEAD(15), JUNGLE_GREATER_UNDEAD(15), MOUNTAINS(15), RIVERS(15), RUINS(15), SWAMPS(15), WASTELAND(15);

        public final int dc;

        Terrain(int dc) {
            this.dc=dc;
        }
    }
}
