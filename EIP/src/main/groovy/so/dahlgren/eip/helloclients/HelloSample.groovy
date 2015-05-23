package so.dahlgren.eip.helloclients

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import java.util.concurrent.CountDownLatch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class HelloSample {
    private static final Logger logger = LoggerFactory.getLogger(HelloSample.class)

    // Gimme the spread operator!
    @CompileStatic(value=TypeCheckingMode.SKIP)
    static void runSample(ConnectionFactory factory, String queueName, long millisToRun) {
        int producers = 3
        int consumers = 2

        logger.info('Sample will include {} producer threads and {} consumer threads', producers, consumers)
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch stop = new CountDownLatch(producers + consumers);

        Connection connection = factory.newConnection()

        List queueDeclarationParams = [
            queueName,
            false, // durable
            false, //exclusive
            false, // autoDelete
            null // Map<String,Object> args
        ]

        Map sharedParams = [
            queueName: queueName,
            startLatch: start,
            stopLatch: stop,
            millisToRun: millisToRun
        ]

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

        logger.info("Starting sample...")
        start.countDown()
        stop.await()
        logger.info("Sample complete.")

        connection.close()
    }
}
