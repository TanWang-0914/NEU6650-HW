
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class RabbitMQChannelFactory extends BasePooledObjectFactory<Channel> {

    private static ConnectionFactory factory;
    private static Connection connection;

    // "guest"/"guest" by default, limited to localhost connections
    //    ConnectionFactory factory = new ConnectionFactory();
    //factory.setUri("amqp://userName:password@hostName:portNumber/virtualHost");
    //    Connection conn = factory.newConnection();

    static {
        factory = new ConnectionFactory();
        factory.setUsername("SERVER_USER");
        factory.setPassword("SERVER_PASSWORD");

        // change for running!!!
        factory.setHost("3.236.58.246");
        try {
            connection = factory.newConnection();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Channel create() throws Exception {
        Channel channel = connection.createChannel();
        return channel;
    }

    @Override
    public PooledObject<Channel> wrap(Channel channel) {
        return new DefaultPooledObject<Channel>(
                channel
        );
    }

    @Override
    public void passivateObject(PooledObject<Channel> pooledObject) {
        pooledObject.getObject();
    }

}
