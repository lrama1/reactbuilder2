package ${packageName}.common;

public class SortedIndicator {
    private String columnKey;
    private String direction;

    public SortedIndicator() {
        super();
    }

    public SortedIndicator(String columnKey, String direction) {
        super();
        this.columnKey = columnKey;
        this.direction = direction;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
