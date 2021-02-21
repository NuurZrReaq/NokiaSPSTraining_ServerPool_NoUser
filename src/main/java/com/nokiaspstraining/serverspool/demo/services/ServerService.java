package com.nokiaspstraining.serverspool.demo.services;


import com.nokiaspstraining.serverspool.demo.enums.Enumerations;
import com.nokiaspstraining.serverspool.demo.models.Server;
import com.nokiaspstraining.serverspool.demo.repositories.ServerRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.access.StateMachineFunction;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ServerService {

    @Autowired
    private ServerRepo serverRepo;


    @Autowired
    private  StateMachineFactory<Enumerations.ServerStatus, Enumerations.ServerEvent> factory;



    public int allocate(int size) {
        List<String> result;
        result = allocateServer(size);
        Server server = serverRepo.findById(Integer.parseInt(result.get(2))).orElseThrow();
        if(result.get(0).equals("True")){
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            activate(server.getKey());

        }
        else if(result.get(0).equals("False")){
            if(result.get(1).equals("Active")){
                return server.getKey();
            }
            while(true){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                server = serverRepo.findById(Integer.parseInt(result.get(2))).orElseThrow();
                if(server.getServerStatus().equals(Enumerations.ServerStatus.ACTIVE)) break;
            }



        }

        return server.getKey();
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected List<String> allocateServer(int size){

        List<Server> activeServers = new ArrayList<>();
        List<String> result = new ArrayList<>();
        Iterable<Server> servers = serverRepo.findAll();
        for (Server server : servers) {
            if (server.getServerStatus().name().equals("ACTIVE")) {
                activeServers.add(server);
            }
        }
        for (Server server : activeServers) {
            if (size <= server.getAvaStorage()) {
                server.setAvaStorage(server.getAvaStorage() - size);
                serverRepo.save(server);
                result.add("False");
                result.add("Active");
                result.add(Integer.toString(server.getKey()));
                return result;
            }
        }
        servers = serverRepo.findAll();
        for (Server server : servers) {
            if (size <= server.getAvaStorage()) {
                if (server.getServerStatus().equals(Enumerations.ServerStatus.CREATING)){
                    result.add("False");
                    result.add("Creating");
                    result.add(Integer.toString(server.getKey()));
                    server.setAvaStorage(server.getAvaStorage() - size);
                    serverRepo.save(server);
                    return result;
                }
                server.setAvaStorage(server.getAvaStorage() - size);
                serverRepo.save(server);
                result.add("False");
                result.add("Active");
                result.add(Integer.toString(server.getKey()));
                return result;
            }
        }
        Server server = create(size);
        result.add("True");
        result.add("Active");
        result.add(Integer.toString(server.getKey()));
        return result;

    }

    private void activate(int serverId) {
        StateMachine<Enumerations.ServerStatus,Enumerations.ServerEvent> sm = buildSM(serverId);
        Message<Enumerations.ServerEvent> eventMessage =MessageBuilder.withPayload(Enumerations.ServerEvent.ACTIVATE)
                .setHeader("serverId",serverId)
                .build();
        sm.sendEvent(eventMessage);



    }

    private StateMachine<Enumerations.ServerStatus, Enumerations.ServerEvent> buildSM(int serverId) {
        Server server = serverRepo.findById(serverId).orElseThrow();
        String serverID = String.valueOf(server.getKey());
        StateMachine<Enumerations.ServerStatus,Enumerations.ServerEvent> sm = factory.getStateMachine(serverID);
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(new StateMachineFunction<StateMachineAccess<Enumerations.ServerStatus, Enumerations.ServerEvent>>() {
            @Override
            public void apply(StateMachineAccess<Enumerations.ServerStatus, Enumerations.ServerEvent> sma) {
                sma.addStateMachineInterceptor(new StateMachineInterceptorAdapter<Enumerations.ServerStatus,Enumerations.ServerEvent>(){
                    @Override
                    public void preStateChange(State<Enumerations.ServerStatus, Enumerations.ServerEvent> state, Message<Enumerations.ServerEvent> message, Transition<Enumerations.ServerStatus, Enumerations.ServerEvent> transition, StateMachine<Enumerations.ServerStatus, Enumerations.ServerEvent> stateMachine) {
                        Optional.ofNullable(message).ifPresent(msg->{
                            Optional.ofNullable(Integer.class.cast(msg.getHeaders().getOrDefault("serverId",-1)))
                                    .ifPresent(serverId->{
                                        Server server1 = serverRepo.findById(serverId).orElseThrow();
                                        server1.setServerStatus(state.getId());
                                        serverRepo.save(server1);
                                    });
                        });
                    }
                });

                sma.resetStateMachine(new DefaultStateMachineContext<Enumerations.ServerStatus,Enumerations.ServerEvent>
                        (server.getServerStatus(),null,null,null));
            }
        });
        sm.start();
        return sm;
    }


    public Server create(int size) {

        int id;
        while(true){

            Random random = new Random( );
            id = random.nextInt(1000000);
            Server s=serverRepo.findById(id).orElse(null);
            if(s==null) break;
        }
        Server server = new Server();
        server.setKey(id);
        server.setServerStatus(Enumerations.ServerStatus.CREATING);
        server.setAvaStorage(100 - size);
        return serverRepo.save(server);
    }

    public void deleteById(int id) {
        serverRepo.deleteById(id);
    }
}
