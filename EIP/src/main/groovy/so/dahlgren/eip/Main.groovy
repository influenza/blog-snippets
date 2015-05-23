package so.dahlgren.eip

import com.rabbitmq.client.ConnectionFactory;
import groovy.transform.CompileStatic // <-- let's make Groovy a safer place

/**
 * Main entry point when running the samples as an application.
 */
@CompileStatic
class Main {
    static void main(String[] args) {
        ConnectionFactory cf = new ConnectionFactory().with {
            it.setHost("localhost")
            it
        }
        HelloClients.runHelloSample(cf, "eip-one", 10000)
    }
}
