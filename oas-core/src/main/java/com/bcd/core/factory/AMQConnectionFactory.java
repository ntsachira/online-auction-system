package com.bcd.core.factory;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.activemq.ActiveMQConnectionFactory;

@ApplicationScoped
public class AMQConnectionFactory extends ActiveMQConnectionFactory {
    public AMQConnectionFactory() {
        super("tcp://localhost:61616");
    }
}
