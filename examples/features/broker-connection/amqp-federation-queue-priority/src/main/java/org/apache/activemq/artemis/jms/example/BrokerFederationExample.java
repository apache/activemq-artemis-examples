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
 * This example is demonstrating how messages are federated between two brokers when the federation
 * configuration applies a higher consumer priority than the default given to a standard consumer
 * that is listening on the broker where the message are actually produced to.
 */
public class BrokerFederationExample {

   public static void main(final String[] args) throws Exception {
      final ConnectionFactory connectionFactoryServer1 = new JmsConnectionFactory("amqp://localhost:5660");
      final ConnectionFactory connectionFactoryServer2 = new JmsConnectionFactory("amqp://localhost:5770");

      final Connection connectionOnServer1 = connectionFactoryServer1.createConnection();
      final Connection connectionOnServer2 = connectionFactoryServer2.createConnection();

      connectionOnServer1.start();
      connectionOnServer2.start();

      final Session sessionOnServer1 = connectionOnServer1.createSession(Session.AUTO_ACKNOWLEDGE);
      final Session sessionOnServer2 = connectionOnServer2.createSession(Session.AUTO_ACKNOWLEDGE);

      final Queue trackingQueue = sessionOnServer2.createQueue("tracking");

      // Consumer created on server 1 but federation consumer will have a higher priority
      final MessageConsumer consumerOn1 = sessionOnServer1.createConsumer(trackingQueue);
      final MessageConsumer consumerOn2 = sessionOnServer2.createConsumer(trackingQueue);

      final MessageProducer producerOn1 = sessionOnServer1.createProducer(trackingQueue);

      final TextMessage messageSent1 = sessionOnServer1.createTextMessage("message #1");
      final TextMessage messageSent2 = sessionOnServer1.createTextMessage("message #2");
      final TextMessage messageSent3 = sessionOnServer1.createTextMessage("message #3");

      producerOn1.send(messageSent1);
      producerOn1.send(messageSent2);
      producerOn1.send(messageSent3);

      // Server 1 consumer has lower priority than the federation consumer which was configured with
      // a positive priority offset so it should not get the message sent to the Queue.
      final TextMessage received1FromC2 = (TextMessage) consumerOn2.receive(10_000);
      final TextMessage received2FromC2 = (TextMessage) consumerOn2.receive(10_000);

      System.out.println("Consumer on server 2 received message: " + received1FromC2.getText());
      System.out.println("Consumer on server 2 received message: " + received2FromC2.getText());
      System.out.println("Consumer on server 2 received message: " + received2FromC2.getText());
   }
}
