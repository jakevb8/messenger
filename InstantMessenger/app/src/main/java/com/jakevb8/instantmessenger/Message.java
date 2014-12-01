package com.jakevb8.instantmessenger;

import java.io.Serializable;

/**
 * Created by Jake on 11/26/2014.
 */
public class Message implements Serializable{
    public int Id;
    public String UserId;
    public String UserName;
    public String TargetIp;
    public int TargetPort;
    public String Message;
}
