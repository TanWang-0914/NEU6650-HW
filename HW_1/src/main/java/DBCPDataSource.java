import org.apache.commons.dbcp2.*;

public class DBCPDataSource {
    private static BasicDataSource dataSource;

    // NEVER store sensitive information below in plain text!
//    private static final String HOST_NAME = System.getProperty("MySQL_IP_ADDRESS");
//    private static final String PORT = System.getProperty("MySQL_PORT");
    private static final String HOST_NAME = "database-1.cmqf5gzhd3rt.us-east-1.rds.amazonaws.com";
    private static final String PORT = "3306";
    private static final String DATABASE = "database_1";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "12345678";
//    private static final String USERNAME = System.getProperty("DB_USERNAME");
//    private static final String PASSWORD = System.getProperty("DB_PASSWORD");


    static {
        // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
        System.out.println("milestone1");
        System.out.println(HOST_NAME);
        dataSource = new BasicDataSource();
        try {
            System.out.println("milestone2");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("milestone3");
        } catch (ClassNotFoundException e) {
            System.out.println("milestone4");
            e.printStackTrace();
        }
        System.out.println("milestone5");
        String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
        dataSource.setUrl(url);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setInitialSize(10);
        dataSource.setMaxTotal(60);
        System.out.println(url);
    }

    public static BasicDataSource getDataSource() {
        return dataSource;
    }
}