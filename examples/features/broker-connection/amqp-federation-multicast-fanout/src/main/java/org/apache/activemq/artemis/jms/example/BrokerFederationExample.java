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
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.qpid.jms.JmsConnectionFactory;

/**
 * This example is demonstrating federation of multicast messages in a fan-out style topology
 */
public class BrokerFederationExample {

   public static void main(final String[] args) throws Exception {
      final ConnectionFactory connectionFactorySource = new JmsConnectionFactory("amqp://localhost:5060");
      final ConnectionFactory connectionFactoryTier21 = new JmsConnectionFactory("amqp://localhost:5261");
      final ConnectionFactory connectionFactoryTier22 = new JmsConnectionFactory("amqp://localhost:5262");
      final ConnectionFactory connectionFactoryTier23 = new JmsConnectionFactory("amqp://localhost:5263");
      final ConnectionFactory connectionFactoryTier24 = new JmsConnectionFactory("amqp://localhost:5264");

      final Connection connectionOnSource = connectionFactorySource.createConnection();
      final Connection connectionOnTier21 = connectionFactoryTier21.createConnection();
      final Connection connectionOnTier22 = connectionFactoryTier22.createConnection();
      final Connection connectionOnTier23 = connectionFactoryTier23.createConnection();
      final Connection connectionOnTier24 = connectionFactoryTier24.createConnection();

      connectionOnSource.start();
      connectionOnTier21.start();
      connectionOnTier22.start();
      connectionOnTier23.start();
      connectionOnTier24.start();

      final Session sessionOnSource = connectionOnSource.createSession(Session.AUTO_ACKNOWLEDGE);
      final Session sessionOnTier21 = connectionOnTier21.createSession(Session.AUTO_ACKNOWLEDGE);
      final Session sessionOnTier22 = connectionOnTier22.createSession(Session.AUTO_ACKNOWLEDGE);
      final Session sessionOnTier23 = connectionOnTier23.createSession(Session.AUTO_ACKNOWLEDGE);
      final Session sessionOnTier24 = connectionOnTier24.createSession(Session.AUTO_ACKNOWLEDGE);

      final Topic fanoutTopic = sessionOnSource.createTopic("fanout");

      // Consume on tier two brokers and message will be fanned out from the source through tier one
      final MessageConsumer consumerOnTier21 = sessionOnTier21.createConsumer(fanoutTopic);
      final MessageConsumer consumerOnTier22 = sessionOnTier22.createConsumer(fanoutTopic);
      final MessageConsumer consumerOnTier23 = sessionOnTier23.createConsumer(fanoutTopic);
      final MessageConsumer consumerOnTier24 = sessionOnTier24.createConsumer(fanoutTopic);

      Thread.sleep(3_000); // Allow time for federation links to be created.

      final MessageProducer producerOnSource = sessionOnSource.createProducer(fanoutTopic);
      final TextMessage messageFromSource = sessionOnSource.createTextMessage("Message sent to source");

      producerOnSource.send(messageFromSource);

      final TextMessage receivedByConsumerOnTier21 = (TextMessage) consumerOnTier21.receive(5_000);
      final TextMessage receivedByConsumerOnTier22 = (TextMessage) consumerOnTier22.receive(5_000);
      final TextMessage receivedByConsumerOnTier23 = (TextMessage) consumerOnTier23.receive(5_000);
      final TextMessage receivedByConsumerOnTier24 = (TextMessage) consumerOnTier24.receive(5_000);

      System.out.println("Consumer on server Tier 2-1 received msage: " + receivedByConsumerOnTier21.getText());
      System.out.println("Consumer on server Tier 2-2 received msage: " + receivedByConsumerOnTier22.getText());
      System.out.println("Consumer on server Tier 2-3 received msage: " + receivedByConsumerOnTier23.getText());
      System.out.println("Consumer on server Tier 2-4 received msage: " + receivedByConsumerOnTier24.getText());
   }
}
