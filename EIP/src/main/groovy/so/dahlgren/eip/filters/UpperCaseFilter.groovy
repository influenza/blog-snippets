package so.dahlgren.eip.filters

import com.rabbitmq.client.Channel

/**
 * Interpret incoming messages as a simple string, transform
 * it to uppercase according to current locale rules, and pass
 * it along.
 */
class UpperCaseFilter extends BasicFilter {
    UpperCaseFilter(String name, Channel input, String inputQueueName, Channel output, String outputQueueName) {
        super(name, input, inputQueueName, output, outputQueueName)
    }

    byte[] filterLogic(byte[] messageBytes) {
        String input = new String(messageBytes, 'UTF-8')
        input.toUpperCase() as byte[]
    }
}
