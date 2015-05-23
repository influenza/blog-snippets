EIP Sample Code
===============

This repository provides sample Groovy code to explore the messaging patterns described in
Hohpe and Woolf's text, [Enterprise Integration Patterns](http://www.amazon.com/Enterprise-Integration-Patterns-Designing-Deploying/dp/0321200683).

For additional information, see [the blog](http://dahlgren.so/categories.html#eip-ref).

To run:

  `gradle run`

For more options:

    user@host$ java -jar path/to/this.jar --help
    usage: HelloSample [options]
    Options
     -c,--consumers <number>        Number of consumers to spawn
     -d,--duration <milliseconds>   Number of milliseconds to run
     -H,--host <hostname>           Hostname running the message queue server
        --help                      Print usage information
     -P,--port <portnumber>         Portnumber for the message queue server
     -p,--producers <number>        Number of producers to spawn
        --password <password>       Password for mq user
        --queue-name <name>         Name of queue to connect to
     -U,--user <username>           Username for mq authentication
