import java.sql.*;
import org.apache.commons.dbcp2.*;

public class PurchaseDao {
    private static BasicDataSource dataSource;

    public PurchaseDao() {
        System.out.println("Creating PurchaseDao");
        dataSource = DBCPDataSource.getDataSource();
        System.out.println("datasource successfully get");
    }

    public void createPurchase(Purchase newPurchase) {
        System.out.println("Creating new Purchase");
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO Purchases (storeId, purchaseId, custId, itemID, itemNum, purchaseTime) " +
                "VALUES (?,?,?,?,?,?)";
        try {
            System.out.println("Try block of setting up Query");
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setInt(1, newPurchase.getStoreId());
            preparedStatement.setInt(2, newPurchase.getPurchaseId());
            preparedStatement.setInt(3, newPurchase.getCustId());
            preparedStatement.setInt(4, newPurchase.getItemID());
            preparedStatement.setInt(5, newPurchase.getItemNum());
            preparedStatement.setInt(6, newPurchase.getPurchaseDate());

            // execute insert SQL statement
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

//    public static void main(String[] args) {
//        PurchaseDao purchaseDao = new PurchaseDao();
//        purchaseDao.createPurchase(new Purchase(10, 4, 3, 5, 500, 20));
//    }
}
