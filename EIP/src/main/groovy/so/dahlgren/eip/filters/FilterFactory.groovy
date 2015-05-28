package so.dahlgren.eip.filters

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

class FilterFactory {
    static class FilterDefinition {
        Class<? extends BasicFilter> filterClass
        String filterName
    }

    static class PipelineConnection {
        Connection connection
        String inputQueue
        Channel input
        String outputQueue
        Channel output
    }

    static final List<Class> paramTypes = [
        String.class, // name
        Channel.class, // input
        String.class, // input queue
        Channel.class, // output
        String.class // output queue
    ].asImmutable()

    static PipelineConnection createFilters(
        String pipelineName,
        ConnectionFactory factory,
        List<FilterDefinition> pipeline
    ) {
        Connection connection = factory.newConnection()

        Channel input = connection.createChannel()
        String inputName = 'Input for ' + pipelineName
        // We declare the queues to auto-delete, meaning they are ephemeral and
        // will be removed once this pipeline disconnects.
        input.queueDeclare(inputName, false, false, true, null)

        Channel output = input
        String outputName

        PipelineConnection result = new PipelineConnection(
            connection: connection,
            input: input,
            inputQueue: inputName
        )

        BasicFilter current = null

        pipeline.each { FilterDefinition definition ->
            def constructor = definition.filterClass.getConstructor(*paramTypes)
            if (!constructor) {
                throw new IllegalArgumentException("${definition.filterClass} does not have the expected BasicFilter constructor")
            }
            output = connection.createChannel()
            outputName = "[${pipelineName}] from ${definition.filterName}"
            output.queueDeclare(outputName, false, false, true, null)

            current = constructor.newInstance(definition.filterName, input, inputName, output, outputName)

            input = output
            inputName = outputName
        }

        result.output = input
        result.outputQueue = inputName
        return result
    }
}
