package browserhistorymanager;

import java.util.Scanner;
import java.util.InputMismatchException;


public class UserInterface {
    private BrowserHistory browserHistory;
    private Scanner scanner;

    public UserInterface() {
        browserHistory = new BrowserHistory();
        scanner = new Scanner(System.in);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n1. Visit New Page");
            System.out.println("2. Go Back");
            System.out.println("3. Go Forward");
            System.out.println("4. View History");
            System.out.println("5. Clear History");
            System.out.println("6. Bookmark Current Page");
            System.out.println("7. View Bookmarks");
            System.out.println("8. Search History by URL");
            System.out.println("9. Undo");
            System.out.println("10. Redo");
            System.out.println("11. Jump to Bookmark");
            System.out.println("12. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                choice = 13;
            }
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    visitNewPage();
                    break;
                case 2:
                    browserHistory.goBack();
                    break;
                case 3:
                    browserHistory.goForward();
                    break;
                case 4:
                    browserHistory.viewHistory();
                    break;
                case 5:
                    browserHistory.clearHistory();
                    break;
                case 6:
                    browserHistory.bookmarkPage();
                    break;
                case 7:
                    browserHistory.viewBookmarks();
                    break;
                case 8:
                    searchByUrl();
                    break;
                case 9:
                    browserHistory.undo();
                    break;
                case 10:
                    browserHistory.redo();
                    break;
                case 11:
                    jumpToBookmark();
                    break;
                case 12:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
                    break;
            }
        }
    }

    private void visitNewPage() {
        System.out.print("Enter the URL: ");
        String url = scanner.nextLine();
        browserHistory.visit(url);
    }

    private void searchByUrl() {
        System.out.print("Enter URL to search: ");
        String url = scanner.nextLine();
        browserHistory.searchHistory(url);
    }

    private void jumpToBookmark() {
        System.out.print("Enter the bookmark index to jump to: ");
        int index = scanner.nextInt();
        browserHistory.jumpToBookmark(index);
    }
}
