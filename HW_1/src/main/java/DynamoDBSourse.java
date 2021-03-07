import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.apache.commons.dbcp2.*;

public class DynamoDBSourse {
    static AmazonDynamoDB client;
    static DynamoDB dynamoDB;
//    private static String tableName = "Purchases";
//    private static BasicDataSource dataSource;


    // NEVER store sensitive information below in plain text!
    private static final String awsAccessKey = System.getProperty("aws_access_key_id");
    private static final String awsSecretKey = System.getProperty("aws_secret_access_key");
    private static final String awsSessionToken = System.getProperty("aws_session_token");


    static {
//        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
//                "ASIAYTP4QQCGGOEAOUWV",
//                "7XpAV0oHIiWoxBF7KdrSuX/l6Wh6lpv5EUNKzneP",
//                "FwoGZXIvYXdzEH4aDJ6+x1N+3iFFXtPdACLIATDycv8V2WeXBesnrSiwCkX7jqt08/32TadxqwYI4me2FSuPcgQXcjN1clVTzYS2flIBjc+YQ1dK0CWsqDrZbtnA2ZWGzQCgwZbY5fbh1hP/24uEBNp2l4EFL199siKkL9OwTmcbd4pVefKyS+YTAPXc8uGSYzCZXErEgvqymjyqjrUMO6uyaeDT/aSgbQ16wrsjp4OXDtPpuvVkXcEfO4pNUibCQ+42QMPS0QLVNhijWfE7mVuBdGCrc+l9uyF9GsRooAp4HB+kKKLe8YEGMi2lMYG20kOxXElMW6M5ruM4xKfgFYxFREf/lTG+T1aurSmBH7AVjDvwelrd6mY="
//        );
        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                awsAccessKey,
                awsSecretKey,
                awsSessionToken
        );

        client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                .withRegion(Regions.US_EAST_1)
                .build();
        dynamoDB = new DynamoDB(client);

    }

    public static DynamoDB getDataSource() {
        return dynamoDB;
    }
}