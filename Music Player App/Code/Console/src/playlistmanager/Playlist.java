package src.playlistmanager;

import java.util.Random;
import java.util.Scanner;

import com.mpatric.mp3agic.Mp3File;

// import javazoom.jl.decoder.Bitstream;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
// import java.util.ArrayList;
// import src.utils.Helper

public class Playlist implements Serializable {
    private static final long serialVersionUID = 1L;

    private class Node implements Serializable {
        Song song;
        Node next;
        Node previous; // New reference for doubly linked list

        Node(Song song) {
            this.song = song;
            this.next = null;
            this.previous = null; // Initialize as null
        }
    }

    private Node head;
    private int playlistSize;
    private boolean repeatMode = false;

    private boolean isPaused = false; // To track pause state
    private boolean isSongPlaybackStopped = false; // To track song Playback state
    private boolean isSongPlaybackStarted = false; // To track song Playback state
    private Thread playThread; // Separate thread for playing
    private Song currentSong; // Current song being played
    private Node currentSongNode; // Current song Node in the playlist
    private int pausedFrame = 0; // Frame where the song was paused
    private AdvancedPlayer player; // Player to handle playback
    private Mp3File mp3File;
    private double frameRatePerMiliseconds = 1.0;

    public Playlist() {
        head = null;
        playlistSize = 0;
    }

    // Add a song to the end of the playlist
    public void addSong(Song song) {
        boolean wasRepeatModeOn = repeatMode;
        if (wasRepeatModeOn) {
            disableRepeatMode();
        }

        Node newNode = new Node(song);
        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newNode;
            newNode.previous = temp; // Set previous pointer for doubly linked list
        }
        System.out.println("Song added: " + song.getTitle());
        playlistSize++;

        if (wasRepeatModeOn) {
            enableRepeatMode();
        }

    }

    // Remove a song by title
    public void removeSongByTitle(String title) {

        if (head == null) {
            System.out.println("The playlist is empty.");
            return;
        }

        boolean wasRepeatModeOn = repeatMode;
        if (wasRepeatModeOn) {
            disableRepeatMode();
        }

        if (head.song.getTitle().equalsIgnoreCase(title)) {
            head = head.next;
            if (head != null) {
                head.previous = null; // Update previous pointer if head is changed
            }
            System.out.println("Song removed: " + title);
            playlistSize--;
            if (wasRepeatModeOn) {
                enableRepeatMode();
            }
            return;
        }

        Node current = head;
        while (current != null && !current.song.getTitle().equalsIgnoreCase(title)) {
            current = current.next;
        }

        if (current == null) {
            System.out.println("Song not found: " + title);
        } else {
            if (current.previous != null) {
                current.previous.next = current.next; // Update next of previous node
            }
            if (current.next != null) {
                current.next.previous = current.previous; // Update previous of next node
            }
            System.out.println("Song removed: " + title);
            playlistSize--;
        }
        if (wasRepeatModeOn) {
            enableRepeatMode();
        }

    }

    public void removeSongByPosition(int position) {

        if (head == null) {
            System.out.println("The playlist is empty.");
            return;
        }
        boolean wasRepeatModeOn = repeatMode;
        if (wasRepeatModeOn) {
            disableRepeatMode();
        }
        if (position == 1) {
            head = head.next;
            if (head != null) {
                head.previous = null; // Update previous pointer
            }
            System.out.println("Song removed from position 1.");
            playlistSize--;
            if (wasRepeatModeOn) {
                enableRepeatMode();
            }
            return;
        }

        Node current = head;
        int currentPosition = 1;
        while (current != null && currentPosition < position) {
            current = current.next;
            currentPosition++;
        }

        if (current == null) {
            System.out.println("Position " + position + " is out of bounds.");
        } else {
            if (current.previous != null) {
                current.previous.next = current.next;
            }
            if (current.next != null) {
                current.next.previous = current.previous;
            }
            System.out.println("Song removed from position " + position + ".");
            playlistSize--;
        }
        if (wasRepeatModeOn) {
            enableRepeatMode();
        }
    }

    // Display all songs in the playlist
    public void displayPlaylist() {
        if (head == null) {
            System.out.println("The playlist is empty.");
            return;
        }

        boolean wasRepeatModeOn = repeatMode;
        if (wasRepeatModeOn) {
            disableRepeatMode();
        }
        Node current = head;
        int index = 1;
        double totalDuration = 0;

        while (current != null) {
            System.out.println(index + ". " + current.song);
            totalDuration += current.song.getDuration();
            current = current.next;
            index++;
        }

        System.out.println("Total playlist duration: " + totalDuration + " mins");
        if (wasRepeatModeOn) {
            System.out.println("Repeat Mode is ON:");
        } else {
            System.out.println("Repeat Mode is OFF:");
        }
        if (wasRepeatModeOn) {
            enableRepeatMode();
        }
    }

    // Reorder songs by moving one song to a new position
    public void moveSong(int oldPosition, int newPosition) {
        if (head == null) {
            System.out.println("The playlist is empty.");
            return;
        }

        if (oldPosition == newPosition) {
            System.out.println("The positions are the same.");
            return;
        }
        if (oldPosition > playlistSize) {
            System.out.println("No song found on " + oldPosition + " position");
            return;
        }
        if (newPosition > playlistSize) {
            System.out.println("Invalid new position");
            return;
        }
        boolean wasRepeatModeOn = repeatMode;
        if (wasRepeatModeOn) {
            disableRepeatMode();
        }
        // Step 1: Find the node at oldPosition
        Node currentOld = head;
        for (int i = 1; i < oldPosition && currentOld != null; i++) {
            currentOld = currentOld.next;
        }

        // Step 2: Remove currentOld from its old position
        if (currentOld.previous != null) {
            currentOld.previous.next = currentOld.next;
        } else { // If currentOld is head
            head = currentOld.next;
        }

        if (currentOld.next != null) {
            currentOld.next.previous = currentOld.previous;
        }

        // Step 3: Find the node at newPosition
        Node currentNew = head;
        for (int i = 1; i < newPosition && currentNew != null; i++) {
            currentNew = currentNew.next;
        }

        // Step 4: Insert currentOld at the new position
        if (newPosition == 1) {
            currentOld.next = head;
            if (head != null) {
                head.previous = currentOld;
            }
            head = currentOld;
            currentOld.previous = null;
        } else if (currentNew == null) { // Moving to the last position
            Node tail = head;
            while (tail.next != null) {
                tail = tail.next;
            }
            tail.next = currentOld;
            currentOld.previous = tail;
            currentOld.next = null;
        } else { // Inserting in the middle of the playlist
            currentOld.next = currentNew;
            currentOld.previous = currentNew.previous;
            if (currentNew.previous != null) {
                currentNew.previous.next = currentOld;
            }
            currentNew.previous = currentOld;
        }
        System.out.println("Moved song from position " + oldPosition + " to " + newPosition);
        if (wasRepeatModeOn) {
            enableRepeatMode();
        }
    }

    public void reversePlaylist() {
        if (head == null) {
            System.out.println("The playlist is empty.");
            return;
        }
        if (head.next == null) {
            System.out.println("The playlist has only one song.");
            return;
        }
        boolean wasRepeatModeOn = repeatMode;
        if (wasRepeatModeOn) {
            disableRepeatMode();
        }
        Node current = head;
        Node temp = null;

        // Swap the previous and next pointers for each node to reverse the list
        while (current != null) {
            temp = current.previous;
            current.previous = current.next;
            current.next = temp;
            current = current.previous; // move to the next node (which is actually the previous one now)
        }

        // After reversing, set the head to the last node
        if (temp != null) {
            head = temp.previous;
        }

        System.out.println("The playlist has been reversed.");
        if (wasRepeatModeOn) {
            enableRepeatMode();
        }
    }

    public SearchResult searchSongByTitle(String title) {
        Node current = head;
        int position = 1; // To track the position of the song
        boolean wasRepeatModeOn = repeatMode;
        if (wasRepeatModeOn) {
            disableRepeatMode();
        }
        while (current != null) {
            if (current.song.getTitle().equalsIgnoreCase(title)) {
                SearchResult result = new SearchResult(current.song, position);
                System.out.println("Found song: " + result);
                if (wasRepeatModeOn) {
                    enableRepeatMode();
                }
                return result; // Return the found song and its position
            }
            current = current.next;
            position++;
        }
        System.out.println("Song not found: " + title);
        if (wasRepeatModeOn) {
            enableRepeatMode();
        }
        return null; // Return null if the song is not found
    }

    // Search for a song by artist
    public SearchResult searchSongByArtist(String artist) {
        Node current = head;
        int position = 1; // To track the position of the song
        boolean wasRepeatModeOn = repeatMode;
        if (wasRepeatModeOn) {
            disableRepeatMode();
        }
        while (current != null) {
            if (current.song.getArtist().equalsIgnoreCase(artist)) {
                SearchResult result = new SearchResult(current.song, position);
                System.out.println("Found song: " + result);
                if (wasRepeatModeOn) {
                    enableRepeatMode();
                }
                return result; // Return the found song and its position
            }
            current = current.next;
            position++;
        }
        System.out.println("Artist not found: " + artist);
        if (wasRepeatModeOn) {
            enableRepeatMode();
        }
        return null; // Return null if no song is found for the artist
    }

    // Save playlist to file

    public void savePlaylist(String filename, Scanner scanner) {
        File file = new File(filename);

        // Check if file already exists
        if (file.exists()) {
            System.out.println("File '" + filename + "' already exists. Do you want to overwrite it? (yes/no): ");
            String response = scanner.nextLine();

            if (!response.equalsIgnoreCase("yes")) {
                System.out.println("Playlist was not saved.");
                return;
            }

        }

        // Save the playlist if the file does not exist or user agrees to overwrite
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
            System.out.println("Playlist has been saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving playlist: " + e.getMessage());
        }

    }

    // Load playlist from file
    public Playlist loadPlaylist(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            Playlist loadedplaylist = (Playlist) in.readObject(); // Read the playlist object from the file
            // System.out.println("Playlist loaded from " + fileName);
            // initializeThread();
            return loadedplaylist;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading playlist: " + e.getMessage());
            return null;
        }
    }

    // Advanced Features Shuffle , Repeat Mode, Sort Playlist.
    // Shuffle Playlist Start
    public void shufflePlaylist() {

        if (playlistSize <= 1) {
            System.out.println("Playlist is Empty/has only one song");
            return;
        }
        boolean wasRepeatModeOn = repeatMode;
        if (wasRepeatModeOn) {
            disableRepeatMode();
        }
        Node[] nodeArray = convertPlaylistToArray();
        Random random = new Random();
        // Fisher-Yates shuffle algorithm
        for (int i = playlistSize - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Node temp = nodeArray[i];
            nodeArray[i] = nodeArray[j];
            nodeArray[j] = temp;
        }

        convertArrayToPlaylist(nodeArray);
        System.out.println("Playlist shuffled!");
        if (wasRepeatModeOn) {
            enableRepeatMode();
        }

    }

    public Node[] convertPlaylistToArray() {

        Node[] nodeArray = new Node[playlistSize];
        Node current = head;
        int index = 0;
        while (current != null) {
            nodeArray[index] = current;
            current = current.next;
            index++;
        }
        return nodeArray;
    }

    public void convertArrayToPlaylist(Node[] shuffledArray) {
        if (shuffledArray.length == 0)
            return;

        head = shuffledArray[0];
        head.previous = null;
        Node current = head;

        for (int i = 1; i < shuffledArray.length; i++) {
            current.next = shuffledArray[i];
            shuffledArray[i].previous = current;
            current = current.next;
        }

        current.next = null;
    }
    // Shuffle Playlist End

    // Sort Playlist Start

    public void sortPlaylist(boolean byTitle) {
        if (head == null || head.next == null) {
            System.out.println("Playlist is empty/has only one song.");
            return;
        }
        boolean wasRepeatModeOn = repeatMode;
        if (wasRepeatModeOn) {
            disableRepeatMode();
        }
        head = mergeSort(head, byTitle);
        System.out.println("Playlist sorted lexographically.");
        if (wasRepeatModeOn) {
            enableRepeatMode();
        }
    }

    // Merge Sort function
    private Node mergeSort(Node node, boolean byTitle) {
        if (node == null || node.next == null) {
            return node;
        }

        Node middle = getMiddle(node);
        Node nextOfMiddle = middle.next;

        middle.next = null;

        Node left = mergeSort(node, byTitle);
        Node right = mergeSort(nextOfMiddle, byTitle);

        return merge(left, right, byTitle);
    }

    // Function to merge two sorted linked lists
    private Node merge(Node left, Node right, boolean byTitle) {
        if (left == null)
            return right;
        if (right == null)
            return left;

        if (compareSongs(left.song, right.song, byTitle) <= 0) {
            left.next = merge(left.next, right, byTitle);
            left.next.previous = left;
            left.previous = null;
            return left;
        } else {
            right.next = merge(left, right.next, byTitle);
            right.next.previous = right;
            right.previous = null;
            return right;
        }
    }

    // Helper function to compare songs based on title or artist
    private int compareSongs(Song s1, Song s2, boolean byTitle) {
        if (byTitle) {
            return s1.getTitle().compareToIgnoreCase(s2.getTitle());
        } else {
            return s1.getArtist().compareToIgnoreCase(s2.getArtist());
        }
    }

    // Function to get the middle of the linked list
    private Node getMiddle(Node node) {
        if (node == null)
            return node;

        Node slow = node, fast = node;

        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    // Sort Playlist End

    // Repeate Mode start

    public boolean isRepeatModeOn() {
        return repeatMode;
    }

    public void toggleRepeatMode(boolean enable) {
        if (head == null && enable) {
            System.out.println("Playlist is empty cannot enable repeat mode");
            return;
        } else if (head == null && !enable) {
            System.out.println("Playlist is empty cannot disable repeat mode");
            return;
        }
        if (enable && !repeatMode) {
            enableRepeatMode();
            System.out.println("Repeat mode enabled.");
        } else if (!enable && repeatMode) {
            disableRepeatMode();
            System.out.println("Repeat mode disabled.");
        } else if (enable && repeatMode) {
            System.out.println("Repeat mode already enabled.");
        } else if (!enable && !repeatMode) {
            System.out.println("Repeat mode already disabled.");
        }
    }

    public void enableRepeatMode() {
        if (head == null) {
            // No repeat mode for an empty playlist.
            return;
        }

        if (head.next == null) {
            // Single song case: Make it a self-loop.
            head.next = head;
            head.previous = head;
            // System.out.println("Repeat mode enabled.");
        } else {
            // More than one song: Link the last node to the head.
            Node last = head;
            while (last.next != null) {
                last = last.next;
            }

            // Connect last node's next to head, and head's previous to last node
            last.next = head;
            head.previous = last;
            // System.out.println("Repeat mode enabled.");
        }
        repeatMode = true;

    }

    public void disableRepeatMode() {
        if (head == null) {
            return;
        }

        if (head.next == head) {
            // Single song case: Break the self-loop.
            head.next = null;
            head.previous = null;
            // System.out.println("Repeat mode disabled.");
        } else {
            // More than one song: Find the last node and break the loop.
            Node last = head;
            while (last.next != head) {
                last = last.next;
            }

            // Break the loop
            last.next = null;
            head.previous = null;

            // System.out.println("Repeat mode disabled.");
        }
        repeatMode = false;
    }

    public void showRepeatModeStatus() {
        if (repeatMode) {
            System.out.println("Repeat Mode is ON");
        } else {
            System.out.println("Repeat Mode is OFF");
        }
    }

    // Repeate Mode end

    // Method to play a song by title
    public void playSongByTitle(String title) {
        Node current = head;
        boolean wasRepeatModeOn = repeatMode;
        if (wasRepeatModeOn) {
            disableRepeatMode();
        }
        while (current != null) {
            if (current.song.getTitle().equalsIgnoreCase(title)) {
                currentSongNode = current;
                currentSong = currentSongNode.song;
                initializeThread();
                if (wasRepeatModeOn) {
                    enableRepeatMode();
                }
                return;
            }
            current = current.next;
        }

        System.out.println("Song not found : " + title + "in the playlist");
        if (wasRepeatModeOn) {
            enableRepeatMode();
        }
    }

    // Thread Initialization

    public void initializeThread() {
        playThread = new Thread(() -> {
            while (true) {
                synchronized (this) { // Synchronize on the PlaylistManager instance
                    try {
                        if (isSongPlaybackStarted) {
                            wait();
                        } else {
                            isSongPlaybackStarted = true;
                            // Wait until notified
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    if (isSongPlaybackStopped) {
                        isSongPlaybackStopped = false;
                        break; // Exit the thread if stopped
                    }
                }
                if (!isPaused && currentSong != null) {

                    playSong(currentSong); // Play the current song
                }

            }
            isSongPlaybackStarted = false;
        });

        playThread.start();
    }

    // Play, Pause , Resume Start

    public void playSong(Song song) {

        try {
            FileInputStream fileInputStream = new FileInputStream(song.getFilePath());
            // Bitstream bitstream = new Bitstream(fileInputStream);
            mp3File = new Mp3File(song.getFilePath());
            frameRatePerMiliseconds = (double) (mp3File.getFrameCount()) / (double) (mp3File.getLengthInMilliseconds());
            player = new AdvancedPlayer(fileInputStream);

            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    // evt.getFrame() gives the last frame played when playback stopped
                    pausedFrame += evt.getFrame(); // Save the frame for resuming
                    System.out.println("Playback stopped at frame: " + pausedFrame);
                }
            });

            // Start playing the song from the paused frame onward

            player.play((int) (pausedFrame * frameRatePerMiliseconds), Integer.MAX_VALUE); // Play until the end

            System.out.println("Playing: " + song.getTitle());

            // Clean up resources after the song finishes playing
            // fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void pauseSong() {
        isPaused = true; // Set pause flag to true
        if (player != null) {
            player.stop(); // Stop the player immediately
        }
        System.out.println("Playback paused.");
        System.out.println("Playback stopped at frame(milisecond): " + pausedFrame);
        System.out.println("Playback stopped at frame: " + (int) (pausedFrame * frameRatePerMiliseconds));
    }

    public synchronized void resumeSong() {
        isPaused = false; // Set pause flag to false
        // playSong(currentSong); // Resume playback from where it was paused
        notify();
        System.out.println("Playback resumed.");
        System.out.println("Playback resumed from frame: " + pausedFrame);
        System.out.println("Playback resumed at frame: " + (int) (pausedFrame * frameRatePerMiliseconds));
    }

    public synchronized void nextSong() {
        if (currentSongNode.next != null) {
            if (player != null) {
                player.stop(); // Stop the player immediately
            }
            pausedFrame = 0;
            frameRatePerMiliseconds = 1.0;
            currentSongNode = currentSongNode.next;
            currentSong = currentSongNode.song;
            // playSong(currentSongNode.song);
            try {
                wait(100); // Wait for 100 milliseconds to give the thread time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            notify();
        }
    }

    public synchronized void previousSong() {
        if (currentSongNode.previous != null) {
            if (player != null) {
                player.stop(); // Stop the player immediately
            }
            pausedFrame = 0;
            frameRatePerMiliseconds = 1.0;
            currentSongNode = currentSongNode.previous;
            currentSong = currentSongNode.song;
            // playSong(currentSongNode.song);
            try {
                wait(100); // Wait for 100 milliseconds to give the thread time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            notify();
        }
    }

    public void stopSongPlayback() {
        if (player != null) {
            player.stop();
            pausedFrame = 0;
            frameRatePerMiliseconds = 1.0;
            isSongPlaybackStopped = true;
            // currentSongNode = currentSongNode.previous;
            // playSong(currentSongNode.song);
            notify();
        }
    }

    // Play, Pause, Resume Stop

}
