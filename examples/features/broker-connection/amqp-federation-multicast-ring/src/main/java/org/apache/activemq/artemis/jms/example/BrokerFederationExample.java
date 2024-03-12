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
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.qpid.jms.JmsConnectionFactory;

/**
 * This example is demonstrating how messages are federated across a multicast
 * address with a set of brokers in a ring topology.
 */
public class BrokerFederationExample {

   public static void main(final String[] args) throws Exception {

      final ConnectionFactory connectionFactoryServer1 = new JmsConnectionFactory("amqp://localhost:5660");
      final ConnectionFactory connectionFactoryServer2 = new JmsConnectionFactory("amqp://localhost:5760");
      final ConnectionFactory connectionFactoryServer3 = new JmsConnectionFactory("amqp://localhost:5860");
      final ConnectionFactory connectionFactoryServer4 = new JmsConnectionFactory("amqp://localhost:5960");

      final Connection connectionOnServer1 = connectionFactoryServer1.createConnection(); // Server 1
      final Connection connectionOnServer2 = connectionFactoryServer2.createConnection(); // Server 2
      final Connection connectionOnServer3 = connectionFactoryServer3.createConnection(); // Server 3
      final Connection connectionOnServer4 = connectionFactoryServer4.createConnection(); // Server 4

      connectionOnServer1.start();
      connectionOnServer2.start();
      connectionOnServer3.start();
      connectionOnServer4.start();

      final Session sessionOnServer1 = connectionOnServer1.createSession(Session.AUTO_ACKNOWLEDGE);
      final Session sessionOnServer2 = connectionOnServer2.createSession(Session.AUTO_ACKNOWLEDGE);
      final Session sessionOnServer3 = connectionOnServer3.createSession(Session.AUTO_ACKNOWLEDGE);
      final Session sessionOnServer4 = connectionOnServer4.createSession(Session.AUTO_ACKNOWLEDGE);

      final Topic ringTopic = sessionOnServer1.createTopic("ringAddress");

      final MessageConsumer consumerOn1 = sessionOnServer1.createConsumer(ringTopic);
      final MessageConsumer consumerOn2 = sessionOnServer2.createConsumer(ringTopic);
      final MessageConsumer consumerOn3 = sessionOnServer3.createConsumer(ringTopic);
      final MessageConsumer consumerOn4 = sessionOnServer4.createConsumer(ringTopic);

      System.out.println("Starting example to produce and consume on ring of brokers");

      // Producer on any given server or the Server4 should propagate to all consumers on all nodes
      final MessageProducer producerOn1 = sessionOnServer1.createProducer(ringTopic);
      final MessageProducer producerOn2 = sessionOnServer2.createProducer(ringTopic);
      final MessageProducer producerOn3 = sessionOnServer3.createProducer(ringTopic);
      final MessageProducer producerOn4 = sessionOnServer4.createProducer(ringTopic);

      Thread.sleep(5000); // Allow time for federation links to be created.

      // Receive Message sent on Server 1
      {
         producerOn1.send(sessionOnServer1.createTextMessage("message #1 from Server 1"));

         final Message received1 = consumerOn1.receive(10_000);
         final Message received2 = consumerOn2.receive(10_000);
         final Message received3 = consumerOn3.receive(10_000);
         final Message received4 = consumerOn4.receive(10_000);

         if (received1 == null) {
            System.out.println("Did not receive message on server 1 that was expected");
         } else {
            final TextMessage message = (TextMessage) received1;
            System.out.println("Received message on server 1 : " + message.getText());
         }

         if (received2 == null) {
            System.out.println("Did not receive message on server 2 that was expected");
         } else {
            final TextMessage message = (TextMessage) received2;
            System.out.println("Received message on server 2 : " + message.getText());
         }

         if (received3 == null) {
            System.out.println("Did not receive message on server 3 that was expected");
         } else {
            final TextMessage message = (TextMessage) received3;
            System.out.println("Received message on server 3 : " + message.getText());
         }

         if (received4 == null) {
            System.out.println("Did not receive message on server 4 that was expected");
         } else {
            final TextMessage message = (TextMessage) received4;
            System.out.println("Received message on server 4 : " + message.getText());
         }
      }

      // Receive Message sent on Server 2
      {
         producerOn2.send(sessionOnServer2.createTextMessage("message #2 from Server 2"));

         final Message received1 = consumerOn1.receive(10_000);
         final Message received2 = consumerOn2.receive(10_000);
         final Message received3 = consumerOn3.receive(10_000);
         final Message received4 = consumerOn4.receive(10_000);

         if (received1 == null) {
            System.out.println("Did not receive message on server 1 that was expected");
         } else {
            final TextMessage message = (TextMessage) received1;
            System.out.println("Received message on server 1 : " + message.getText());
         }

         if (received2 == null) {
            System.out.println("Did not receive message on server 2 that was expected");
         } else {
            final TextMessage message = (TextMessage) received2;
            System.out.println("Received message on server 2 : " + message.getText());
         }

         if (received3 == null) {
            System.out.println("Did not receive message on server 3 that was expected");
         } else {
            final TextMessage message = (TextMessage) received3;
            System.out.println("Received message on server 3 : " + message.getText());
         }

         if (received4 == null) {
            System.out.println("Did not receive message on server 4 that was expected");
         } else {
            final TextMessage message = (TextMessage) received4;
            System.out.println("Received message on server 4 : " + message.getText());
         }
      }

      // Receive Message sent on Server 3
      {
         producerOn3.send(sessionOnServer3.createTextMessage("message #3 from Server 3"));

         final Message received1 = consumerOn1.receive(10_000);
         final Message received2 = consumerOn2.receive(10_000);
         final Message received3 = consumerOn3.receive(10_000);
         final Message received4 = consumerOn4.receive(10_000);

         if (received1 == null) {
            System.out.println("Did not receive message on server 1 that was expected");
         } else {
            final TextMessage message = (TextMessage) received1;
            System.out.println("Received message on server 1 : " + message.getText());
         }

         if (received2 == null) {
            System.out.println("Did not receive message on server 2 that was expected");
         } else {
            final TextMessage message = (TextMessage) received2;
            System.out.println("Received message on server 2 : " + message.getText());
         }

         if (received3 == null) {
            System.out.println("Did not receive message on server 3 that was expected");
         } else {
            final TextMessage message = (TextMessage) received3;
            System.out.println("Received message on server 3 : " + message.getText());
         }

         if (received4 == null) {
            System.out.println("Did not receive message on server 4 that was expected");
         } else {
            final TextMessage message = (TextMessage) received4;
            System.out.println("Received message on server 4 : " + message.getText());
         }
      }

      // Receive Message sent on Server 4
      {
         producerOn4.send(sessionOnServer4.createTextMessage("message #4 from Server 4"));

         final Message received1 = consumerOn1.receive(10_000);
         final Message received2 = consumerOn2.receive(10_000);
         final Message received3 = consumerOn3.receive(10_000);
         final Message received4 = consumerOn4.receive(10_000);

         if (received1 == null) {
            System.out.println("Did not receive message on server 1 that was expected");
         } else {
            final TextMessage message = (TextMessage) received1;
            System.out.println("Received message on server 1 : " + message.getText());
         }

         if (received2 == null) {
            System.out.println("Did not receive message on server 2 that was expected");
         } else {
            final TextMessage message = (TextMessage) received2;
            System.out.println("Received message on server 2 : " + message.getText());
         }

         if (received3 == null) {
            System.out.println("Did not receive message on server 3 that was expected");
         } else {
            final TextMessage message = (TextMessage) received3;
            System.out.println("Received message on server 3 : " + message.getText());
         }

         if (received4 == null) {
            System.out.println("Did not receive message on server 4 that was expected");
         } else {
            final TextMessage message = (TextMessage) received4;
            System.out.println("Received message on server 4 : " + message.getText());
         }
      }
   }
}
