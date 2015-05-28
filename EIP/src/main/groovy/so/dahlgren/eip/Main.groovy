package so.dahlgren.eip

import com.rabbitmq.client.ConnectionFactory;
import groovy.transform.CompileStatic // <-- let's make Groovy a safer place
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import so.dahlgren.eip.filters.BasicFilter
import so.dahlgren.eip.filters.FilterFactory
import so.dahlgren.eip.filters.UpperCaseFilter
import so.dahlgren.eip.helloclients.HelloSample
import so.dahlgren.eip.helloclients.HelloSample

import static so.dahlgren.eip.filters.FilterFactory.FilterDefinition
import static so.dahlgren.eip.filters.FilterFactory.PipelineConnection

/**
 * Main entry point when running the samples as an application.
 */
@CompileStatic
class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class)

    static void main(String[] args) {
        log.info("Constructing a pipeline of several vanilla BasicFilter instances")
        List simplePipeline = ['A', 'B', 'C', 'D'].collect { String name ->
            new FilterDefinition(filterClass: BasicFilter.class, filterName: name)
        }

        ConnectionFactory cf = new ConnectionFactory()
        PipelineConnection pipelineConnection = FilterFactory.createFilters('pipeline one', cf, simplePipeline)

        SimpleProducer producerOne = new SimpleProducer(
            channel: pipelineConnection.input,
            queueName: pipelineConnection.inputQueue
        )

        // Is there anyone out there?
        producerOne.sendMessage("Hellooooo")

        // Now let's actually do something
        List pipelineWithUpperCase = [
            new FilterDefinition(filterClass: BasicFilter.class, filterName: 'first basic filter'),
            new FilterDefinition(filterClass: UpperCaseFilter.class, filterName: 'upper-caser'),
            new FilterDefinition(filterClass: BasicFilter.class, filterName: 'last basic filter'),
        ]

        PipelineConnection secondConnection = FilterFactory.createFilters('pipeline two', cf, pipelineWithUpperCase)

        SimpleProducer producerTwo = new SimpleProducer(
            channel: secondConnection.input,
            queueName: secondConnection.inputQueue
        )

        producerTwo.sendMessage("no capital letters here! none at all.")

        println(">>>>>      CTRL-C to quit")
        println(">>>>>      For extra fun, inject some messages using the rabbitmq management interface")
        // Spin lock to let all the messages propagate
        while (true) {}
    }
}
