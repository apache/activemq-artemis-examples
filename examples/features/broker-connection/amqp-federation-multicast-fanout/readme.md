# AMQP Broker Connection demonstrating fanout of multicast messages

If you have not already done so, [prepare the broker distribution](../../../../README.md#getting-started) before running the example.

To run the example, simply type **mvn verify** from this directory, or **mvn -PnoServer verify** if you want to create and start the broker manually.

This example demonstrates the fanning out of messages sent to a single broker to a tiered set of federated brokers.
