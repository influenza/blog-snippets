package so.dahlgren.eip.helloclients

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConsumerCancelledException
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException
import java.util.concurrent.CountDownLatch
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Reads messages from the queue and logs them.
 */
@CompileStatic
class HelloConsumer implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(HelloConsumer.class)

    String name
    CountDownLatch startLatch
    CountDownLatch stopLatch
    long millisToRun

    String queueName
    Channel inbound
    QueueingConsumer consumer

    /**
     * Waits `timeout` milliseconds for the next message delivery.
     * If a delivery is received, convert it to a UTF-8 string and return it,
     * return null otherwise.
     */
    private String grabMessage(long timeout = 100l) {
        String result = null
        try {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery(timeout)
            result = new String(delivery.getBody(), "UTF-8")
        } catch (InterruptedException ex) {

        } catch (ShutdownSignalException ex) {

        } catch (ConsumerCancelledException ex) {

        }
        return result
    }

    void run() {
        // Try or die
        try { startLatch.await() } catch (InterruptedException ex) { return }

        this.consumer = new QueueingConsumer(inbound)
        this.inbound.basicConsume(
            queueName,
            true, // autoAck - if true, sevrer assumes delivery == acknowledgement
            consumer // Callback handler
        )

        long startTime = System.currentTimeMillis()
        long stopTime = startTime + millisToRun

        while (System.currentTimeMillis() < stopTime) {
            println("[${name}] Grabbed message: ${grabMessage()}")
        }

        // Close the channel when we're finished with it
        this.inbound.close()
        stopLatch.countDown()
    }
}
