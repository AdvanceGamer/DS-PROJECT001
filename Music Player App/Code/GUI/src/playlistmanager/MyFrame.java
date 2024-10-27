package playlistmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
// import java.awt.*;
import java.awt.Image;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

// import org.w3c.dom.events.MouseEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
// import javax.swing.JSlider;
// import javazoom.jl.decoder.Control;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.datatransfer.*;
import java.util.ArrayList;
import java.util.List;
// import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyFrame extends JFrame implements PlaylistListener {
    JPanel sidebarPanel = new JPanel();
    Playlist playlist = new Playlist();

    // SearchResult searchResult;
    DefaultListModel<String> playlistListModel = new DefaultListModel<>();
    boolean checkManualRepeatTrigger = false;
    JList<String> playlistList = new JList<>(playlistListModel);
    JCheckBoxMenuItem repeat = new JCheckBoxMenuItem("Repeat", false);
    JButton playButton = new JButton("Play");
    JButton pauseButton = new JButton("Pause");
    JPanel playbackControls = new JPanel();
    JTextField searchField = new JTextField(20);
    JLabel songTitleLabel = new JLabel("Song Title: ");
    int currentSongTotalDuration = 0;
    JSlider seekbarSlider = new JSlider(0, currentSongTotalDuration, 0);
    JLabel currentTimeLabel = new JLabel("00:00");
    JLabel totalTimeLabel = new JLabel(formatTime(currentSongTotalDuration));
    boolean isSliderAdjusting = false;

    // pNode node=new Node();
    MyFrame() {
        playlist.setPlaylistListener(this);
        JFrame frame = new JFrame("Music Playlist Manager");
        // frame.setBackground(Color.DARK_GRAY);
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close on exit
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        // ImageIcon frameImageIcon = new
        // ImageIcon(getClass().getResource("/resources/music.png"));
        ImageIcon frameImageIcon = new ImageIcon("resources/resources/music.png");
        frame.setIconImage(frameImageIcon.getImage());

        // MenuBar Start
        JMenuBar menuBar = new JMenuBar();
        // menuBar.setBackground(Color.DARK_GRAY);

        // Create File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newPlaylist = new JMenuItem("New Playlist");
        JMenuItem loadPlaylist = new JMenuItem("load Playlist");
        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    private Exception backgroundException = null;
                    boolean wasRepeatModeOn = false;
                    int response;

                    @Override
                    protected Void doInBackground() throws Exception {

                        JFileChooser newPlaylistChooser = new JFileChooser(new File(System.getProperty("user.dir")));
                        response = newPlaylistChooser.showOpenDialog(null);
                        if (response == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = newPlaylistChooser.getSelectedFile();
                            String fileName = selectedFile.getAbsolutePath();
                            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
                                Playlist loadedplaylist = (Playlist) in.readObject(); // Read the playlist object from
                                                                                      // the file
                                playlist.stopSongPlayback();
                                playlist = loadedplaylist;
                                playlist.setPlaylistListener(MyFrame.this);
                                // System.out.println("Playlist loaded from " + fileName);
                                // initializeThread();
                                // playlist=loadedplaylist;
                                if (repeat.isSelected()) {
                                    playlist.setRepeatMode(true);
                                    playlist.enableRepeatMode();
                                } else {
                                    playlist.setRepeatMode(false);
                                    playlist.disableRepeatMode();
                                }
                            } catch (IOException | ClassNotFoundException ex) {
                                System.out.println("Error loading playlist: " + ex.getMessage());
                                backgroundException = ex;
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        if (response == 0) {

                            if (backgroundException != null) {
                                JOptionPane.showMessageDialog(null,
                                        "An error occurred while loading the playlist: "
                                                + backgroundException.getMessage(),
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {

                                wasRepeatModeOn = playlist.getRepeatMode();
                                if (wasRepeatModeOn) {
                                    playlist.disableRepeatMode();
                                }
                                int currentIndex = playlistListModel.getSize();
                                while (currentIndex > 0) {
                                    playlistListModel.remove(currentIndex - 1);
                                    currentIndex--;
                                }
                                Playlist.Node head = playlist.getHead();
                                if (head != null) {
                                    Playlist.Node current = head;
                                    playlistListModel.addElement(current.song.getFileName());
                                    while (current.next != null) {
                                        current = current.next;
                                        playlistListModel.addElement(current.song.getFileName());
                                    }
                                }
                                if (wasRepeatModeOn) {
                                    playlist.enableRepeatMode();
                                }
                                JOptionPane.showMessageDialog(frame, "Playlist loaded successfully.");
                            }
                            // JOptionPane.showMessageDialog(frame, "Playlist loaded successfully.");
                        }

                    }
                };
                worker.execute();
            }
        });

        JMenuItem savePlaylist = new JMenuItem("Save Playlist");

        savePlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = JOptionPane.showInputDialog(null, "Enter file name:");
                if (fileName != null && !fileName.trim().isEmpty()) {
                    // Combine the folder path and file name
                    JFileChooser newFolderChooser = new JFileChooser(new File(System.getProperty("user.dir")));
                    newFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int response = newFolderChooser.showSaveDialog(null);
                    if (response == JFileChooser.APPROVE_OPTION) {
                        File selectedFolder = newFolderChooser.getSelectedFile();
                        String foldername = selectedFolder.getAbsolutePath();
                        String fullpath = foldername + "/" + fileName;
                        System.out
                                .println("fileName" + fileName + "\nfolderName" + foldername + "\nfullpath" + fullpath);
                        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fullpath))) {
                            // playlist.setMp3FileNull();
                            // playlist.setPlayThreadNull();
                            oos.writeObject(playlist); // Saving the playlist model
                            JOptionPane.showMessageDialog(null, "Playlist saved successfully to " + fullpath);

                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Error saving playlist: " + ex.getMessage() + "\nStop Playback to save");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Folder not provided. Operation cancelled");
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "File name not provided. Operation cancelled");
                    System.out.println("File name not provided. Operation cancelled.");
                }

            }
        });

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });
        fileMenu.add(newPlaylist);
        fileMenu.add(loadPlaylist);
        fileMenu.add(savePlaylist);
        fileMenu.addSeparator(); // Adds a line separator
        fileMenu.add(exit);

        // Create Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem addSong = new JMenuItem("Add Song");
        JMenuItem removeSong = new JMenuItem("Remove Song");
        JMenuItem renameSong = new JMenuItem("Rename Song");
        JMenuItem reversePlaylist = new JMenuItem("Reverse Playlist");
        reversePlaylist.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                // background task to prevent UI freezing
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    boolean wasRepeatModeOn = false;

                    @Override
                    protected Void doInBackground() throws Exception {
                        // String filteString=((JMenuItem) e.getSource()).getText();
                        playlist.reversePlaylist();
                        wasRepeatModeOn = playlist.getRepeatMode();
                        if (wasRepeatModeOn) {

                            playlist.disableRepeatMode();
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        int currentIndex = playlistListModel.getSize();
                        while (currentIndex > 0) {
                            playlistListModel.remove(currentIndex - 1);
                            currentIndex--;
                        }
                        Playlist.Node head = playlist.getHead();
                        if (head != null) {
                            Playlist.Node current = head;
                            playlistListModel.addElement(current.song.getFileName());
                            while (current.next != null) {
                                current = current.next;
                                playlistListModel.addElement(current.song.getFileName());
                            }

                        }
                        if (wasRepeatModeOn) {

                            playlist.enableRepeatMode();
                        }
                    }
                };
                worker.execute();
            }
        });
        JMenuItem shufflePlaylist = new JMenuItem("Shuffle Playlist");
        shufflePlaylist.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                // background task to prevent UI freezing
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    boolean wasRepeatModeOn = false;

                    @Override
                    protected Void doInBackground() throws Exception {
                        // String filteString=((JMenuItem) e.getSource()).getText();
                        playlist.shufflePlaylist();
                        wasRepeatModeOn = playlist.getRepeatMode();
                        if (wasRepeatModeOn) {

                            playlist.disableRepeatMode();
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        int currentIndex = playlistListModel.getSize();
                        while (currentIndex > 0) {
                            playlistListModel.remove(currentIndex - 1);
                            currentIndex--;
                        }
                        Playlist.Node head = playlist.getHead();
                        if (head != null) {
                            Playlist.Node current = head;
                            playlistListModel.addElement(current.song.getFileName());
                            while (current.next != null) {
                                current = current.next;
                                playlistListModel.addElement(current.song.getFileName());
                            }

                        }
                        if (wasRepeatModeOn) {

                            playlist.enableRepeatMode();
                        }
                    }
                };
                worker.execute();
            }
        });
        JMenu sortPlaylist = new JMenu("Sort Playlist");
        JMenuItem sortPlaylistByName = new JMenuItem("By Name");
        sortPlaylistByName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    boolean wasRepeatModeOn = false;

                    @Override
                    protected Void doInBackground() throws Exception {
                        String filteString = ((JMenuItem) e.getSource()).getText();
                        playlist.sortPlaylist(filteString);
                        wasRepeatModeOn = playlist.getRepeatMode();
                        if (wasRepeatModeOn) {

                            playlist.disableRepeatMode();
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        int currentIndex = playlistListModel.getSize();
                        while (currentIndex > 0) {
                            playlistListModel.remove(currentIndex - 1);
                            currentIndex--;
                        }
                        Playlist.Node head = playlist.getHead();
                        if (head != null) {
                            Playlist.Node current = head;
                            playlistListModel.addElement(current.song.getFileName());
                            while (current.next != null) {
                                current = current.next;
                                playlistListModel.addElement(current.song.getFileName());
                            }

                        }
                        if (wasRepeatModeOn) {

                            playlist.enableRepeatMode();
                        }
                    }
                };
                worker.execute();

            }
        });
        JMenuItem sortPlaylistByArtist = new JMenuItem("By Artist");
        sortPlaylistByArtist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    boolean wasRepeatModeOn = false;

                    @Override
                    protected Void doInBackground() throws Exception {

                        String filteString = ((JMenuItem) e.getSource()).getText();
                        playlist.sortPlaylist(filteString);
                        wasRepeatModeOn = playlist.getRepeatMode();
                        if (wasRepeatModeOn) {

                            playlist.disableRepeatMode();
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        int currentIndex = playlistListModel.getSize();
                        while (currentIndex > 0) {
                            playlistListModel.remove(currentIndex - 1);
                            currentIndex--;
                        }
                        Playlist.Node head = playlist.getHead();
                        if (head != null) {
                            Playlist.Node current = head;
                            playlistListModel.addElement(current.song.getFileName());
                            while (current.next != null) {
                                current = current.next;
                                playlistListModel.addElement(current.song.getFileName());
                            }

                        }
                        if (wasRepeatModeOn) {

                            playlist.enableRepeatMode();
                        }
                    }

                };
                worker.execute();

            }
        });
        JMenuItem sortPlaylistByTime = new JMenuItem("By Time");
        sortPlaylist.add(sortPlaylistByName);
        sortPlaylist.add(sortPlaylistByArtist);
        sortPlaylist.add(sortPlaylistByTime);
        editMenu.add(addSong);
        editMenu.add(removeSong);
        editMenu.add(renameSong);
        editMenu.add(shufflePlaylist);
        editMenu.add(sortPlaylist);
        editMenu.add(reversePlaylist);

        // Create View menu
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem showPlaylist = new JCheckBoxMenuItem("Show Playlist", true);

        showPlaylist.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    sidebarPanel.setVisible(true);
                    playlist.displayPlaylist();
                } else {
                    sidebarPanel.setVisible(false);
                }
            }

        });

        JCheckBoxMenuItem darkMode = new JCheckBoxMenuItem("Dark Mode", false);
        viewMenu.add(showPlaylist);
        viewMenu.add(darkMode);

        // Create Playback menu
        JMenu playbackMenu = new JMenu("Playback");
        JMenuItem play = new JMenuItem("Play");
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = playlistList.getSelectedIndex();
                if (selectedIndex != -1) { // Check if a song is selected
                    if (playlist.getSearchAction()) {
                        // System.out.println("playlistList.getname():"+playlistList.getSelectedValue());
                        playlist.playSongByName(playlistList.getSelectedValue());
                    } else {

                        playlist.playSongByPosition((selectedIndex + 1));
                    }
                    // playlistListModel.remove(selectedIndex); // Remove the song
                    playButton.setVisible(false);
                    pauseButton.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(frame, "No song selected to Play.");
                }
            }
        });
        JMenuItem pause = new JMenuItem("Pause");
        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!playlist.getIsPaused() && playlist.getIsSongPlaybackStarted()) {
                    playlist.pauseSong();
                    pauseButton.setVisible(false);
                    playButton.setVisible(true);
                }
            }
        });
        JMenuItem resume = new JMenuItem("Resume");
        resume.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playlist.getIsPaused() && playlist.getIsSongPlaybackStarted()) {
                    playlist.resumeSong();
                    playButton.setVisible(false);
                    pauseButton.setVisible(true);
                }
            }
        });
        JMenuItem stop = new JMenuItem("Stop");
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // int selectedIndex = playlistList.getSelectedIndex();
                playlist.stopSongPlayback();
            }
        });
        JMenuItem nextSong = new JMenuItem("Next Song");
        nextSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // int selectedIndex = playlistList.getSelectedIndex();
                playlist.nextSong();
            }
        });
        JMenuItem previousSong = new JMenuItem("Previous Song");
        previousSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // int selectedIndex = playlistList.getSelectedIndex();
                playlist.previousSong();
            }
        });
        // JCheckBoxMenuItem repeat = new JCheckBoxMenuItem("Repeat", false);
        // boolean checkManualRepeatTrigger=false;
        repeat.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (checkManualRepeatTrigger) {
                    checkManualRepeatTrigger = false;
                    return;
                } else {

                    boolean enable = false;
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        enable = true;
                    } else {
                        enable = false;
                    }
                    Playlist.Node head = playlist.getHead();
                    boolean repeatMode = playlist.getRepeatMode();
                    if (head == null && enable) {
                        System.out.println("Playlist is empty cannot enable repeat mode");
                        JOptionPane.showMessageDialog(frame, "Playlist is empty cannot enable repeat mode");
                        checkManualRepeatTrigger = true;
                        repeat.setSelected(false);
                        return;
                    } else if (head == null && !enable) {
                        System.out.println("Playlist is empty cannot disable repeat mode");
                        JOptionPane.showMessageDialog(frame, "Playlist is empty cannot disable repeat mode");
                        checkManualRepeatTrigger = true;
                        repeat.setSelected(true);
                        return;
                    }
                    if (enable && !repeatMode) {
                        playlist.enableRepeatMode();
                        System.out.println("Repeat mode enabled.");
                    } else if (!enable && repeatMode) {
                        playlist.disableRepeatMode();
                        System.out.println("Repeat mode disabled.");
                    } else if (enable && repeatMode) {
                        System.out.println("Repeat mode already enabled.");
                        JOptionPane.showMessageDialog(frame, "Repeat mode already enabled");
                    } else if (!enable && !repeatMode) {
                        System.out.println("Repeat mode already disabled.");
                        JOptionPane.showMessageDialog(frame, "Repeat mode already disabled");
                    }

                }

            }
        });
        playbackMenu.add(play);
        playbackMenu.add(pause);
        playbackMenu.add(resume);
        playbackMenu.add(stop);
        playbackMenu.addSeparator();
        playbackMenu.add(nextSong);
        playbackMenu.add(previousSong);
        playbackMenu.addSeparator();
        playbackMenu.add(repeat);

        // Create Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem userGuide = new JMenuItem("User Guide");
        JMenuItem checkForUpdates = new JMenuItem("Check for Updates");
        JMenuItem about = new JMenuItem("About");
        helpMenu.add(userGuide);
        helpMenu.add(checkForUpdates);
        helpMenu.addSeparator();
        helpMenu.add(about);

        // Add menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(playbackMenu);
        menuBar.add(helpMenu);

        // Menubar Ends

        // Main panel Starts
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.RED);
        mainPanel.setPreferredSize(new Dimension(600, 360));
        // Side Panel Starts
        // JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(250, 360));
        // sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBorder(new EmptyBorder(0, 5, 2, 0));
        JPanel searchbarContainer = new JPanel();
        searchbarContainer.setLayout(null);
        searchbarContainer.setPreferredSize(new Dimension(250, 25));
        JLabel playlistText = new JLabel("Playlist");
        playlistText.setBounds(5, 5, 50, 20);
        playlistText.setBackground(new Color(23, 121, 12));
        JButton searchButton = new JButton("search:");
        JButton cancelSearchButton = new JButton("cancel:");
        searchButton.setBounds(76, 3, 55, 20);
        searchButton.setBorder(BorderFactory.createEmptyBorder());
        searchButton.setFocusable(false);
        // searchButton.setBackground(Color.GRAY); // Button background
        // searchButton.setForeground(Color.WHITE);
        cancelSearchButton.setBounds(76, 3, 55, 20);
        cancelSearchButton.setBorder(BorderFactory.createEmptyBorder());
        cancelSearchButton.setFocusable(false);
        cancelSearchButton.setVisible(false);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                searchButton.setVisible(false);
                cancelSearchButton.setVisible(true);
                playlist.setSearchAction(true);
                playlistList.setDragEnabled(false);
                String query = searchField.getText();
                List<SearchResult> matchedSongs = new ArrayList<>();
                matchedSongs = playlist.searchSongBySongName(query);
                int currentIndex = playlistListModel.getSize();
                while (currentIndex > 0) {
                    playlistListModel.remove(currentIndex - 1);
                    currentIndex--;
                }
                int i = 0;
                while (i < matchedSongs.size()) {

                    playlistListModel.addElement(matchedSongs.get(i).getSong().getFileName());
                    i++;
                }

            }
        });
        cancelSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playlist.setSearchAction(false);
                cancelSearchButton.setVisible(false);
                searchButton.setVisible(true);
                playlistList.setDragEnabled(true);
                boolean wasRepeatModeOn = playlist.getRepeatMode();
                if (wasRepeatModeOn) {
                    playlist.disableRepeatMode();
                }
                int currentIndex = playlistListModel.getSize();
                while (currentIndex > 0) {
                    playlistListModel.remove(currentIndex - 1);
                    currentIndex--;
                }
                Playlist.Node head = playlist.getHead();
                if (head != null) {
                    Playlist.Node current = head;
                    playlistListModel.addElement(current.song.getFileName());
                    while (current.next != null) {
                        current = current.next;
                        playlistListModel.addElement(current.song.getFileName());
                    }

                }
                if (wasRepeatModeOn) {

                    playlist.enableRepeatMode();
                }

            }
        });
        // searchButton.setMargin(new Insets(20, 5, 5, 5));
        // JTextField searchField = new JTextField(20);
        searchField.setBounds(135, 5, 110, 20);

        searchbarContainer.add(searchField);
        searchbarContainer.add(playlistText);
        searchbarContainer.add(searchButton);
        searchbarContainer.add(cancelSearchButton);

        sidebarPanel.add(searchbarContainer, BorderLayout.NORTH);

        // Sample playlist data
        // DefaultListModel<String> playlistListModel=new DefaultListModel<>();
        // JList<String> playlistList = new JList<>(playlistListModel);

        sidebarPanel.add(playlistList, BorderLayout.CENTER);
        // sidebarPanel.setVisible(true);
        playlistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // JButton addSongButton =new JButton("Add Song");
        // sidebarPanel.add(addSongButton);
        addSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Simulate adding a song to the playlist
                JFileChooser songChooser = new JFileChooser();
                FileNameExtensionFilter mp3Filter = new FileNameExtensionFilter("MP3 Files", "mp3");
                songChooser.setFileFilter(mp3Filter);
                songChooser.setAcceptAllFileFilterUsed(false);
                int response = songChooser.showOpenDialog(null);
                String newSongFilepath = "";
                String newSongName = "";

                if (response == JFileChooser.APPROVE_OPTION) {
                    File songFile = new File(songChooser.getSelectedFile().getAbsolutePath());
                    newSongFilepath = songFile.getAbsolutePath();
                    newSongName = songFile.getName();

                    Song newSong = new Song("", "", 3.0, newSongFilepath, newSongName);
                    playlist.addSong(newSong);

                    playlistListModel.addElement(newSongName);
                }

                // String newSong = "New Song " + (playlistListModel.getSize() + 1);

            }
        });

        // Delete Button
        // JButton deleteButton = new JButton("Delete Song");
        // Action to delete selected song
        removeSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = playlistList.getSelectedIndex();
                if (selectedIndex != -1) { // Check if a song is selected
                    playlist.removeSongByPosition((selectedIndex + 1));
                    playlistListModel.remove(selectedIndex); // Remove the song
                } else {
                    JOptionPane.showMessageDialog(frame, "No song selected to delete.");
                }
            }
        });
        // Enable drag and drop
        playlistList.setDragEnabled(true);
        playlistList.setDropMode(DropMode.INSERT);
        playlistList.setTransferHandler(new TransferHandler() {
            int fromIndex = -1;

            @Override
            protected Transferable createTransferable(JComponent c) {
                fromIndex = playlistList.getSelectedIndex();
                return new StringSelection(playlistList.getSelectedValue());
            }

            @Override
            public int getSourceActions(JComponent c) {
                return MOVE;
            }

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.stringFlavor);
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }

                JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
                int toIndex = dropLocation.getIndex();

                try {
                    String movedSong = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);

                    if (toIndex == playlistListModel.getSize()) {
                        System.out.println("cannot move song between indexes");
                        return true;
                    } else {
                        playlist.moveSong(fromIndex + 1, toIndex + 1);
                        playlistListModel.remove(fromIndex);
                        playlistListModel.add(toIndex, movedSong);
                        playlistList.setSelectedIndex(toIndex);
                    }

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

        });

        playlistList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                // Call the original renderer to get the component
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                // Set the text with numbering
                label.setText((index + 1) + ". " + value.toString());
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(playlistList);

        sidebarPanel.add(scrollPane);

        // Right Panel

        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setPreferredSize(new Dimension(350, 360));
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel,
                BoxLayout.Y_AXIS));

        // ImageIcon musicImageIcon = new
        // ImageIcon(getClass().getResource("/resources/MusicIcon.png"));
        ImageIcon musicImageIcon = new ImageIcon("resources/resources/MusicIcon.png");
        Image originalMusicImage = musicImageIcon.getImage();
        int musicImageNewHeight = 180;
        // Calculate new width while maintaining aspect ratio
        int originalMusicImageWidth = originalMusicImage.getWidth(null);
        int originalMusicImageHeight = originalMusicImage.getHeight(null);
        int musicImageNewWidth = (musicImageNewHeight * originalMusicImageWidth) / originalMusicImageHeight;
        Image resizedMusicImage = originalMusicImage.getScaledInstance(musicImageNewWidth, musicImageNewHeight,
                Image.SCALE_SMOOTH);
        ImageIcon resizedMusicImageIcon = new ImageIcon(resizedMusicImage);
        JLabel musicIconLabel = new JLabel();
        musicIconLabel.setIcon(resizedMusicImageIcon);
        musicIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        musicIconLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        mainContentPanel.add(musicIconLabel);
        // JLabel songTitleLabel = new JLabel("Song Title: ");
        // JLabel songDurationLabel = new JLabel("Duration: ");
        songTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        songTitleLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        mainContentPanel.add(songTitleLabel);
        // JSlider seekbarSlider= new JSlider(0,currentSongTotalDuration,0);
        seekbarSlider.setPreferredSize(new Dimension(220, 20));
        seekbarSlider.setMaximumSize(new Dimension(230, 100));
        seekbarSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        seekbarSlider.setBorder(new EmptyBorder(10, 0, 5, 0));

        seekbarSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // System.out.println("isSliderAdjusting" + isSliderAdjusting);
                if (seekbarSlider.getValueIsAdjusting()) {
                    isSliderAdjusting = true;
                    // This block runs only when the user is manually adjusting the slider
                    // System.out.println("User is dragging the slider");

                    // Temporarily stop the time thread and update song position
                    if (playlist.getSliderUpdateTimer() != null) {
                        playlist.getSliderUpdateTimer().cancel(); // Stop the timer
                        playlist.setSliderUpdateTimerNull();
                    }
                } else {
                    // When the user has finished dragging the slider
                    // System.out.println("1.User is done the slider");
                    if (isSliderAdjusting) { // Custom flag to track dragging
                        isSliderAdjusting = false;
                        // System.out.println("2.User is done the slider");
                        // Get the new position from the slider
                        int newSeekPositionInSeconds = seekbarSlider.getValue();
                        playlist.setCurrentTime(newSeekPositionInSeconds);
                        playlist.setPausedFrame(newSeekPositionInSeconds * 1000);

                        // Pause and then resume song at the new position
                        if(!playlist.getIsPaused()){

                            playlist.pauseSong();
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                playlist.resumeSong();
                                // System.out.println("Resumed song after delay");
                            }
                        }, 700); // Delay in milliseconds (500ms = 0.5 seconds)
                    }
                    

                    }
                }
            }
        });

        // JPanel seekbarSliderPanel = new JPanel();
        // seekbarSliderPanel.add(seekbarSlider);
        mainContentPanel.add(seekbarSlider);

        // JLabel currentTimeLabel = new JLabel("00:00");
        // JLabel totalTimeLabel = new JLabel(formatTime(currentSongTotalDuration));

        // Create a panel for the time labels (current time on the left, total time on
        // the right)
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BorderLayout());
        timePanel.add(currentTimeLabel, BorderLayout.WEST); // Left side
        timePanel.add(totalTimeLabel, BorderLayout.EAST); // Right side
        timePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timePanel.setMaximumSize(new Dimension(215, 5));
        timePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        mainContentPanel.add(timePanel);
        // mainContentPanel.add(seekbarSliderPanel);
        // seekbarSliderPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // seekbarSliderPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        // mainContentPanel.add(songDurationLabel);
        // Playback controls
        // JPanel playbackControls = new JPanel();
        playbackControls.setLayout(new FlowLayout(FlowLayout.CENTER));
        playbackControls.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener() {
            private long lastClickTime = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                // Debouncing
                long currenTime = System.currentTimeMillis();
                if (currenTime - lastClickTime < 700) {
                    return;
                }
                lastClickTime = currenTime;
                if (playlist.getIsPaused() && playlist.getIsSongPlaybackStarted()) {
                    playlist.resumeSong();
                    playButton.setVisible(false);
                    pauseButton.setVisible(true);
                }
            }

        });
        // JButton pauseButton = new JButton("Pause");
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            private long lastClickTime = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                // Debouncing
                long currenTime = System.currentTimeMillis();
                if (currenTime - lastClickTime < 700) {
                    return;
                }
                lastClickTime = currenTime;
                if (!playlist.getIsPaused() && playlist.getIsSongPlaybackStarted()) {
                    playlist.pauseSong();
                    pauseButton.setVisible(false);
                    playButton.setVisible(true);
                }
            }

        });
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            private long lastClickTime = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                // int selectedIndex = playlistList.getSelectedIndex();
                long currenTime = System.currentTimeMillis();
                if (currenTime - lastClickTime < 1000) {
                    return;
                }
                lastClickTime = currenTime;
                if (playlist.getIsSongPlaybackStarted() && playlist.getCurrentSongNode() != null
                        && playlist.getCurrentSongNode().next != null) {

                    playlist.nextSong();
                    playButton.setVisible(false);
                    pauseButton.setVisible(true);
                }
            }
        });
        JButton previousButton = new JButton("Previous");
        previousButton.addActionListener(new ActionListener() {
            private long lastClickTime = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                // int selectedIndex = playlistList.getSelectedIndex();
                long currenTime = System.currentTimeMillis();
                if (currenTime - lastClickTime < 1000) {
                    return;
                }
                lastClickTime = currenTime;
                if (playlist.getIsSongPlaybackStarted() && playlist.getCurrentSongNode() != null
                        && playlist.getCurrentSongNode().previous != null) {

                    playlist.previousSong();
                    playButton.setVisible(false);
                    pauseButton.setVisible(true);
                }
            }
        });
        playButton.setFocusable(false);
        pauseButton.setFocusable(false);
        nextButton.setFocusable(false);
        previousButton.setFocusable(false);

        playbackControls.add(previousButton);
        playbackControls.add(playButton);
        playbackControls.add(pauseButton);
        playbackControls.add(nextButton);

        mainContentPanel.add(playbackControls);

        // Add the sidebar and main content to the main panel
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(mainContentPanel, BorderLayout.CENTER);

        // Footer

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

        frame.setJMenuBar(menuBar);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(footerSection, BorderLayout.SOUTH);
        // frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void onSongChanged(String songName) {

        SwingUtilities.invokeLater(() -> {
            songTitleLabel.setText(songName); // Safe! Executed on the EDT
        });
    }

    @Override
    public void simulateSeekbar(int currentTime, int songDuration) {
        seekbarSlider.setMaximum(songDuration);
        seekbarSlider.setValue(currentTime);
        currentTimeLabel.setText(formatTime(currentTime));
        totalTimeLabel.setText(formatTime(songDuration));
    }

    @Override
    public void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private String formatTime(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
