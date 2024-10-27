package browserhistorymanager;

public class HistoryNode {
    String url;  
    HistoryNode prev;  
    HistoryNode next;  

    public HistoryNode(String url) {
        this.url = url;
        this.prev = null;
        this.next = null;
    }
    public String getUrl(){
        return url;
    }
}
