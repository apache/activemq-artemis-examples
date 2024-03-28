# AMQP Broker Connection with Queue federation across three brokers

To run the example, simply type **mvn verify** from this directory, or **mvn -PnoServer verify** if you want to create and start the broker manually.

This example demonstrates the configuration of Queue federation across a set of three brokers where the message
flow is arranged as follows

```
  Producer -> A -> B -> C -> Consumer
```

A consumer on broker 'C' should receive messages sent by a producer on broker 'A'
