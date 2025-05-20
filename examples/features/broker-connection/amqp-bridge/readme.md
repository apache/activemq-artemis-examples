# AMQP Broker Connection with bridge from and bridge to configurations

If you have not already done so, [prepare the broker distribution](../../../../README.md#getting-started) before running the example.

To run the example, simply type **mvn verify** from this directory, or **mvn -PnoServer verify** if you want to create and start the broker manually.

This example demonstrates how you can bridge messages sent to an Address on a remote server back to the local server and also instruct the local server to bridge messages sent to a Queue on the local server to a queue on the remote broker over single AMQP connection.
