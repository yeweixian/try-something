package com.dangerye.stream;

import com.dangerye.stream.services.MessageProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = StreamProducerApplication8101.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class StreamProducerApplication8101Test {
    @Autowired
    private MessageProducer messageProducer;

    @Test
    public void testSendMessage() {
        messageProducer.sendMessage("hello, world. - 9394");
        System.out.println("send message...");
    }
}
