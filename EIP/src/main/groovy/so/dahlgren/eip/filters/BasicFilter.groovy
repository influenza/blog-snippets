package so.dahlgren.eip.filters

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import groovy.util.logging.Slf4j

@Slf4j
class BasicFilter extends DefaultConsumer {
    String name
    String inputQueueName
    String outputQueueName

    Channel input
    Channel output

    /**
     * This constructor must be accessible for the FilterFactory to function as designed.
     */
    BasicFilter(String name, Channel input, String inputQueueName, Channel output, String outputQueueName) {
        super(input)
        this.name = name
        this.input = input
        this.inputQueueName = inputQueueName
        this.outputQueueName = outputQueueName
        this.output = output

        this.additionalSetup()
        this.input.basicConsume(inputQueueName, false, this)
    }

    /**
     * Override as needed.
     * This method is called before binding to the input channel.
     */
    protected void additionalSetup() { }

    void handleDelivery(
        String consumerTag, Envelope envelope, AMQP.BasicProperties props, byte[] body
    ) {
        long deliveryTag = envelope.deliveryTag
        output.basicPublish( '', outputQueueName, null, filterLogic(body))
        input.basicAck(deliveryTag, false)
    }
    /**
     * To be overridden by subclasses.
     */
    byte[] filterLogic(byte[] messageBytes) {
        log.info("[{} @ {}] Received: ${new String(messageBytes, 'UTF-8')}", name, inputQueueName)
        messageBytes
    }

}
