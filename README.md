# jobcoin-mixer

This is a jobcoin mixer server. It provides:
1) An API to obtain new addresses to send jobcoins to which is private to the user.
2) An API to send money to the private address.
3) Check for transaction logs

To run the project, first run:

<code>sbt assembly

This will build a fat jar in target/scala-2.12/jobcoin-mixer-assembly-0.1.jar

Run this jar using: 

<code>java -jar target/scala-2.12/jobcoin-mixer-assembly-0.1.jar

This will create a running http server at port 8080.

You can hit the server at 'localhost:8080' to see an explanation of the end points.

TODO:

1) This is not secure at all, with no authentication/authorization mechanism. Very prone to attacks.
2) The transaction logs are exposed to show the working of the API. Wouldnt be the case in real world.
3) Write proper integration/regression tests to check the working of the API.
4) There is no failover/recovery built into the system. We would absolutely need that.
5) Write more unit tests.

