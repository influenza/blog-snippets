package so.dahlgren.eip

import com.rabbitmq.client.ConnectionFactory;
import groovy.transform.TypeCheckingMode
import groovy.util.CliBuilder
import groovy.util.logging.Slf4j
import groovy.util.OptionAccessor

/**
 * Base class for samples.
 * This class provides some reusable logic to ease the addition
 * of new samples.
 */
@Slf4j
class Sample {
    protected static final DEFAULT_QUEUE_NAME = 'eip-one'
    protected static final DEFAULT_USER_NAME = 'guest'
    protected static final DEFAULT_PASSWORD = 'guest'
    protected static final DEFAULT_HOST = 'localhost'
    protected static final DEFAULT_PORT = 5672
    // Common MQ options are specified here. Subclasses may add there
    // own additionally
    protected CliBuilder cliOptions = new CliBuilder().with {
        it.header = 'Options'
        it.H(longOpt:'host', args:1, argName:'hostname', 'Hostname running the message queue server')
        it.P(longOpt:'port', args:1, argName:'portnumber', 'Portnumber for the message queue server')
        it.U(longOpt:'user', args:1, argName:'username', 'Username for mq authentication')
        it._(longOpt:'password', args:1, argName:'password', 'Password for mq user')
        it._(longOpt:'queue-name', args:1, argName:'name', 'Name of queue to connect to')
        it._(longOpt:'help', 'Print usage information')
        it
    }

    protected OptionAccessor parseArgs(String[] args) {
        OptionAccessor options = cliOptions.parse(args)
        if (options.help) {
            System.err.println(cliOptions.usage())
            System.exit(-1)
        }
        options
    }

    ConnectionFactory getConnectionFactory(OptionAccessor options) {
        String queueName = options.'queue-name' ?: DEFAULT_QUEUE_NAME

        String hostname = options.H ?: DEFAULT_HOST
        int port = (options.P ?: DEFAULT_PORT) as int

        String username = options.U ?: DEFAULT_USER_NAME
        String password = options.password ?: DEFAULT_PASSWORD

        log.debug(
            "Connection settings: amqp://{}@{}:{}",
            username, hostname, port
        )
        new ConnectionFactory().with {
            it.setHost(hostname)
            it.setPort(port)
            it.setUsername(username)
            it.setPassword(password)
            it
        }
    }
}
