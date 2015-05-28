package so.dahlgren.eip

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class SimpleProducer {
    Channel channel
    String queueName

    void sendMessage(String message) {
        log.trace("Sending message ${message} to queue ${queueName}")
        try {
            channel.basicPublish('', this.queueName, null, message as byte[])
        } catch (IOException ex) {
            log.error("IOException trying to publish message: ${ex}")
        }
    }
}
