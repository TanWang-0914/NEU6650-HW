import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;


public class KafkaProducerFactory extends BasePooledObjectFactory<KafkaProducer<String, String>> {

    private static final String produce_topic = "purchases_events";
    private static final String bootstrapServers = "3.235.146.135:9092";
    private static Properties properties;

    // "guest"/"guest" by default, limited to localhost connections
    //    ConnectionFactory factory = new ConnectionFactory();
    //factory.setUri("amqp://userName:password@hostName:portNumber/virtualHost");
    //    Connection conn = factory.newConnection();

    static {
        properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }


    @Override
    public KafkaProducer<String, String> create() throws Exception {
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        return producer;
    }

    @Override
    public PooledObject<KafkaProducer<String, String>> wrap(KafkaProducer<String, String> producer) {
        return new DefaultPooledObject<KafkaProducer<String, String>>(
                producer
        );
    }

    @Override
    public void passivateObject(PooledObject<KafkaProducer<String, String>> pooledObject) {
        pooledObject.getObject();
    }

}
