import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DynamoDBSource {
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
//                "ASIAYTP4QQCGFE32ESI5",
//                "UGdnmzgYDdhmQOYpzs1hPs6cVpbQntZlJNCvwSRq",
//                "FwoGZXIvYXdzEP7//////////wEaDAheTaH1wrJ2+c9siCLIAcSHKrGplkWCPBLNCJdAhVj1ve5a+Ufgddhv+LB8ihKPwpYt7uDOfSsVI2A89WeGxBRe+IXB5jH8YDNJ8bhgVzmSXI6NqVb+KxuGNjGqanAD1kzYnEtOwv/78JezIuCAOe0fjdh+5m8dO0/3rMNjbLveGOTcZtag79E0v2N3/7igY6Dv+OoVuS2ZUqzITme/TSLD3SG1i8n2hX7eOTyE4OW6eMiyy9w7x3P/MYcW+wSzn0NQPXI+SlU/f1NND9vG/TA82mBX73/xKLay/oIGMi2JroDS5JwtSMicg0yHeNyCLT38i/Pa4KaAJK6SCOftYWvShr0cPy+52F0zW+g="
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