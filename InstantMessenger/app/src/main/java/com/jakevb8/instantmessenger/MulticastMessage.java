package com.jakevb8.instantmessenger;

import java.io.Serializable;

/**
 * Created by jvanburen on 12/4/2014.
 */
public class MulticastMessage implements Serializable{
    public String deviceId;
    public String userName;
    public String message;
}
