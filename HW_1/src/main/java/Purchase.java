public class Purchase {
    private int storeId;
    private int purchaseId;
    private int custId;
    private int itemID;
    private int itemNum;
    private int purchaseDate;


    public Purchase(int storeId, int purchaseId, int custId, int itemID, int itemNum, int purchaseDate) {
        this.storeId = storeId;
        this.purchaseId = purchaseId;
        this.custId = custId;
        this.itemID = itemID;
        this.itemNum = itemNum;
        this.purchaseDate = purchaseDate;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getItemNum() {
        return itemNum;
    }

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    public int getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(int purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
