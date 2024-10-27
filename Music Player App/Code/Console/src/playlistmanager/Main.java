package src.playlistmanager;

import src.utils.Helper;

import java.util.Scanner;

import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        Playlist playlist = new Playlist();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\n--- Music Playlist Manager ---");
            System.out.println("1. Add Song");
            System.out.println("2. Remove Song");
            System.out.println("3. Display Playlist");
            System.out.println("4. Move Song");
            System.out.println("5. Reverse Playlist");
            System.out.println("6. Search for a song");
            System.out.println("7. Save current playlist");
            System.out.println("8. Load playlist");
            System.out.println("9. Play a Song");
            System.out.println("10. Shuffle Playlist");
            System.out.println("11. Sort Playlist");
            System.out.println("12. Repeat Mode:");
            System.out.println("13. Exit");
            System.out.print("Choose an option: ");
            int choice;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                choice = 14;
            }
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter song title: ");
                    String titleString = scanner.nextLine();
                    String title = Helper.getValidTitle(titleString);
                    System.out.print("Enter artist name: ");
                    String artistString = scanner.nextLine();
                    String artist = Helper.getValidArtist(artistString);
                    System.out.print("Enter duration (minutes): ");
                    String durationString = scanner.nextLine();
                    double duration = Helper.getValidDuration(durationString);
                    System.out.print("Enter FilePath: ");
                    String filePathString = scanner.nextLine();
                    String filePath = Helper.getValidFilePath(filePathString);
                    playlist.addSong(new Song(title, artist, duration, filePath));
                    break;
                case 2:
                    System.out.print("Song to remove: ");
                    System.out.print("\n1. By Title: ");
                    System.out.print("\n2. By Position: ");
                    System.out.print("\nChoose an option: ");
                    int choice1;
                    try {
                        choice1 = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        choice1 = 3;
                    }
                    scanner.nextLine();
                    switch (choice1) {
                        case 1:
                            System.out.print("  Enter Title : ");
                            titleString = scanner.nextLine();
                            if (Helper.checkValidTitle(titleString)) {
                                String removeTitle = titleString;
                                playlist.removeSongByTitle(removeTitle);
                            } else {
                                System.out.println("Invailid Title");
                            }
                            break;
                        case 2:
                            System.out.print("  Enter Position : ");
                            String PositionString = scanner.nextLine();
                            if (Helper.checkValidPosition(PositionString)) {
                                int removePosition = Integer.parseInt(PositionString);
                                playlist.removeSongByPosition(removePosition);
                            } else {
                                System.out.println("Invailid Position");
                            }
                            break;
                        default:
                            System.out.println("Invalid option.");
                            break;
                    }
                    break;
                case 3:
                    playlist.displayPlaylist();
                    break;
                case 4:
                    System.out.print("Enter current song position: ");
                    String oldPositionString = scanner.nextLine();
                    int oldPosition;
                    if (Helper.checkValidPosition(oldPositionString)) {
                        oldPosition = Integer.parseInt(oldPositionString);
                    } else {
                        System.out.println("Invalid Old Position");
                        break;
                    }
                    System.out.print("Enter new position: ");
                    String newPositionString = scanner.nextLine();
                    int newPosition;
                    if (Helper.checkValidPosition(newPositionString)) {
                        newPosition = Integer.parseInt(newPositionString);
                    } else {
                        System.out.println("Invalid New Position");
                        break;
                    }
                    playlist.moveSong(oldPosition, newPosition);
                    break;
                case 5:
                    playlist.reversePlaylist();
                    break;
                case 6:
                    System.out.print("Song to search: ");
                    System.out.print("\n1. By Title: ");
                    System.out.print("\n2. By Artist: ");
                    System.out.print("\nChoose an option: ");
                    int choice2;
                    try {
                        choice2 = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        choice2 = 3;
                    }
                    scanner.nextLine();
                    switch (choice2) {
                        case 1:
                            System.out.print("  Enter Title : ");
                            titleString = scanner.nextLine();
                            if (Helper.checkValidTitle(titleString)) {
                                String searchTitle = titleString;
                                playlist.searchSongByTitle(searchTitle);
                            } else {
                                System.out.println("Invailid Title");
                            }
                            break;
                        case 2:
                            System.out.print("  Enter Artist : ");
                            artistString = scanner.nextLine();
                            if (Helper.checkValidTitle(artistString)) {
                                String searchArtist = artistString;
                                playlist.searchSongByArtist(searchArtist);
                            } else {
                                System.out.println("Invailid Artist");
                            }
                            break;
                        default:
                            System.out.println("Invalid option.");
                            break;
                    }
                    break;
                case 7:
                    System.out.println("Enter Filename: .");
                    String filenameString = scanner.nextLine();
                    if (Helper.checkValidTitle(filenameString)) {
                        String filename = filenameString;
                        playlist.savePlaylist(filename, scanner);
                    } else {
                        System.out.println("Invailid Filename");
                    }
                    break;
                case 8:
                    System.out.println("Enter Filename: .");
                    filenameString = scanner.nextLine();
                    if (Helper.checkValidTitle(filenameString)) {
                        String filename = filenameString;
                        Playlist loadedPlaylist = playlist.loadPlaylist(filename);
                        if (loadedPlaylist != null) {

                            System.out.println(" Do you want to save the current playlist ? (yes/no): ");
                            String response = scanner.nextLine();

                            if (response.equalsIgnoreCase("yes")) {
                                System.out.println("     Enter Filename : .");
                                filenameString = scanner.nextLine();
                                if (Helper.checkValidTitle(filenameString)) {
                                    filename = filenameString;
                                    playlist.savePlaylist(filename, scanner);
                                } else {
                                    System.out.println("Invailid Filename");
                                    System.out.println("Previous Playlist was not saved.");
                                }
                            } else {
                                System.out.println("Previous Playlist was not saved.");
                            }
                            playlist = loadedPlaylist; // Reassign the reference to the loaded playlist
                            System.out.println("New Playlist loaded successfully.");
                        }
                    } else {
                        System.out.println("Invailid Filename");
                    }
                    break;
                case 9:
                    System.out.print("Enter song title to play: ");
                    String playTitleString = scanner.nextLine();
                    if (Helper.checkValidTitle(playTitleString)) {
                        String playTitle = playTitleString;
                        playlist.playSongByTitle(playTitle);
                        boolean flag = true;
                        while (flag) {

                            System.out.println("---Song Menu---");
                            System.out.println("1. Pause");
                            System.out.println("2. Resume");
                            System.out.println("3. Next");
                            System.out.println("4. Previous");
                            System.out.println("5. Stop Song and return to main menu (Exit)");
                            System.out.println("Choose an option");
                            int songMenuChoice;
                            try {
                                songMenuChoice = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                songMenuChoice = 6;
                            }
                            scanner.nextLine();
                            switch (songMenuChoice) {
                                case 1:
                                    playlist.pauseSong();
                                    break;
                                case 2:
                                    playlist.resumeSong();
                                    break;
                                case 3:
                                    playlist.nextSong();
                                    ;
                                    break;
                                case 4:
                                    playlist.previousSong();
                                    break;
                                case 5:
                                    playlist.stopSongPlayback();
                                    flag = false;
                                    break;
                                default:
                                    System.out.println("Invalid option.");
                                    break;
                            }
                        }
                    } else {
                        System.out.println("Invailid Title");
                    }
                    break;
                case 10:
                    playlist.shufflePlaylist();
                    break;
                case 11:
                    System.out.print("Sort Playlist: ");
                    System.out.print("\n1. By Title: ");
                    System.out.print("\n2. By Artist: ");
                    System.out.print("\nChoose an option: ");
                    int sortChoice;
                    try {
                        sortChoice = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        sortChoice = 3;
                    }
                    scanner.nextLine();
                    switch (sortChoice) {
                        case 1:
                            playlist.sortPlaylist(true);
                            break;
                        case 2:
                            playlist.sortPlaylist(false);
                            break;
                        default:
                            System.out.println("Invalid Option");
                            break;
                    }
                    break;
                case 12:
                    System.out.print("Repeat Mode: ");
                    System.out.print("\n1. to enable: ");
                    System.out.print("\n2. to disable: ");
                    System.out.print("\n3. to check status: ");
                    System.out.print("\nChoose an option: ");
                    int repeatChoice;
                    try {
                        repeatChoice = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        repeatChoice = 4;
                    }
                    scanner.nextLine();
                    switch (repeatChoice) {
                        case 1:
                            playlist.toggleRepeatMode(true);
                            break;
                        case 2:
                            playlist.toggleRepeatMode(false);
                            break;
                        case 3:
                            playlist.showRepeatModeStatus();
                            break;
                        default:
                            System.out.println("Invalid Option");
                            break;
                    }
                    break;
                case 13:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }

        scanner.close();
        System.out.println("Goodbye!");
    }

}
