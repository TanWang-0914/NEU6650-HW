import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

public class PurchaseDao {
    //    private static BasicDataSource dataSource;
    private DynamoDB dynamoDB;
    private String tableName;
    Table table;

    public PurchaseDao(String tableName) {

        System.out.println("Creating PurchaseDao");
        this.tableName = tableName;
        dynamoDB = DynamoDBSource.getDataSource();
        table = dynamoDB.getTable(tableName);
        System.out.println("datasource successfully get");
    }


    /**
     * write purchase to dynamoDB, with primary key string purchaseID,
     * @param purchaseID
     * @param storeID
     * @param custID
     * @param date
     * @param purchaseBody
     */
    public void createPurchase(String purchaseID, String storeID, String custID, String date, String purchaseBody){

        Item item = new Item().withPrimaryKey("PurchaseID", purchaseID).withString("StoreID", storeID)
                .withString("CustID", custID).withString("PurchaseDate", date).withString("PurchaseBody", purchaseBody);
        table.putItem(item);
    }

}