package com.nokiaspstraining.serverspool.demo;

import com.nokiaspstraining.serverspool.demo.enums.Enumerations;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;


@Configuration
@EnableStateMachineFactory
@EnableAutoConfiguration
public class SMConfig extends StateMachineConfigurerAdapter<Enumerations.ServerStatus, Enumerations.ServerEvent> {
    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(SMConfig.class);


    @Override
    public void configure(StateMachineTransitionConfigurer<Enumerations.ServerStatus, Enumerations.ServerEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(Enumerations.ServerStatus.ACTIVE).target(Enumerations.ServerStatus.INACTIVE).event(Enumerations.ServerEvent.CANCEL)
                .and()
                .withExternal()
                .source(Enumerations.ServerStatus.CREATING).target(Enumerations.ServerStatus.ACTIVE).event(Enumerations.ServerEvent.ACTIVATE);
    }

    @Override
    public void configure(StateMachineStateConfigurer<Enumerations.ServerStatus, Enumerations.ServerEvent> states) throws Exception {
        states.withStates()
                .initial(Enumerations.ServerStatus.CREATING)
                .state(Enumerations.ServerStatus.ACTIVE)
                .end(Enumerations.ServerStatus.INACTIVE);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<Enumerations.ServerStatus, Enumerations.ServerEvent> config) throws Exception {
        StateMachineListenerAdapter<Enumerations.ServerStatus, Enumerations.ServerEvent> adapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<Enumerations.ServerStatus, Enumerations.ServerEvent> from, State<Enumerations.ServerStatus, Enumerations.ServerEvent> to) {
                log.info(String.format("state changed from %s to %s",from,to));
            }

        } ;
        config.withConfiguration().autoStartup(false).listener(adapter);
    }
}
