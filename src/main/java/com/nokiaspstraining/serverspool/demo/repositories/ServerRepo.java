package com.nokiaspstraining.serverspool.demo.repositories;

import com.nokiaspstraining.serverspool.demo.models.Server;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


public interface ServerRepo  extends AerospikeRepository<Server, Integer> {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Retryable
    Server findByAvaStorageGreaterThanEqualAndServerStatusIs(int size,String status);



}
