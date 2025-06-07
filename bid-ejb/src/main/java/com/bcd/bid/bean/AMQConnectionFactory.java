package com.bcd.bid.bean;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.activemq.ActiveMQConnectionFactory;

@ApplicationScoped
public class AMQConnectionFactory extends ActiveMQConnectionFactory {
    public AMQConnectionFactory() {
        super("tcp://localhost:61616");
    }
}
