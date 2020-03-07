# Prime number stream

## Task
Develop a set of 2 small services that work together to deliver a sequence
of prime numbers up to a given number.

### Description:

Proxy-service
The `proxy-service` acts as an entry point to the outside world.
It's main tasks are:
● expose a HTTP endpoint over REST responding to GET /prime/<number>
that continuously streams all prime numbers up to a given <number>
e.g. /prime/17 should return 2,3,5,7,11,13,17.
● delegates the actual calculation to the second microservice via a gRPC RPC call
● handles wrong inputs in a proper way
Prime-number-server
The `prime-number-server` does the actual Prime number calculation -
it serves responses continuously over gRPC and
uses proper abstractions to communicate failure

## Running

```shell script
//in seperate terminal sessions
sbt primeNumberServer/run
sbt proxyService/run

```

## Result

In this experiment I choose to stick with Akka, as the toolkit provides all tools needs to conduct this experiment.

*Akka gRPC* provides ability to generate scala server and client code based on protobuf files.

*Akka HTTP* provides a platform to host the gRPC endpoint as well creating HTTP endpoint for the proxy service.

Wording in the task `continuously streams all prime numbers` mentioned that the results should be streamed.
gRPC provides this capability, but I decided to experiment additionally with Server Sent Events on the proxy server, as I never had a chance to do it before.

Results are quite interesting as currently this indeed streams the prime numbers in realtime end-to-end. This solution definitely benefits from small memory footprint.

## What can be done better

### Error handling

Most likely I didn't user correct abstractions to communicate failure, due to my inexperience with SSE, and my Akka Streams knowledge is currently rusty.
Currently the error message is part of the stream, so the client should terminate the connection if it would encounter the message.

Possibility is that we can lookup the first Reply received from the stream and return a correct HTTP error code, like 400 for negative number.

Another alternative would be to stream the whole response to the proxy and return its entirety, but the wording of the task didn't point to that and this can have memory usage implications.
Making it a basic HTTP endpoint. Also, that's boring.

### Architecture

Code architecture, or lack of its existence is visible here. In general I would prefer to apply here a hexagonal architecture pattern for each service, but that might be an overkill for this experiment.
For an actual production grade service, there is lack of monitoring, tracing and service discovery

### Tooling

I could add some sbt plugin, like formatting, or package updates.

### Testing

Some rudimentary tests in place. 

I would create an e2e test suite in docker packaging both apps into contains and run using TestContainers.


