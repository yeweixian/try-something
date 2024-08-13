package com.dangerye.stream.services.impl;

import com.dangerye.stream.services.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

@EnableBinding(Source.class)
public class MessageProducerImpl implements MessageProducer {
    @Autowired
    private Source source;

    @Override
    public void sendMessage(String msg) {
        final MessageChannel output = source.output();
        final Message<String> message = MessageBuilder.withPayload(msg).build();
        output.send(message);
    }
}
