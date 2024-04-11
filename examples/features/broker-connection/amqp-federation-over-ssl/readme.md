# AMQP Broker Connection demonstrating Federation over SSL connections

To run the example, simply type **mvn verify** from this directory, or **mvn -PnoServer verify** if you want to create and start the broker manually.

This example demonstrates how you can federate messages sent to an Address on a remote server back to the local server and also instruct the remote server to federate messages sent to a Queue on the local server back to itself over a single AMQP connection. The connection is made using a connector and acceptor with SSL configured.

The broker accepting the connection needs an acceptor on the remote to connect to which is configured as follows

    <acceptor name="ssl-acceptor">tcp://localhost:5770?sslEnabled=true;keyStorePath=server-keystore.p12;keyStorePassword=securepass;keyStoreType=PKCS12</acceptor>

While the connecting broker needs to configure its broker connection URI to enable SSL and provide a trust store that include the broker certificate or certificate of the signing authority indicating the remote certificate can be trusted.

    <broker-connections>
      <amqp-connection uri="tcp://localhost:5770?sslEnabled=true;trustStorePath=server-ca-truststore.p12;trustStorePassword=securepass;trustStoreType=PKCS12" name="federation-example" retry-interval="100">
        ...
      </amqp-connection>
    </broker-connections>

The keystore and trustores used in the example were generated with store-generation.txt