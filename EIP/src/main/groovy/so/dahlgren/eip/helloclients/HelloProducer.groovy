package so.dahlgren.eip.helloclients

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import java.util.concurrent.CountDownLatch

/**
 * Writes messages to the queue.
 */
@Slf4j
@CompileStatic
class HelloProducer implements Runnable {

    String name
    CountDownLatch startLatch
    CountDownLatch stopLatch
    long millisToRun

    String queueName
    Channel out

    /**
     * Write the provided string to the queue.
     */
    void putMessage(String messageText) {
        log.trace('[{}] Sending message text: {}', name, messageText)
        if (messageText == null) { throw new NullPointerException('Null message text not allowed') }
        try {
            out.basicPublish(
                '', // Exchange (blank == default)
                this.queueName, // Routing key - i.e., our queue name
                null, // router headers, etc (com.rabbitmq.client.BasicProperties instance)
                messageText.getBytes() // message body
            )
        } catch (IOException ex) {
        }
    }

    void run() {
        // Try or die
        try { startLatch.await() } catch (InterruptedException ex) { return }
        log.info('[{}] Starting...', name)

        long startTime = System.currentTimeMillis()
        long stopTime = startTime + millisToRun

        while (System.currentTimeMillis() < stopTime) {
            putMessage("Message from ${System.currentTimeMillis()}")
        }

        log.info('[{}] Stopping...', name)
        // Close the channel when we're finished with it
        this.out.close()
        stopLatch.countDown()
    }
}


