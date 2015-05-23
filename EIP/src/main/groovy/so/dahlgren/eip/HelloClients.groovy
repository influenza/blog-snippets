package so.dahlgren.eip

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException
import java.util.concurrent.CountDownLatch
import groovy.time.TimeCategory
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * Writes messages to the queue.
 */
@CompileStatic
class HelloProducer implements Runnable {
    CountDownLatch startLatch
    CountDownLatch stopLatch
    long millisToRun

    String queueName
    Channel out

    /**
     * Write the provided string to the queue.
     */
    void putMessage(String messageText) {
        if (messageText == null) { throw new NullPointerException("Null message text not allowed") }
        try {
            out.basicPublish(
                "", // Exchange (blank == default)
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

        long startTime = System.currentTimeMillis()
        long stopTime = startTime + millisToRun

        while (System.currentTimeMillis() < stopTime) {
            putMessage("Message from ${System.currentTimeMillis()}")
        }

        // Close the channel when we're finished with it
        this.out.close()
        stopLatch.countDown()
    }
}

/**
 * Reads messages from the queue and logs them.
 */
@CompileStatic
class HelloConsumer implements Runnable {
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
            println("[x] Grabbed message: ${grabMessage()}")
        }

        // Close the channel when we're finished with it
        this.inbound.close()
        stopLatch.countDown()
    }
}

@CompileStatic
class HelloClients {
    // Gimme the spread operator!
    @CompileStatic(value=TypeCheckingMode.SKIP)
    static void runHelloSample(ConnectionFactory factory, String queueName, long millisToRun) {

        int producers = 3
        int consumers = 2

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
                [ out: connection.createChannel().with { it.queueDeclare(*queueDeclarationParams); it } ]
            )
            new Thread(producer).start()
        }

        for (int i = 0; i < consumers; i++) {
            HelloConsumer consumer = new HelloConsumer(sharedParams +
                [ inbound: connection.createChannel().with { it.queueDeclare(*queueDeclarationParams); it } ]
            )

            new Thread(consumer).start()
        }

        println("Starting...")
        start.countDown()
        stop.await()
        println("Finished!")

        connection.close()
    }
}
