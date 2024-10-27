package browserhistorymanager;

public class Action {
    public enum ActionType {
        VISIT, BACK, FORWARD
    }

    private ActionType actionType;
    private String url;  
    private HistoryNode pageBeforeClear;  

    public Action(ActionType actionType, String url) {
        this.actionType = actionType;
        this.url = url;
    }

    public Action(ActionType actionType, HistoryNode pageBeforeClear) {
        this.actionType = actionType;
        this.pageBeforeClear = pageBeforeClear;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getUrl() {
        return url;
    }

    public HistoryNode getPageBeforeClear() {
        return pageBeforeClear;
    }
}
