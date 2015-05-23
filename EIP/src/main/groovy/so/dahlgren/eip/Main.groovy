package so.dahlgren.eip

import com.rabbitmq.client.ConnectionFactory;
import groovy.transform.CompileStatic // <-- let's make Groovy a safer place
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import so.dahlgren.eip.helloclients.HelloSample

/**
 * Main entry point when running the samples as an application.
 */
@CompileStatic
class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class)

    static void main(String[] args) {
        logger.trace("Creating connection factory...")
        ConnectionFactory cf = new ConnectionFactory().with {
            it.setHost("localhost")
            it
        }
        logger.info("Starting HelloSample")
        HelloSample.runSample(cf, "eip-one", 10000)
    }
}
