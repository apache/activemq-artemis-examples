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
 * address with a set of brokers in a hub and spoke arrangement with each spoke
 * broker having bi-directional federation linking them to the hub. Messages in this
 * example are produced and consumed on the spokes and on the hub, any message sent
 * on one of the spokes or on the hub is available to any consumer on any other spoke
 * or at the hub as well as a consumer on the source.
 */
public class BrokerFederationExample {

   public static void main(final String[] args) throws Exception {

      final ConnectionFactory connectionFactoryHub = new JmsConnectionFactory("amqp://localhost:5460");
      final ConnectionFactory connectionFactorySpoke1 = new JmsConnectionFactory("amqp://localhost:5660");
      final ConnectionFactory connectionFactorySpoke2 = new JmsConnectionFactory("amqp://localhost:5760");
      final ConnectionFactory connectionFactorySpoke3 = new JmsConnectionFactory("amqp://localhost:5860");

      final Connection connectionOnHub = connectionFactoryHub.createConnection(); // Hub
      final Connection connectionOnSpoke1 = connectionFactorySpoke1.createConnection(); // Spoke 1
      final Connection connectionOnSpoke2 = connectionFactorySpoke2.createConnection(); // Spoke 2
      final Connection connectionOnSpoke3 = connectionFactorySpoke3.createConnection(); // Spoke 3

      connectionOnHub.start();
      connectionOnSpoke1.start();
      connectionOnSpoke2.start();
      connectionOnSpoke3.start();

      final Session sessionOnHub = connectionOnHub.createSession(Session.AUTO_ACKNOWLEDGE);
      final Session sessionOnSpoke1 = connectionOnSpoke1.createSession(Session.AUTO_ACKNOWLEDGE);
      final Session sessionOnSpoke2 = connectionOnSpoke2.createSession(Session.AUTO_ACKNOWLEDGE);
      final Session sessionOnSpoke3 = connectionOnSpoke3.createSession(Session.AUTO_ACKNOWLEDGE);

      final Topic ordersTopic = sessionOnSpoke1.createTopic("orders");

      final MessageConsumer ordersConsumerOnHub = sessionOnHub.createConsumer(ordersTopic);
      final MessageConsumer ordersConsumerOn1 = sessionOnSpoke1.createConsumer(ordersTopic);
      final MessageConsumer ordersConsumerOn2 = sessionOnSpoke2.createConsumer(ordersTopic);
      final MessageConsumer ordersConsumerOn3 = sessionOnSpoke3.createConsumer(ordersTopic);

      System.out.println("Starting example to produce and consume on hub and spoke brokers");

      // Producer on any given spoke or the hub should propagate to all consumers on all nodes
      final MessageProducer ordersProducerOnHub = sessionOnHub.createProducer(ordersTopic);
      final MessageProducer ordersProducerOn1 = sessionOnSpoke1.createProducer(ordersTopic);
      final MessageProducer ordersProducerOn2 = sessionOnSpoke2.createProducer(ordersTopic);
      final MessageProducer ordersProducerOn3 = sessionOnSpoke3.createProducer(ordersTopic);

      Thread.sleep(5000); // Allow time for federation links to be created.

      // Receive Message sent on spoke 1
      {
         ordersProducerOn1.send(sessionOnSpoke1.createTextMessage("message #1 from spoke 1"));

         final Message receivedHub = ordersConsumerOnHub.receive(10_000);
         final Message received1 = ordersConsumerOn1.receive(10_000);
         final Message received2 = ordersConsumerOn2.receive(10_000);
         final Message received3 = ordersConsumerOn3.receive(10_000);

         if (receivedHub == null) {
            System.out.println("Did not receive message on Hub that was expected");
         } else {
            final TextMessage mssage = (TextMessage) receivedHub;
            System.out.println("Received message on the Hub : " + mssage.getText());
         }

         if (received1 == null) {
            System.out.println("Did not receive message on spoke 1 that was expected");
         } else {
            final TextMessage mssage = (TextMessage) received1;
            System.out.println("Received message on spoke 1 : " + mssage.getText());
         }

         if (received2 == null) {
            System.out.println("Did not receive message on spoke 2 that was expected");
         } else {
            final TextMessage mssage = (TextMessage) received2;
            System.out.println("Received message on spoke 2 : " + mssage.getText());
         }

         if (received3 == null) {
            System.out.println("Did not receive message on spoke 3 that was expected");
         } else {
            final TextMessage mssage = (TextMessage) received3;
            System.out.println("Received message on spoke 3 : " + mssage.getText());
         }
      }

      // Receive Message sent on spoke 2
      {
         ordersProducerOn2.send(sessionOnSpoke2.createTextMessage("message #2 from spoke 2"));

         final Message receivedHub = ordersConsumerOnHub.receive(10_000);
         final Message received1 = ordersConsumerOn1.receive(10_000);
         final Message received2 = ordersConsumerOn2.receive(10_000);
         final Message received3 = ordersConsumerOn3.receive(10_000);

         if (receivedHub == null) {
            System.out.println("Did not receive message on Hub that was expected");
         } else {
            final TextMessage mssage = (TextMessage) receivedHub;
            System.out.println("Received message on the Hub : " + mssage.getText());
         }

         if (received1 == null) {
            System.out.println("Did not receive message on spoke 1 that was expected");
         } else {
            final TextMessage mssage = (TextMessage) received1;
            System.out.println("Received message on spoke 1 : " + mssage.getText());
         }

         if (received2 == null) {
            System.out.println("Did not receive message on spoke 2 that was expected");
         } else {
            final TextMessage mssage = (TextMessage) received2;
            System.out.println("Received message on spoke 2 : " + mssage.getText());
         }

         if (received3 == null) {
            System.out.println("Did not receive message on spoke 3 that was expected");
         } else {
            final TextMessage mssage = (TextMessage) received3;
            System.out.println("Received message on spoke 3 : " + mssage.getText());
         }
      }

      // Receive Message sent on spoke 3
      {
         ordersProducerOn3.send(sessionOnSpoke3.createTextMessage("message #3 from spoke 3"));

         final Message receivedHub = ordersConsumerOnHub.receive(10_000);
         final Message received1 = ordersConsumerOn1.receive(10_000);
         final Message received2 = ordersConsumerOn2.receive(10_000);
         final Message received3 = ordersConsumerOn3.receive(10_000);

         if (receivedHub == null) {
            System.out.println("Did not receive message on Hub that was expected");
         } else {
            final TextMessage mssage = (TextMessage) receivedHub;
            System.out.println("Received message on the Hub : " + mssage.getText());
         }

         if (received1 == null) {
            System.out.println("Did not receive message on spoke 1 that was expected");
         } else {
            final TextMessage mssage = (TextMessage) received1;
            System.out.println("Received message on spoke 1 : " + mssage.getText());
         }

         if (received2 == null) {
            System.out.println("Did not receive message on spoke 2 that was expected");
         } else {
            final TextMessage mssage = (TextMessage) received2;
            System.out.println("Received message on spoke 2 : " + mssage.getText());
         }

         if (received3 == null) {
            System.out.println("Did not receive message on spoke 3 that was expected");
         } else {
            final TextMessage mssage = (TextMessage) received3;
            System.out.println("Received message on spoke 3 : " + mssage.getText());
         }
      }

      // Receive Message sent on Hub
      {
         ordersProducerOnHub.send(sessionOnHub.createTextMessage("message #4 from hub"));

         final Message receivedHub = ordersConsumerOnHub.receive(10_000);
         final Message received1 = ordersConsumerOn1.receive(10_000);
         final Message received2 = ordersConsumerOn2.receive(10_000);
         final Message received3 = ordersConsumerOn3.receive(10_000);

         if (receivedHub == null) {
            System.out.println("Did not receive message on Hub that was expected");
         } else {
            final TextMessage mssage = (TextMessage) receivedHub;
            System.out.println("Received message on the Hub : " + mssage.getText());
         }

         if (received1 == null) {
            System.out.println("Did not receive message on spoke 1 that was expected");
         } else {
            final TextMessage mssage = (TextMessage) received1;
            System.out.println("Received message on spoke 1 : " + mssage.getText());
         }

         if (received2 == null) {
            System.out.println("Did not receive message on spoke 2 that was expected");
         } else {
            final TextMessage mssage = (TextMessage) received2;
            System.out.println("Received message on spoke 2 : " + mssage.getText());
         }

         if (received3 == null) {
            System.out.println("Did not receive message on spoke 3 that was expected");
         } else {
            final TextMessage mssage = (TextMessage) received3;
            System.out.println("Received message on spoke 3 : " + mssage.getText());
         }
      }
   }
}
