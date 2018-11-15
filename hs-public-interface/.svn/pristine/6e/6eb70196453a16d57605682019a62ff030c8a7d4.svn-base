package www.ucforward.com.index.message;

/**
 * 房源索引信息
 * @author wenbn
 * @version 1.0
 * @date 2018/6/25
 */
public class HouseIndexMessage {

    public static final String INDEX = "index";
    public static final String REMOVE = "remove";

    public static final int MAX_RETRY = 3;

    private int houseId;
    private String operation;
    private int retry = 0;

    /**
     * 默认构造器 防止jackson序列化失败
     */
    public HouseIndexMessage() {
    }

    public HouseIndexMessage(int houseId, String operation, int retry) {
        this.houseId = houseId;
        this.operation = operation;
        this.retry = retry;
    }

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }
}
