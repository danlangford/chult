package dan.langford.chult.fsm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.text.MessageFormat;
import java.util.EnumSet;

import static dan.langford.chult.fsm.Events.*;
import static dan.langford.chult.fsm.States.*;

@Configuration
@EnableStateMachine
public class StateConfig extends EnumStateMachineConfigurerAdapter<States, Events> {


    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config.withConfiguration().autoStartup(true).listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states.withStates()
                .initial(STARTING)
                .states(EnumSet.allOf(States.class))
                .end(CELEBRATING);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions.withExternal()
                    .source(STARTING).event(TRAVEL).target(ENCOUNTERING)
                .and().withExternal()
                    .source(ENCOUNTERING).event(TRAVEL).guard(notFoundLocationGuard()).target(ENCOUNTERING)
                .and().withExternal()
                    .source(ENCOUNTERING).event(REST).target(RESTING)
                .and().withExternal()
                    .source(ENCOUNTERING).event(SLEEP).target(SLEEPING)
                .and().withExternal()
                    .source(ENCOUNTERING).event(TRAVEL).guard(foundLocationGuard()).target(CELEBRATING)
                .and().withExternal()
                    .source(RESTING).event(TRAVEL).target(ENCOUNTERING)
                .and().withExternal()
                    .source(SLEEPING).event(TRAVEL).target(ENCOUNTERING)
                .and();
    }


    @Bean
    public StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<States, Events>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                System.out.println(MessageFormat.format("State change from {0} to {1}", from==null? null :from.getId(), to.getId()));
            }
        };
    }

    @Bean
    public Guard<States, Events> foundLocationGuard() {
        return context -> "A3".equals(context.getMessageHeader("target"));
    }

    @Bean
    public Guard<States, Events> notFoundLocationGuard() {
        return context -> !"A3".equals(context.getMessageHeader("target"));
    }

}
