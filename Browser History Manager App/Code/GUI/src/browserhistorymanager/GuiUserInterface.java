package browserhistorymanager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;
public class GuiUserInterface extends JFrame {
    private JPanel sidebarPanel = new JPanel();
    private DefaultListModel<String> historyListModel = new DefaultListModel<>();
    private DefaultListModel<String> bookmarkListModel = new DefaultListModel<>();
    private JList<String> historyList = new JList<>(historyListModel);
    private JList<String> bookmarkList = new JList<>(bookmarkListModel);
    private JTextField urlField = new JTextField(20);
    private JTextField searchField = new JTextField(20);
    // private JTextArea mainContentArea = new JTextArea(20, 40);
    JEditorPane editorPane = new JEditorPane();
    private JButton backButton = new JButton("Back");
    private JButton forwardButton = new JButton("Forward");
    private JButton undoButton = new JButton("Undo");
    private JButton redoButton = new JButton("Redo");
    private JButton visitButton = new JButton("Visit");
    private JButton clearHistoryButton = new JButton("Clear History");
    private JButton addBookmarkButton = new JButton("Add Bookmark");
    private JButton searchButton = new JButton("Search");
    private JTextArea searchResults = new JTextArea(1, 15);
    private JButton toggleSearchResultsButton = new JButton("-");
    BrowserHistory browser = new BrowserHistory();

    public GuiUserInterface() {
        super("Browser History Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(true);

        ImageIcon frameImageIcon = new ImageIcon("resources/resources/browser_icon2.png");
        setIconImage(frameImageIcon.getImage());

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navigationPanel.setMaximumSize(new Dimension(600, 40));
        navigationPanel.add(undoButton);
        navigationPanel.add(redoButton);
        navigationPanel.add(backButton);
        navigationPanel.add(forwardButton);
        navigationPanel.add(new JLabel("URL: "));
        navigationPanel.add(urlField);
        navigationPanel.add(visitButton);
        undoButton.setFocusable(false);
        redoButton.setFocusable(false);
        backButton.setFocusable(false);
        forwardButton.setFocusable(false);
        visitButton.setFocusable(false);
        mainPanel.add(navigationPanel, BorderLayout.NORTH);

        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(300, 500));
        sidebarPanel.setMaximumSize(new Dimension(300, 500));
        // Search Panel
        searchButton.setBounds(190, 0, 55, 20);
        searchButton.setBorder(BorderFactory.createEmptyBorder());
        searchButton.setFocusable(false);
        searchField.setBounds(0, 2, 188, 20);
        // searchField.setBorder(BorderFactory.createEmptyBorder());
        // searchField.setFocusable(false);
        JPanel searchPanel = new JPanel(null);
        searchPanel.setPreferredSize(new Dimension(300, 20));
        searchPanel.setMaximumSize(new Dimension(600, 20));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        // searchPanel.setBackground(Color.CYAN);
        toggleSearchResultsButton.setFont(new Font("Arial", Font.BOLD, 14));
        toggleSearchResultsButton.setBounds(280, 3, 15, 20);
        toggleSearchResultsButton.setBorder(BorderFactory.createEmptyBorder());
        toggleSearchResultsButton.setFocusable(false);
        toggleSearchResultsButton.addActionListener(e -> toggleSearchResultsVisibility());
        // searchPanel.add(toggleSearchResultsButton);
        // sidebarPanel.add(new JLabel("Search"));
        sidebarPanel.add(searchPanel);

        searchResults.setEditable(false);
        sidebarPanel.add(new JScrollPane(searchResults));
        // sidebarPanel.add(toggleSearchResultsButton);
        // Browsing History Panel
        JPanel browserHistoryPanel = new JPanel(null);
        browserHistoryPanel.setPreferredSize(new Dimension(300, 20));
        browserHistoryPanel.setMaximumSize(new Dimension(600, 20));
        JLabel browserHistoryLabel = new JLabel("Browsing History");
        browserHistoryLabel.setBounds(3, 2, 100, 20);
        browserHistoryPanel.add(browserHistoryLabel);
        clearHistoryButton.setBounds(210, 2, 90, 20);
        clearHistoryButton.setBorder(BorderFactory.createEmptyBorder());
        clearHistoryButton.setFocusable(false);
        browserHistoryPanel.add(clearHistoryButton);
        sidebarPanel.add(browserHistoryPanel);
        sidebarPanel.add(new JScrollPane(historyList));
        bookmarkList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                // Override without calling super, so no selection occurs
            }
        });
        JPanel bookmarkPanel = new JPanel(null);
        bookmarkPanel.setPreferredSize(new Dimension(300, 20));
        bookmarkPanel.setMaximumSize(new Dimension(600, 20));
        JLabel bookmarkLabel = new JLabel("Bookmarks");
        bookmarkLabel.setBounds(3, 2, 100, 20);
        bookmarkPanel.add(bookmarkLabel);
        addBookmarkButton.setBounds(210, 2, 90, 20);
        addBookmarkButton.setBorder(BorderFactory.createEmptyBorder());
        addBookmarkButton.setFocusable(false);
        bookmarkPanel.add(addBookmarkButton);
        sidebarPanel.add(bookmarkPanel);
        // Bookmarks Panel
        // sidebarPanel.add(new JLabel("Bookmarks"));

        sidebarPanel.add(new JScrollPane(bookmarkList));
        // sidebarPanel.add(addBookmarkButton);

        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Main Content Area
        // JPanel contentPanel = new JPanel(new BorderLayout());
        // contentPanel.add(new JLabel("Current Page:"), BorderLayout.NORTH);
        // mainContentArea.setEditable(false);
        // contentPanel.add(new JScrollPane(mainContentArea), BorderLayout.CENTER);

        // mainPanel.add(contentPanel, BorderLayout.CENTER);
        // JEditorPane editorPane = new JEditorPane();
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(new JLabel("Current Page:"), BorderLayout.NORTH);
        editorPane.setEditable(false);
        // editorPane.add(new JLabel("Current Page:"), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(editorPane);
        contentPanel.add(scrollPane,BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        // mainPanel.add(scrollPane, BorderLayout.CENTER);


        // Create a panel for the footer section
        JPanel footerSection = new JPanel();
        footerSection.setLayout(new BorderLayout());
        footerSection.setBackground(Color.LIGHT_GRAY);

        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel copyrightLabel = new JLabel();
        copyrightLabel.setText("© 2024 Sahil. All rights reserved.");
        footerPanel.add(copyrightLabel);

        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(400, 1));
        separator.setBackground(new Color(157, 163, 88));
        separator.setForeground(new Color(0, 0, 0));

        footerSection.add(separator, BorderLayout.NORTH);
        footerSection.add(footerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
        add(footerSection, BorderLayout.SOUTH);
        // Display the frame
        setVisible(true);

        // Initialize button actions (placeholder methods)
        addListeners();
    }

    private void addListeners() {
        visitButton.addActionListener(e -> visitPage());
        backButton.addActionListener(e -> goBack());
        forwardButton.addActionListener(e -> goForward());
        undoButton.addActionListener(e -> undoAction());
        redoButton.addActionListener(e -> redoAction());
        clearHistoryButton.addActionListener(e -> clearHistory());
        addBookmarkButton.addActionListener(e -> addBookmark());
        searchButton.addActionListener(e -> searchHistory());
    }

    private void toggleSearchResultsVisibility() {
        boolean isVisible = searchResults.isVisible();
        searchResults.setVisible(!isVisible);
        toggleSearchResultsButton.setText(isVisible ? "+" : "-");

        // Update layout to accommodate visibility change
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    // Placeholder action methods
    private void visitPage() {
        String url = urlField.getText();
        if (!url.isEmpty()) {
            // historyListModel.addElement(url);
            browser.visit(url);
            showBrowserPage(url);
            // mainContentArea.setText(url);
            urlField.setText("");

            showBrowsingHistory();
            if (browser.getCurrentPage() != null) {
                // mainContentArea.setText(browser.getCurrentPage().getUrl());
                showBrowserPage(url);
            }
        }
    }

    private void goBack() {
        // mainContentArea.setText("Going back...");
        browser.goBack();
        showBrowsingHistory();
        if (browser.getCurrentPage() != null) {
            // mainContentArea.setText(browser.getCurrentPage().getUrl());
            showBrowserPage(browser.getCurrentPage().getUrl());
        }
    }

    private void goForward() {
        // mainContentArea.setText("Going forward...");
        browser.goForward();
        showBrowsingHistory();
        if (browser.getCurrentPage() != null) {
            // mainContentArea.setText(browser.getCurrentPage().getUrl());
            showBrowserPage(browser.getCurrentPage().getUrl());
        }
    }

    private void undoAction() {
        // mainContentArea.setText("Undo action...");
        if(browser.undo()){
            showBrowsingHistory();
            if(browser.getCurrentPage()!=null){
                // mainContentArea.setText(browser.getCurrentPage().getUrl());
                showBrowserPage(browser.getCurrentPage().getUrl());
            }
            else{
                // mainContentArea.setText("");
                showBrowserPage("");
            }
        }

    }

    private void redoAction() {
        if(browser.redo()){
            showBrowsingHistory();
            if(browser.getCurrentPage()!=null){
                // mainContentArea.setText(browser.getCurrentPage().getUrl());
                showBrowserPage(browser.getCurrentPage().getUrl());
            }
            else{
                // mainContentArea.setText("");
                showBrowserPage("");
            }
        }
    }

    private void clearHistory() {
        historyListModel.clear();
        browser.clearHistory();
        // mainContentArea.setText("History cleared");
        if(browser.getCurrentPage()!=null){

            showBrowserPage(browser.getCurrentPage().getUrl());
        }
    }

    private void addBookmark() {
        // String url = urlField.getText();
        HistoryNode node = browser.getCurrentPage();
        String url = "";
        if (node != null) {
            url = node.getUrl();
        }
        if (!url.isEmpty()) {
            browser.bookmarkPage();
            showBookmarks();
        }
    }

    private void searchHistory() {
        String query = searchField.getText();
        if (!query.isEmpty()) {
            // mainContentArea.setText("Searching for: " + query);
            searchField.setText("");
            String result = browser.searchHistory(query);
            if (result != null) {
                searchResults.setText(result);
            } else {
                searchResults.setText("No page Found");
            }
        }
    }

    public void showBrowsingHistory() {
        int currentIndex = historyListModel.getSize();
        while (currentIndex > 0) {
            historyListModel.remove(currentIndex - 1);
            currentIndex--;
        }
        HistoryNode firstPage = browser.getFirstPage();
        if (firstPage != null) {
            HistoryNode current = firstPage;
            HistoryNode workingPage = browser.getCurrentPage();
            if (current == workingPage) {
                historyListModel.addElement("current-->> " + current.getUrl());
            } else {

                historyListModel.addElement(current.getUrl());
            }
            while (current.next != null) {
                current = current.next;
                if (current == workingPage) {
                    historyListModel.addElement("current-->>" + current.getUrl());
                } else {

                    historyListModel.addElement(current.getUrl());
                }
            }

        }

    }

    public void showBookmarks() {
        int currentIndex = bookmarkListModel.getSize();
        while (currentIndex > 0) {
            bookmarkListModel.remove(currentIndex - 1);
            currentIndex--;
        }
        for (HistoryNode page : browser.getBookmarks()) {
            bookmarkListModel.addElement(page.getUrl());
        }

    }
    public void jumpToBookmark(){
         
    }
    public void showBrowserPage(String text){
        editorPane.setContentType("text/html");
        editorPane.setText("<html><body><h2>"+ text+" Loading...</h2></body></html>");
        SwingUtilities.invokeLater(() ->{
            try {
                // Replace with the URL you want to display
              // Convert the string to a URI
            URI uri = new URI(text);
            
            // Check if the URI is absolute
            if (!uri.isAbsolute()) {
                // Handle the relative URI case
                URI baseUri = new URI("http://example.com/"); // Use a relevant base URI
                uri = baseUri.resolve(uri); // Convert to absolute URI
            }

            // Convert URI to URL
            URL url = uri.toURL();
            
            // Attempt to connect to the URL
            if (isUrlReachable(url)) {
                // Load the URL (replace with actual loading logic)
                System.out.println("Loading URL: " + url);
                // e.g., open in a web browser or fetch data from it
            } else {
                throw new IOException("URL is not reachable: " + url);
            }

                // URL url = new URL(text);
                editorPane.setPage(url);
            } 
            catch (MalformedURLException e) {
                editorPane.setContentType("text/html");
                editorPane.setText("<html> "+ text+" Could not load</html>");
            } 
            catch (IOException e) {
                editorPane.setContentType("text/html");
                editorPane.setText("<html> "+ text+" Could not load</html>");
            }
            catch (URISyntaxException e) {
            // Handle the exception if the URI is malformed
            editorPane.setContentType("text/html");
                editorPane.setText("<html> "+ text+" Could not load</html>");
        }
        });
        

        
    }
    public boolean isUrlReachable(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // Use HEAD to check if it's reachable
            connection.setConnectTimeout(3000); // Set timeout for connecting
            connection.setReadTimeout(3000); // Set timeout for reading response
            int responseCode = connection.getResponseCode();
            return (responseCode >= 200 && responseCode < 400); // 2xx and 3xx are reachable
        } catch (IOException e) {
            return false; // If an exception occurs, the URL is not reachable
        }
    }

    // public static void main(String[] args) {
    // SwingUtilities.invokeLater(GuiUserInterface::new);
    // }
}
