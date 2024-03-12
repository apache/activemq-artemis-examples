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
 * This example is demonstrating how messages are federated two brokers with dual Queue federation
 * configured such that message on the Queue will move between brokers as a consumer moves.
 */
public class BrokerFederationExample {

   public static void main(final String[] args) throws Exception {
      final ConnectionFactory connectionFactoryServer1 = new JmsConnectionFactory("amqp://localhost:5660");
      final ConnectionFactory connectionFactoryServer2 = new JmsConnectionFactory("amqp://localhost:5770");

      final Connection connectionOnServer1 = connectionFactoryServer1.createConnection();
      final Connection connectionOnServer2 = connectionFactoryServer2.createConnection();

      connectionOnServer1.start();
      connectionOnServer2.start();

      Thread.sleep(3_000); // Allow federation connections to build

      final Session sessionOnServer1 = connectionOnServer1.createSession(Session.AUTO_ACKNOWLEDGE);
      final Session sessionOnServer2 = connectionOnServer2.createSession(Session.AUTO_ACKNOWLEDGE);

      final Queue trackingQueue = sessionOnServer2.createQueue("tracking");

      final MessageConsumer consumerOn2 = sessionOnServer2.createConsumer(trackingQueue);
      final MessageProducer producerOn1 = sessionOnServer1.createProducer(trackingQueue);

      final TextMessage messageSent1 = sessionOnServer1.createTextMessage("message #1");
      final TextMessage messageSent2 = sessionOnServer1.createTextMessage("message #2");

      producerOn1.send(messageSent1);
      producerOn1.send(messageSent2);

      // Messages sent on server 1 will move to server 2
      final TextMessage receivedFromC2 = (TextMessage) consumerOn2.receive(10_000);

      System.out.println("Consumer on server 2 received message: " + receivedFromC2.getText());

      consumerOn2.close();

      // Message should move back to server 1 since consumer demand on server 2 is gone
      final MessageConsumer consumerOn1 = sessionOnServer1.createConsumer(trackingQueue);

      final TextMessage receivedFromC1 = (TextMessage) consumerOn1.receive(10_000);

      System.out.println("Consumer on server 1 received message: " + receivedFromC1.getText());
   }
}
