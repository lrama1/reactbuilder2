package ${packageName}.common;

import java.util.List;

public class ListWrapper<T>{
    private List<T> rows;
    private int totalRecords;
    private int lastPage;
    private int currentPage = 1;
    private SortedIndicator sortedIndicator = new SortedIndicator();


    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public SortedIndicator getSortedIndicator() {
        return sortedIndicator;
    }

    public void setSortedIndicator(SortedIndicator sortedIndicator) {
        this.sortedIndicator = sortedIndicator;
    }
}