import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.*;

public class RPCClient{

    private static final String rpc_send_topic = "rpc_requests";
    private static final String rpc_receive_topic = "rpc_responses";
    private static final String groupId = "rpc_group";
    private static final String bootstrapServers = "3.235.146.135:9092";
    private static Properties properties;

    public RPCClient() throws IOException, TimeoutException {
        properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    public String call(String message){
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        // create a producer record
        ProducerRecord<String, String> record =
                new ProducerRecord<String, String>(rpc_send_topic, message);

        // send data - asynchronous
        producer.send(record);

        // flush data
        producer.flush();
        // flush and close producer
        producer.close();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> result = executor.submit(new ConsumerCallable(bootstrapServers, groupId));

        String resultString = null;
        try{
            resultString = result.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return resultString;

    }


    public class ConsumerCallable implements Callable {

        private KafkaConsumer<String, String> consumer;

        public ConsumerCallable(String bootstrapServers, String groupId){
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            // properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

            // create consumer
            consumer = new KafkaConsumer<String, String>(properties);
        }

        @Override
        public String call() {
            // poll for new data
            String result = null;
            consumer.subscribe(Arrays.asList(rpc_receive_topic));
            try {
                loop: for(int i = 0; i < 600; i++) {
                    ConsumerRecords<String, String> records =
                            consumer.poll(Duration.ofMillis(100)); // new in Kafka 2.0.0

                    for (ConsumerRecord<String, String> record : records) {
                        if (record.value() != null){
                            result = record.value();
                            break loop;
                        }
                    }
                }
            } finally {
                consumer.close();
                // tell our main code we're done with the consumer
                return result;
            }
        }
    }
}
