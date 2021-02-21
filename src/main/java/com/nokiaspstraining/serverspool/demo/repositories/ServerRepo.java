package com.nokiaspstraining.serverspool.demo.repositories;

import com.nokiaspstraining.serverspool.demo.models.Server;
import org.springframework.data.aerospike.repository.AerospikeRepository;



public interface ServerRepo  extends AerospikeRepository<Server, Integer> {


    Server findByAvaStorageGreaterThanEqualAndServerStatusIs(int size,String status);



}
