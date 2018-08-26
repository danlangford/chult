package dan.langford.chult.fsm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.OnStateChanged;
import org.springframework.statemachine.annotation.WithStateMachine;

@WithStateMachine
@Slf4j
public class StateAccess {


    public static StateContext<States, Events> context;

    @OnStateChanged
    public void stateChanged(StateContext<States,Events> context){
        this.context=context;
    }

}
