package browserhistorymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class BrowserHistory {
    private HistoryNode currentPage; // Current page
    private HistoryNode firstPage; // first page
    private ArrayList<HistoryNode> bookmarks; // List to store bookmarks
    private Stack<Action> undoStack;
    private Stack<Action> redoStack;
    private HashMap<String, HistoryNode> searchHistory; // HashMap to search by URL
    private final int MAX_UNDO_REDO_SIZE = 50;

    public BrowserHistory() {
        this.firstPage = null; // Initially, no pages visited
        this.currentPage = null; // Initially, no pages visited
        this.bookmarks = new ArrayList<>();
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.searchHistory = new HashMap<>();
    }

    public HistoryNode getFirstPage() {
        return firstPage;
    }

    public HistoryNode getCurrentPage() {
        return currentPage;
    }

    public ArrayList<HistoryNode> getBookmarks() {
        return bookmarks;
    }

    public String getCurrentPageUrl() {
        if (currentPage != null) {
            return currentPage.url;
        }
        return null;
    }

    public void visit(String url) {
        HistoryNode newPage = new HistoryNode(url);
        if (currentPage != null) {
            currentPage.next = null;
            currentPage.next = newPage; // Remove forward history
            newPage.prev = currentPage;
        } else {
            firstPage = newPage;
        }
        currentPage = newPage;
        // undoStack.push(new Action(Action.ActionType.VISIT, url));
        addToStack(undoStack, new Action(Action.ActionType.VISIT, url));
        redoStack.clear(); // Clear redo stack after a new action
        searchHistory.put(url, newPage); // Add the page to the searchable history
        System.out.println("Visited: " + url);
    }

    public void goBack() {
        if (currentPage != null && currentPage.prev != null) {
            // undoStack.push(new Action(Action.ActionType.BACK, currentPage.url));
            addToStack(undoStack, new Action(Action.ActionType.BACK, currentPage.url));
            redoStack.clear(); // Clear redo stack after a new action
            currentPage = currentPage.prev;

            System.out.println("Moved back to: " + currentPage.url);
        } else {
            System.out.println("Cannot go back, this is the first page.");
        }
    }

    public void goForward() {
        if (currentPage != null && currentPage.next != null) {
            // undoStack.push(new Action(Action.ActionType.FORWARD, currentPage.url));
            addToStack(undoStack, new Action(Action.ActionType.FORWARD, currentPage.url));
            redoStack.clear(); // Clear redo stack after a new action
            currentPage = currentPage.next;
            System.out.println("Moved forward to: " + currentPage.url);
        } else {
            System.out.println("Cannot go forward, this is the most recent page.");
        }
    }

    public void viewHistory() {
        if (currentPage == null) {

            System.out.println("No history to display.");
            return;
        }

        System.out.println("Browsing History:");
        HistoryNode temp = firstPage;
        while (temp != null) {
            if (temp == currentPage) {
                System.out.println(temp.url + " <-- Current Page");
            } else {
                System.out.println(temp.url);
            }
            temp = temp.next;
        }
    }

    public void clearHistory() {

        // undoStack.push(new Action(Action.ActionType.CLEAR, firstPage)); // Save the
        // state before clear
        // addToStack(undoStack, new Action(Action.ActionType.CLEAR, firstPage));
        // redoStack.clear(); // Clear redo stack after a new action
        undoStack.clear();
        firstPage = null;
        currentPage = null;
        searchHistory.clear();
        // bookmarks.clear();
        System.out.println("Browsing history cleared.");
    }

    // Advanced Features

    public boolean undo() {
        if (!undoStack.isEmpty()) {
            Action lastAction = undoStack.pop();
            switch (lastAction.getActionType()) {
                case VISIT:
                    // redoStack.push(new Action(Action.ActionType.VISIT, currentPage.url)); // Save
                    // the visit action
                    addToStack(redoStack, new Action(Action.ActionType.VISIT, currentPage.url));
                    if (currentPage.prev != null) {
                        currentPage = currentPage.prev;
                        currentPage.next = null;
                    } else {
                        currentPage = null;
                        firstPage = null;
                    }

                    break;

                case BACK:
                    // redoStack.push(new Action(Action.ActionType.BACK, currentPage.url)); // Save
                    // the back action
                    addToStack(redoStack, new Action(Action.ActionType.BACK, currentPage.url));
                    if (currentPage.next != null) {
                        currentPage = currentPage.next;
                    }
                    break;

                case FORWARD:
                    // redoStack.push(new Action(Action.ActionType.FORWARD, currentPage.url)); //
                    // Save forward action
                    addToStack(redoStack, new Action(Action.ActionType.FORWARD, currentPage.url));
                    if (currentPage.prev != null) {
                        currentPage = currentPage.prev;
                    }
                    break;

            }
            System.out.println("Undo performed.");
            return true;
        } else {
            System.out.println("Nothing to undo.");
            return false;
        }
    }

    public boolean redo() {
        if (!redoStack.isEmpty()) {
            Action redoAction = redoStack.pop();
            switch (redoAction.getActionType()) {
                case VISIT:
                    String url = redoAction.getUrl();
                    HistoryNode newPage = new HistoryNode(url);
                    if (currentPage != null) {
                        currentPage.next = null;
                        currentPage.next = newPage; // Remove forward history
                        newPage.prev = currentPage;
                    } else {
                        firstPage = newPage;
                    }
                    currentPage = newPage;
                    // undoStack.push(new Action(Action.ActionType.VISIT, url));
                    addToStack(undoStack, new Action(Action.ActionType.VISIT, url));
                    searchHistory.put(url, newPage); // Add the page to the searchable history
                    System.out.println("Visited: " + url);
                    break;

                case BACK:
                    goBack();
                    break;

                case FORWARD:
                    goForward();
                    break;

            }
            System.out.println("Redo performed.");
            return true;
        } else {
            System.out.println("Nothing to redo.");
            return false;
        }
    }

    // Bookmark a page
    public String bookmarkPage() {
        if (currentPage != null) {
            boolean flag = false;
            for (HistoryNode page : bookmarks) {
                if (page.getUrl().equals(currentPage.getUrl())) {
                    flag = true;
                }
            }
            if (!flag) {
                bookmarks.add(currentPage);
                System.out.println("Bookmarked: " + currentPage.url);
                return currentPage.url;
            }
            return null;

        } else {
            System.out.println("No page to bookmark.");
            return null;
        }
    }

    // View all bookmarks
    public void viewBookmarks() {
        if (bookmarks.isEmpty()) {
            System.out.println("No bookmarks.");
        } else {
            System.out.println("Bookmarks:");
            for (HistoryNode bookmark : bookmarks) {
                System.out.println(bookmark.url);
            }
        }
    }

    // Search the history by URL
    public String searchHistory(String url) {
        if (searchHistory.containsKey(url)) {
            System.out.println("Found in history: " + url);
            return url;
        } else {
            System.out.println("URL not found in history.");
            return null;
        }
    }

    // Jump to a bookmark
    public void jumpToBookmark(int index) {
        if (index >= 0 && index < bookmarks.size()) {
            // undoStack.push(new Action(Action.ActionType.FORWARD, currentPage.url));
            addToStack(undoStack, new Action(Action.ActionType.FORWARD, currentPage.url));
            currentPage = bookmarks.get(index);
            redoStack.clear(); // Clear redo history on jumping to a bookmark
            System.out.println("Jumped to bookmark: " + currentPage.url);
        } else {
            System.out.println("Invalid bookmark index.");
        }
    }

    private void addToStack(Stack<Action> stack, Action action) {
        if (stack.size() >= MAX_UNDO_REDO_SIZE) {
            stack.remove(0); // Remove the oldest action
        }
        stack.push(action);
    }

}
