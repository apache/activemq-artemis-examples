# AMQP Broker Connection with Queue federation with configured higher priority

If you have not already done so, [prepare the broker distribution](../../../../README.md#getting-started) before running the example.

To run the example, simply type **mvn verify** from this directory, or **mvn -PnoServer verify** if you want to create and start the broker manually.

This example demonstrates the affect of consumer priority when using Queue federation, message will be moved to the broker federating messages as it has been given a higher consumer priority than a consumer on the local broker.
