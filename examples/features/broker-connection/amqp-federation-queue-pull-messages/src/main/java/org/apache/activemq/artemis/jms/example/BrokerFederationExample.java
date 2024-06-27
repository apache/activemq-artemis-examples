/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.artemis.jms.example;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.qpid.jms.JmsConnectionFactory;

/**
 * This example is demonstrating how messages are federated two brokers with Queue federation
 * configured such that messages are pulled only when a queue on the target broker has no pending
 * messages with a batch size of 1.
 */
public class BrokerFederationExample {

   public static void main(final String[] args) throws Exception {
      // Consumers are pull consumers so message stay on the server until a receive call is made.
      final ConnectionFactory connectionFactoryServer1 = new JmsConnectionFactory("amqp://localhost:5660?jms.prefetchPolicy.all=0");
      final ConnectionFactory connectionFactoryServer2 = new JmsConnectionFactory("amqp://localhost:5770?jms.prefetchPolicy.all=0");

      final Connection connectionOnServer1 = connectionFactoryServer1.createConnection();
      final Connection connectionOnServer2 = connectionFactoryServer2.createConnection();

      connectionOnServer1.start();
      connectionOnServer2.start();

      final Session sessionOnServer1 = connectionOnServer1.createSession(Session.AUTO_ACKNOWLEDGE);
      final Session sessionOnServer2 = connectionOnServer2.createSession(Session.AUTO_ACKNOWLEDGE);

      final Queue trackingQueue = sessionOnServer2.createQueue("tracking");

      final MessageConsumer consumerOn1 = sessionOnServer1.createConsumer(trackingQueue);
      final MessageProducer producerOn1 = sessionOnServer1.createProducer(trackingQueue);
      final MessageProducer producerOn2 = sessionOnServer2.createProducer(trackingQueue);

      Thread.sleep(3_000); // Allow federation connections to build

      final TextMessage messageSent1 = sessionOnServer2.createTextMessage("message #1");
      final TextMessage messageSent2 = sessionOnServer2.createTextMessage("message #2");
      final TextMessage messageSent3 = sessionOnServer2.createTextMessage("message #3");

      producerOn2.send(messageSent1); // Should get pulled to server 1
      producerOn2.send(messageSent2);
      producerOn2.send(messageSent3);

      final MessageConsumer consumerOn2 = sessionOnServer2.createConsumer(trackingQueue);

      // These messages sent to server 2 should stay there as server 1 now has backlog
      final TextMessage receivedFromC2 = (TextMessage) consumerOn2.receive(10_000);
      final TextMessage receivedFromC3 = (TextMessage) consumerOn2.receive(10_000);

      System.out.println("Consumer on server 2 received message: " + receivedFromC2.getText());
      System.out.println("Consumer on server 2 received message: " + receivedFromC3.getText());

      final TextMessage receivedFromC1 = (TextMessage) consumerOn1.receive(10_000);

      System.out.println("Consumer on server 1 received message: " + receivedFromC1.getText());

      // Now create local backlog on server 1 which should prevent any messages being federated
      // from server 2 and the local consumer on server 2 should be able to receive them

      final TextMessage messageSent4 = sessionOnServer1.createTextMessage("message #4");
      final TextMessage messageSent5 = sessionOnServer1.createTextMessage("message #5");
      final TextMessage messageSent6 = sessionOnServer1.createTextMessage("message #6");

      producerOn1.send(messageSent4);
      producerOn1.send(messageSent5);
      producerOn1.send(messageSent6);

      // These should stay local to server 2 and be consumed when pulled by the consumer on 2

      final TextMessage messageSent7 = sessionOnServer2.createTextMessage("message #7");
      final TextMessage messageSent8 = sessionOnServer2.createTextMessage("message #8");
      final TextMessage messageSent9 = sessionOnServer2.createTextMessage("message #9");

      producerOn2.send(messageSent7);
      producerOn2.send(messageSent8);
      producerOn2.send(messageSent9);

      final TextMessage receivedFromC7 = (TextMessage) consumerOn2.receive(10_000);
      final TextMessage receivedFromC8 = (TextMessage) consumerOn2.receive(10_000);
      final TextMessage receivedFromC9 = (TextMessage) consumerOn2.receive(10_000);

      System.out.println("Consumer on server 2 received message: " + receivedFromC7.getText());
      System.out.println("Consumer on server 2 received message: " + receivedFromC8.getText());
      System.out.println("Consumer on server 2 received message: " + receivedFromC9.getText());

      connectionOnServer1.close();
      connectionOnServer2.close();
   }
}
