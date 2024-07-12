# ActiveMQ Artemis Examples

This repository contains various examples demonstrating many of the features of all versions of [Apache ActiveMQ Artemis](https://activemq.apache.org/components/artemis/).

## Getting Started

### Prepare the broker distribution

To begin, run the following at the root of your examples checkout to prepare a broker distribution for use.

```
mvn clean package
```

This will establish a broker installation within the _artemis-distribution_ module in the root, which the individual examples will then reference while creating a broker instance when they are run.

### Running individual examples

After preparing the broker distribution as above, you can then run most individual examples by changing into their directory and running `mvn verify` or `mvn install` (See the readme.md file in each example directory for specific details).

For instance, a simple introductory example would be the ["queue"](examples/features/standard/queue/) example. To run it, do the following:

```
cd examples/features/standard/queue/
mvn clean verify
```

This will start a broker instance, using the previously prepared broker installation in _artemis-distribution_, and then run the example client application against it, which will print having produced and consumed a message.


## Contributing

See [CONTRIBUTING](CONTRIBUTING.md) for details.
