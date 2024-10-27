package src.playlistmanager;

import java.io.Serializable;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
// import java.io.File;

public class Song implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String artist;
    private double duration; // Duration in minutes
    private String filePath;

    public Song(String title, String artist, double duration, String filePath) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.filePath = filePath;

    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public double getDuration() {
        return duration;
    }

    public String getFilePath() {
        return filePath;
    }

    // public static void checkFolderPermissions(String folderPath) {
    // // File folder = new File(folderPath);
    // // String localfolderPath = "D:\New folder (13)\DSA Project M2\Bol.mp3"; //
    // or without a trailing backslash
    // String[] pathParts = folderPath.split("\\\\");

    // // String[] pathParts = folderPath.split("\\\\"); // Split by backslash
    // for (int i = 0; i < pathParts.length; i++) {

    // System.out.print(pathParts[i] + " ");
    // }
    // // Build the path incrementally and check each folder
    // String currentPath = "";
    // for (String part : pathParts) {
    // currentPath = currentPath.isEmpty() ? part : currentPath + "\\" + part;
    // System.err.println("currentPath"+currentPath);
    // File currentFolder = new File(currentPath);
    // // System.out.println("absolute path :"+currentFolder.getAbsolutePath());
    // // Check if the current folder is readable
    // if (currentFolder.exists()) {
    // if (currentFolder.canRead()) {
    // System.out.println("Access granted to: " + currentFolder.getAbsolutePath());
    // } else {
    // System.out.println("Access denied to: " + currentFolder.getAbsolutePath());
    // }
    // } else {
    // System.out.println("Folder not found: " + currentFolder.getAbsolutePath());
    // }
    // }
    // }

    public void play() {
        new Thread(() -> {
            try {

                // Create a File object for the file path
                // File file = new File(filePath);

                // Split the file path into folders and check access for each
                // checkFolderPermissions(filePath);
                FileInputStream fileInputStream = new FileInputStream(filePath);

                // System.out.println(fileInputStream);
                Player player = new Player(fileInputStream);
                System.out.println("Playing: " + title + " by " + artist);
                player.play();
                // After playback ends
                System.out.println("Playback finished for: " + title);
            } catch (FileNotFoundException | JavaLayerException e) {
                System.out.println("Error playing the song: " + e.getMessage());
                // e.printStackTrace();
            }
        }).start();

    }

    // Optional method to calculate duration
    // private double calculateDuration() {
    // // You can use a library like MP3agic to read MP3 file metadata and get the
    // duration
    // // This is an advanced step, so it's optional
    // return 0; // Placeholder, replace with real duration calculation if needed
    // }
    @Override
    public String toString() {
        return "  Title: " + title + ",  Artist: " + artist + ",  Duration: " + duration + " mins" + ", FIlepath: "
                + filePath;
    }
}
