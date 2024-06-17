# AMQP Broker Connection with local and remote Federation

If you have not already done so, [prepare the broker distribution](../../../../README.md#getting-started) before running the example.

To run the example, simply type **mvn verify** from this directory, or **mvn -PnoServer verify** if you want to create and start the broker manually.

This example demonstrates how you can federate messages sent to an Address on a remote server back to the local server and also instruct the remote server to federate messages sent to a Queue on the local server back to itself over a single AMQP connection.
