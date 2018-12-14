package dan.langford.chult.fsm;

import dan.langford.chult.model.Template;
import dan.langford.chult.service.DmInputService;
import dan.langford.chult.service.DmInputService.Method;
import dan.langford.chult.service.DmInputService.Pace;
import dan.langford.chult.service.DmInputService.Terrain;
import dan.langford.chult.service.TemplateService;
import dan.langford.chult.service.TimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateContext.Stage;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import java.util.Map;

import static dan.langford.chult.fsm.Events.*;
import static dan.langford.chult.fsm.States.*;
import static dan.langford.chult.service.DmInputService.Pace.FAST;
import static dan.langford.chult.service.DmInputService.Pace.SLOW;
import static java.text.MessageFormat.format;
import static org.springframework.messaging.support.MessageBuilder.withPayload;

@Slf4j
@Configuration
@EnableStateMachine
public class StateConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Autowired
    TimeService timeService;

    @Autowired
    DmInputService dmInputService;

    @Autowired
    TemplateService templateService;

    @Autowired @Lazy
    StateMachine<States, Events> stateMachine;

    private Action<States, Events> encounterEnter= x -> {
        say(x, templateService.processNamed("encounter_roll", Map.of("terrain",x.getExtendedState().get("terrain", String.class).toLowerCase())));
        String doneYet = dmInputService.askGeneric("is the encounter over? ready to move on?");
        stateMachine.sendEvent(withPayload(CONTEMPLATE).build());
    };

    private Action<States, Events> encounterExit= x -> {
        say(x, "DM may need to resolve “Shivering Sickness” in some scenarios");
    };

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config.withConfiguration().autoStartup(true).listener(listener());

    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states.withStates()
                .initial(STARTING)
                .state(CONTEMPLATING, x -> {
                    say(x, "DM declares time passed and remaining");
                    say(x, "DM declares parties last known location. current location OR they may be lost");
                    say(x, "party declares intent to rest or travel");
                    dmInputService.askAction();
                }, null)
                .state(TRAVELING, x-> {
                    say(x, "Party declares an adjacent hex as their “target” to explore");
                    String targetHex = dmInputService.askGeneric("what hex do we want to explore?");
                    Terrain targetTerrain = dmInputService.askTerrain();
                    say(x, "Party declares travel method and pace");
                    say(x, "FOOT (3:00±) or CANOE (2:00±)");
                    Method method = dmInputService.askMethod();
                    say(x, "NORMAL, FAST (-5 Perception, DIS on Survival vs LOST), or SLOW (+5 Perception, ADV on Survival vs LOST)");
                    Pace pace = dmInputService.askPace();

                    String navigatorPc = dmInputService.askGeneric("Who is the navigator?");
                    say(x, "{0} roll a WIS/Survival check (DC {1}) {2} vs becoming LOST", navigatorPc, targetTerrain.dc, pace.equals(SLOW)?"with ADVantage":pace.equals(FAST)?"with DISadvantage":"");
                    Terrain actualTerrain = dmInputService.askTerrain();
                    x.getExtendedState().getVariables().put("terrain",actualTerrain.name());
                    say(x, "Did party get lost?");
                    say(x, "describe terrain features. did party find the obelisks?");
                    say(x, templateService.processNamed("travel_pace", Map.of("method", method.name(), "pace", pace.name())));

                }, null)
                .state(RESTING)
                .state(SLEEPING, null, x -> {
                    // this is the start of a day
                    timeService.newDay();
                    say(x, "Good Morning.");
                    say(x,"regain HP and spell slots");
                    say(x,"resolve any shivering sickness?");
                    say(x,"remind about any conditions (exhaustion, sickness, etc.)");
                    say(x,"Check the weather");
                    say(x, templateService.processNamed("weather"));
                    say(x,"prep spells");
                    say(x,"using any insect repellent?");
                })
                .end(WINNING)
        .and().withStates().parent(TRAVELING).initial(ENCOUNTERING).state(ENCOUNTERING, encounterEnter, encounterExit)
        .and().withStates().parent(RESTING).initial(ENCOUNTERING).state(ENCOUNTERING, encounterEnter, encounterExit)
        .and().withStates().parent(SLEEPING).initial(ENCOUNTERING).state(ENCOUNTERING, encounterEnter, encounterExit)
        .and();
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions.withExternal()
                .and().withExternal().source(STARTING).event(CONTEMPLATE).target(CONTEMPLATING)

                .and().withExternal().source(CONTEMPLATING).event(TRAVEL).target(TRAVELING)
                .and().withExternal().source(CONTEMPLATING).event(REST).target(RESTING)
                .and().withExternal().source(CONTEMPLATING).event(SLEEP).target(SLEEPING)

                .and().withExternal().source(TRAVELING).event(CONTEMPLATE).target(CONTEMPLATING)
                .and().withExternal().source(RESTING).event(CONTEMPLATE).target(CONTEMPLATING)
                .and().withExternal().source(SLEEPING).event(CONTEMPLATE).target(CONTEMPLATING)

                .and().withExternal().source(TRAVELING).event(WIN).target(WINNING)

                .and();
    }


    @Bean
    public StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                say("State change from {0} to {1}", from == null ? null : from.getId(), to.getId());
            }
            @Override
            public void transition(Transition<States, Events> transition) {
                say("Transition Event {0}", transition.getTrigger()==null? null : transition.getTrigger().getEvent());
            }
        };
    }

    static public void say(StateContext<States,Events> x, String msgFrmt, Object...arguments){
        States state=null;
        Stage stage = x.getStage();
        if(stage.equals(Stage.STATE_ENTRY)) {
            state = x.getTarget().getId();
        } else if (stage.equals(Stage.STATE_EXIT)) {
            state = x.getSource().getId();
        }
        say("["+state+" "+stage+"] "+msgFrmt, arguments);
    }

    static public void say(String msgFrmt, Object...arguments){
        System.out.println(format(msgFrmt, arguments));
    }

}
