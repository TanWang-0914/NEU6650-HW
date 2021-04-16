import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class RunnableGet implements Runnable{

    private static final String rpc_requests_topic = "rpc_requests";
    private static final String rpc_responses_topic = "rpc_responses";
    private static final String groupId = "rpc_group";
    private static final String bootstrapServers = "3.235.146.135:9092";
    private static Properties properties_con;
    private static Properties properties_pro;
    StoreRecords storeRecords;
    private KafkaConsumer<String, String> consumer;
    private KafkaProducer<String, String> producer;
    private CountDownLatch latch;

    public RunnableGet(StoreRecords storeRecords, CountDownLatch latch){
        this.storeRecords = storeRecords;

        properties_con = new Properties();
        properties_con.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties_con.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties_con.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties_con.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // properties_con.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        // create consumer
        consumer = new KafkaConsumer<String, String>(properties_con);
        // subscribe consumer to our topic(s)
        consumer.subscribe(Arrays.asList(rpc_requests_topic));

        properties_pro = new Properties();

        properties_pro.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties_pro.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties_pro.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        producer = new KafkaProducer<String, String>(properties_pro);

        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            String response = null;
            while (true) {
                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(100)); // new in Kafka 2.0.0

                for (ConsumerRecord<String, String> record : records) {
                    String requestMessage = record.value();
                    JSONObject requestObj = new JSONObject(requestMessage);
                    String op = requestObj.getString("op");
                    int param = Integer.parseInt(requestObj.getString("param"));

                    if (op.equals("store")) {
                        response = storeRecords.getStoreTop10Items(param);
                    } else if (op.equals("top10")) {
                        response = storeRecords.getItemTop5Store(param);
                    }

                    // create a producer record
                    ProducerRecord<String, String> response_record =
                            new ProducerRecord<String, String>(rpc_responses_topic, response);

                    // send data - asynchronous
                    producer.send(response_record);

                    // flush data
                    producer.flush();
                }
            }
        } catch (WakeupException e) {
            System.out.println("Received shutdown signal");
        } finally {
            consumer.close();
            producer.close();
            latch.countDown();

        }
    }

    public void shutdown() {
        // the wakeup() method is a special method to interrupt consumer.poll()
        // it will throw the exception WakeUpException
        consumer.wakeup();
    }
}

