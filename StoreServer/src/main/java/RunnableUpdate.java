import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONArray;
import org.json.JSONML;
import org.json.JSONObject;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class RunnableUpdate implements Runnable{

    StoreRecords storeRecords;
    private String bootstrapServers = "3.235.146.135:9092";
    private String groupId = "store-update-group";
    private String topic = "purchases_topic";
    private Properties properties;
    private KafkaConsumer<String, String> consumer;
    private CountDownLatch latch;

    public RunnableUpdate(StoreRecords storeRecords, CountDownLatch latch){
        this.storeRecords = storeRecords;
        properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        // create consumer
        consumer = new KafkaConsumer<String, String>(properties);
        // subscribe consumer to our topic(s)
        consumer.subscribe(Arrays.asList(topic));
        this.latch = latch;
    }

    @Override
    public void run() {

        try {
            while (true) {
                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(100)); // new in Kafka 2.0.0

                for (ConsumerRecord<String, String> record : records) {
                    String purchaseMessage = record.value();
                    update(purchaseMessage);
                }
            }
        }catch (WakeupException e){
            System.out.println("Received shutdown signal");
        }finally {
            consumer.close();
            latch.countDown();
        }
    }

    public void update(String purchaseMessage){

        JSONObject purchase = new JSONObject(purchaseMessage);

        int storeID = Integer.parseInt(purchase.getString("storeID"));

        String purchaseBody = purchase.getString("purchaseBody");

        JSONObject purchaseBodyJson = new JSONObject(purchaseBody);
        JSONArray purchaseItems = purchaseBodyJson.getJSONArray("items");
        for (int i = 0; i < purchaseItems.length(); i++){
            JSONObject item = purchaseItems.getJSONObject(i);
            int itemID = Integer.parseInt(item.getString("ItemID"))-1;
            int number = item.getInt("numberOfItems:");
//            storeRecords.storeArrays[storeID].getAndAdd(itemID,number);
            storeRecords.increase(storeID, itemID, number);
//             System.out.println("update success");
//             System.out.println("Increase store " + storeID + " item " + itemID + " by " + number);
        }

         System.out.println(" [x] Received '" + purchaseBody + "'");

    }

    public void shutdown() {
        // the wakeup() method is a special method to interrupt consumer.poll()
        // it will throw the exception WakeUpException
        consumer.wakeup();
    }
}
