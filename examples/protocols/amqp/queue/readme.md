# AMQP / Qpid JMS Queue example

If you have not already done so, [prepare the broker distribution](../../../../README.md#getting-started) before running the example.

To run the example, simply type **mvn verify** from this directory, or **mvn -PnoServer verify** if you want to start and create the broker manually.

This example uses the Qpid JMS AMQP 1.0 client to send a message to a queue and then recieve it. The example connects to port 5672, the IANA registered AMQP port.

Artemis listens for AMQP connections on port 5672 by default, and additionally on its default multi-protocol acceptor for port 61616, where it inspects the initial handshake of clients to determine what protocol to use.

    <acceptor name="amqp">tcp://0.0.0.0:5672?protocols=AMQP</acceptor>
