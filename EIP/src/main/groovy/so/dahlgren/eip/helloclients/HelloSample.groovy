package so.dahlgren.eip.helloclients

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.CliBuilder
import groovy.util.logging.Slf4j
import groovy.util.OptionAccessor
import java.util.concurrent.CountDownLatch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import so.dahlgren.eip.Sample

/**
 * A basic sample to show publishing and retrieving messages with RabbitMQ.
 *
 * This sample creates a number of producers and a number of consumers, then sets
 * them to producing and consuming on a specified queue. The sample will run for a
 * specified number of milliseconds before exiting.
 */
@Slf4j
@CompileStatic
class HelloSample extends Sample {

    // CliBuilder is not CompileStatic friendly
    @CompileStatic(value=TypeCheckingMode.SKIP)
    HelloSample() {
        // Add additional specific options
        cliOptions.usage = 'HelloSample [options]'
        cliOptions.p(longOpt:'producers', args:1, argName:'number', 'Number of producers to spawn')
        cliOptions.c(longOpt:'consumers', args:1, argName:'number', 'Number of consumers to spawn')
        cliOptions.d(longOpt:'duration', args:1, argName:'milliseconds', 'Number of milliseconds to run')
    }


    // Gimme the spread operator!
    @CompileStatic(value=TypeCheckingMode.SKIP)
    void runSample(String[] args) {
        OptionAccessor options = parseArgs(args)

        ConnectionFactory factory = getConnectionFactory(options)

        int producers = (options.p ?: 4) as int
        int consumers = (options.c ?: 3) as int

        String queueName = options.'queue-name' ?: 'eip-one'

        String hostname = options.H ?: 'localhost'
        int port = (options.P ?: 5672) as int

        String username = options.U ?: 'guest'
        String password = options.password ?: 'guest'

        long duration = (options.d ?: 10000l) as long

        log.info(
            'Running {} producers with {} consumers on queue \"{}\" for {} millis',
            producers, consumers, queueName, duration
        )

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch stop = new CountDownLatch(producers + consumers);

        Connection connection = factory.newConnection()

        //                                     durable, exclusive, autoDelete, args
        List queueDeclarationParams = [ queueName, false, false, false, null ]

        Map sharedParams = [ queueName: queueName, startLatch: start, stopLatch: stop, millisToRun: duration ]

        for (int i = 0; i < producers; i++) {
            HelloProducer producer = new HelloProducer(sharedParams +
                [
                    name: "Producer-${i}",
                    out: connection.createChannel().with {
                        it.queueDeclare(*queueDeclarationParams)
                        it
                    }
                ]
            )
            new Thread(producer).start()
        }

        for (int i = 0; i < consumers; i++) {
            HelloConsumer consumer = new HelloConsumer(sharedParams +
                [
                    name: "Consumer-${i}",
                    inbound: connection.createChannel().with {
                        it.queueDeclare(*queueDeclarationParams)
                        it
                    }
                ]
            )

            new Thread(consumer).start()
        }

        log.info("Starting sample...")
        start.countDown()
        stop.await()
        log.info("Sample complete.")

        connection.close()
    }
}
