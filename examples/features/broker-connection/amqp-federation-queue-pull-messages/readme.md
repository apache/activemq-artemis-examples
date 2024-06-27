# AMQP Broker Connection example with Federation of Queue messages using pull mode

If you have not already done so, [prepare the broker distribution](../../../../README.md#getting-started) before running the example.

To run the example, simply type **mvn verify** from this directory, or **mvn -PnoServer verify** if you want to create and start the broker manually.

This example demonstrates the use of queue federation with configuration to only pull messages from one broker to another when there is no backlog on the broker pulling messages
