package com.nokiaspstraining.serverspool.demo.controllers;


import com.aerospike.client.Key;
import com.nokiaspstraining.serverspool.demo.enums.Enumerations;
import com.nokiaspstraining.serverspool.demo.models.Server;
import com.nokiaspstraining.serverspool.demo.repositories.ServerRepo;
import com.nokiaspstraining.serverspool.demo.services.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/server")
public class ServerController {

    @Autowired
    private ServerService serverService;

   /* @Autowired
    private ServerRepo serverRepo;*/

    @PostMapping("/allocate/{size}")
    @ResponseBody
    public int allocateServer(@PathVariable("size") int size){
        return serverService.allocate(size);
    }

    /*@PostMapping("/create")
    public void createServer(@RequestBody Server server){

        server.setServerStatus(Enumerations.ServerStatus.CREATING);
        //server.setKey(1);
        Random random = new Random();
        server.setKey(random.nextInt(1000000));
        //System.out.println(serverRepo.save(server));
        serverRepo.save(server);
    }*/

    @DeleteMapping("/delete/{id}")
    public void deleteServer(@PathVariable("id") int id){
        serverService.deleteById(id);
    }

}
