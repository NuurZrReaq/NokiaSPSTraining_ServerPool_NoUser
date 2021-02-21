package com.nokiaspstraining.serverspool.demo.repositories;

import com.aerospike.client.Key;
import com.nokiaspstraining.serverspool.demo.models.Server;
import org.springframework.data.aerospike.repository.AerospikeRepository;


public interface ServerRepo  extends AerospikeRepository<Server, Integer> {


}
