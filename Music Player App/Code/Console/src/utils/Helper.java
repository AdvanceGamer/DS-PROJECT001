package src.utils;

import java.util.Scanner;

public class Helper {
    static Scanner scanner = new Scanner(System.in);

    public static String getValidTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            return title;
        }
        boolean validateInput = false;
        String input = "";
        while (!validateInput) {
            System.out.print("Enter valid title ex- Bekhyali: ");
            input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                validateInput = true;
            }
        }
        return input;
    }

    public static boolean checkValidTitle(String title) {
        return title != null && !title.trim().isEmpty();

    }

    public static String getValidArtist(String artist) {
        if (artist != null && !artist.trim().isEmpty()) {
            return artist;
        }
        boolean validateInput = false;
        String input = "";
        while (!validateInput) {
            System.out.print("Enter valid artist ex- Arijit Singh: ");
            input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                validateInput = true;
            }
        }
        return input;
    }
    public static String getValidFilePath(String FilePath) {
        if (FilePath != null && !FilePath.trim().isEmpty()) {
            return FilePath;
        }
        boolean validateInput = false;
        String input = "";
        while (!validateInput) {
            System.out.print("Filepath cannot be empty:\n Enter valid Filepath : ");
            input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                validateInput = true;
            }
        }
        return input;
    }
    public static double getValidDuration(String duration) {
        // Regular expression to match a number with only one decimal point, allowing
        // multiple digits after the decimal

        String numberPattern = "\\d+(\\.\\d+)?";
        if (duration.matches(numberPattern)) {
            return Double.parseDouble(duration);
        }
        boolean validateInput = false;
        String input = "";
        while (!validateInput) {
            System.out.print("Enter valid duration (minutes) ex- 15.45: ");
            input = scanner.nextLine();
            if (input.matches(numberPattern)) {
                validateInput = true;
            }
        }
        return Double.parseDouble(input);

    }

    public static int getValidPosition(String position) {
        if (position != null && !position.trim().isEmpty()) {
            try {
                int num = Integer.parseInt(position); // Try to parse the string as a long
                // Check if the number is a positive integer and within int range
                if (num > 0 && num <= Integer.MAX_VALUE) {
                    return num;
                }
            } catch (NumberFormatException e) {
                // If parsing fails, it's not a valid integer
            }
        }
        boolean validateInput1 = false;
        String input = "";
        while (!validateInput1) {
            System.out.print("Enter valid position(>=1) : ");
            input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int num = Integer.parseInt(input); // Try to parse the string as a long
                    // Check if the number is a positive integer and within int range
                    if (num > 0 && num <= Integer.MAX_VALUE) {
                        return num;
                    }
                } catch (NumberFormatException e) {
                    continue; // If parsing fails, it's not a valid integer
                }
            }
        }
        return 0;
    }


    public static boolean checkValidPosition(String position) {
        if (position != null && !position.trim().isEmpty()) {
            try {
                int num = Integer.parseInt(position); // Try to parse the string as a long
                // Check if the number is a positive integer and within int range
                if (num > 0 && num <= Integer.MAX_VALUE) {
                    return true;
                }
            } catch (NumberFormatException e) {
                // If parsing fails, it's not a valid integer
            }
        }
        return false;
    }

}
