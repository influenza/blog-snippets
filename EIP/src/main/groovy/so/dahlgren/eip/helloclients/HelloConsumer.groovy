package so.dahlgren.eip.helloclients

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConsumerCancelledException
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException
import java.util.concurrent.CountDownLatch
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 * Reads messages from the queue and logs them.
 */
@Slf4j
@CompileStatic
class HelloConsumer implements Runnable {

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
            result = delivery ? new String(delivery.getBody(), "UTF-8") : null
        } catch (InterruptedException ex) {
            log.error("Interrupted!", ex)
        } catch (ShutdownSignalException ex) {
            log.error("Shutdown signal encountered!", ex)
        } catch (ConsumerCancelledException ex) {
            log.error("Consumer cancelled!", ex)
        }
        return result
    }

    void run() {
        // Try or die
        try { startLatch.await() } catch (InterruptedException ex) { return }
        log.info('[{}] Starting...', name)

        this.consumer = new QueueingConsumer(inbound)
        this.inbound.basicConsume(
            queueName,
            true, // autoAck - if true, sevrer assumes delivery == acknowledgement
            consumer // Callback handler
        )

        long startTime = System.currentTimeMillis()
        long stopTime = startTime + millisToRun

        while (System.currentTimeMillis() < stopTime) {
            String message = grabMessage()
            log.trace('[{}] Grabbed message: {}', name, message)
        }

        log.info('[{}] Stopping...', name)
        // Close the channel when we're finished with it
        this.inbound.close()
        stopLatch.countDown()
    }
}
