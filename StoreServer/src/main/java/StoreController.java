import com.rabbitmq.client.*;
import org.json.JSONObject;

import java.util.concurrent.*;

public class StoreController {

    static final ExecutorService servicePool;
    static StoreRecords storeRecords;
    private static final String EXCHANGE_NAME = "purchaseLogs";

    static {
        servicePool = Executors.newCachedThreadPool();
    }


    private static final String RPC_QUEUE_NAME = "rpc_queue";

    public static void main(String[] argv) throws Exception {
//        StoreRecords storeRecords = new StoreRecords();
//        System.out.println(storeRecords.storeArrays[0].get(0));
        storeRecords = new StoreRecords();
        ConnectionFactory factory = new ConnectionFactory();

        factory.setUsername("STORE_USER");
        factory.setPassword("STORE_PASSWORD");

        factory.setHost("3.236.58.246");
        Connection connectionForUpdate = factory.newConnection();
        Connection connectionForGet = factory.newConnection();

        final Channel channel = connectionForUpdate.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        // String queueName = channel.queueDeclare("store",true, false,false,null).getQueue();
        System.out.println(queueName);

        Thread[] threads = new Thread[50];
        for (int i = 0; i < threads.length; i++){
            threads[i] = new Thread(new RunnableUpdate(storeRecords, connectionForUpdate, queueName));
            threads[i].start();
        }

        Thread rpcThread = new Thread(new RunnableGet(storeRecords, connectionForGet, RPC_QUEUE_NAME));
        rpcThread.start();

//        Channel channelForGet = connectionForGet.createChannel();
//        channelForGet.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
//        channelForGet.queuePurge(RPC_QUEUE_NAME);
//
//        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
//
//        Object monitor = new Object();
//
//        DeliverCallback deliverCallbackOnGet = (consumerTag, delivery) -> {
//            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
//                    .Builder()
//                    .correlationId(delivery.getProperties().getCorrelationId())
//                    .build();
//
//            String response = "";
//
//            try {
//                String requestMessage = new String(delivery.getBody(), "UTF-8");
//                JSONObject requestObj = new JSONObject(requestMessage);
//                String op = requestObj.getString("op");
//                int param = Integer.parseInt(requestObj.getString("param"));
//
//                if (op.equals("store")){
//                    response = storeRecords.getStoreTop10Items(param-1);
//                }else if (op.equals("top10")){
//                    response = storeRecords.getItemTop5Store(param);
//                }
//
//            } catch (RuntimeException e) {
//                System.out.println(" [.] " + e.toString());
//            } finally {
//                channelForGet.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
//                channelForGet.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//                // RabbitMq consumer worker thread notifies the RPC server owner thread
//                synchronized (monitor) {
//                    monitor.notify();
//                }
//            }
//        };
//
//
//        channelForGet.basicConsume(RPC_QUEUE_NAME, false, deliverCallbackOnGet, (consumerTag -> { }));
//            // Wait and be prepared to consume the message from RPC client.
//            while (true) {
//                synchronized (monitor) {
//                    try {
//                        monitor.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
    }
}
