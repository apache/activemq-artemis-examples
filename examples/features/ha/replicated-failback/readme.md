# JMS Replicated Failback Example

## Preparation
If not already done, [Prepare the broker distribution](https://github.com/apache/activemq-artemis-examples?tab=readme-ov-file#prepare-the-broker-distribution).

## Running the Example
To run the example, simply execute the below command from this directory:
```
mvn verify
```

This example demonstrates two servers coupled as a live-backup pair for high availability (HA) using replication and a client connection failing over from live to backup when the live broker is crashed and then back to the original live when it is restarted (i.e. "failback").

For more information on ActiveMQ Artemis failover and HA, and clustering in general, please see the clustering section of the user manual.

