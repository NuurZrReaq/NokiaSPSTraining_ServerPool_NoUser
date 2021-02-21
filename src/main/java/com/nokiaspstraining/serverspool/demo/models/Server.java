package com.nokiaspstraining.serverspool.demo.models;


import com.aerospike.client.Key;
import com.nokiaspstraining.serverspool.demo.enums.Enumerations;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;
import org.springframework.data.annotation.Id;


@Document(collection = "serverpoool")
public class Server {

    @Id
    private int key;
    @Field
    private int avaStorage;
    @Field
    private String serverStatus;

    public Server() {
    }

    public Server(int key) {
        this.key = key;

    }

    public Server(int key, int avaStorage, Enumerations.ServerStatus serverStatus) {
        this.key = key;
        this.avaStorage = avaStorage;
        this.setServerStatus(serverStatus);
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getAvaStorage() {
        return avaStorage;
    }

    public void setAvaStorage(int avaStorage) {
        this.avaStorage = avaStorage;
    }


    public Enumerations.ServerStatus getServerStatus() {

        return Enumerations.ServerStatus.valueOf(serverStatus);
    }

    public void setServerStatus(Enumerations.ServerStatus serverStatus) {
        this.serverStatus = serverStatus.name();
    }

    @Override
    public String toString() {
        return "Server{" +
                "key=" + key +
                ", avaStorage=" + avaStorage +
                ", serverStatus='" + serverStatus + '\'' +
                '}';
    }
}
