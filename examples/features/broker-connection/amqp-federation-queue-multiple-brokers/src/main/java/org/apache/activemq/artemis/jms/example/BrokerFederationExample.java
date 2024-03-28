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
 * This example is demonstrating how queued messages are federated when a series of brokers are
 * configured as follows:
 *
 *  Producer -> A -> B -> C -> Consumer
 *
 * The intermediary broker B must be configured to treat a federation consumer as local demand so
 * that it will establish federation with A when broker C establishes federation with it due to it
 * having local demand from a real consumer.
 */
public class BrokerFederationExample {

   public static void main(final String[] args) throws Exception {
      {
         final ConnectionFactory connectionFactoryServerA = new JmsConnectionFactory("amqp://localhost:5670");

         try (Connection connectionOnServerA = connectionFactoryServerA.createConnection()) {
            final Session sessionOnServerA = connectionOnServerA.createSession(Session.AUTO_ACKNOWLEDGE);
            final Queue applicationQueue = sessionOnServerA.createQueue("applicationQueue");
            final MessageProducer producerOnA = sessionOnServerA.createProducer(applicationQueue);

            producerOnA.send(sessionOnServerA.createTextMessage("message #1"));
            producerOnA.send(sessionOnServerA.createTextMessage("message #2"));
            producerOnA.send(sessionOnServerA.createTextMessage("message #3"));
         }
      }

      // Consumer created on server C should receive message from producer on Server A
      final ConnectionFactory connectionFactoryServerC = new JmsConnectionFactory("amqp://localhost:5672");
      final Connection connectionOnServerC = connectionFactoryServerC.createConnection();
      final Session sessionOnServerC = connectionOnServerC.createSession(Session.AUTO_ACKNOWLEDGE);
      final Queue applicationQueue = sessionOnServerC.createQueue("applicationQueue");
      final MessageConsumer consumerOnC = sessionOnServerC.createConsumer(applicationQueue);

      connectionOnServerC.start();

      final TextMessage received1FromA = (TextMessage) consumerOnC.receive(10_000);
      final TextMessage received2FromA = (TextMessage) consumerOnC.receive(10_000);
      final TextMessage received3FromA = (TextMessage) consumerOnC.receive(10_000);

      System.out.println("Consumer on server C received message: " + received1FromA.getText());
      System.out.println("Consumer on server C received message: " + received2FromA.getText());
      System.out.println("Consumer on server C received message: " + received3FromA.getText());
   }
}
