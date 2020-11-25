package com.sap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static final String ERR_FILE_READ_FAILED = "Couldn't read file";
    public static final String ERR_FILE_WRITE_FAILED = "Couldn't write to file";
    public static final String ERR_INVALID_INTEGER = "Invalid index input. Indexes should be valid integers";
    public static final String ERR_NON_POSITIVE_INDEX = "Invalid index input. Indexes should be positive";
    public static final String ERR_INDEX_OUT_OF_BOUNDS = "Invalid index input. Index is out of bounds";
    public static final String ERR_IDENTICAL_INDEXES = "Indexes are identical";

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the path to the file (example: ./file.txt):");
            String path = scanner.nextLine(); // "./file.txt"; //
            // Read the lines of the file
            List<String> lines = Files.readAllLines(Paths.get(path));
            System.out.println("The file was read successfully");

            int action;
            int firstRowIndex;
            int firstWordIndex;
            int secondRowIndex;
            int secondWordIndex;
            do {
                System.out.println("\nAvailable actions:");
                System.out.println("1. Swap two lines");
                System.out.println("2. Swap two words");
                System.out.println("3. Change file path");
                System.out.println("4. Exit\n");

                System.out.println("Choose an action: ");
                try {
                    action = Integer.parseInt(scanner.nextLine());
                }
                catch (NumberFormatException e) {
                    System.out.println("Invalid action choice. Expecting a number");
                    action = 0;
                }

                switch (action) {
                    case 0:
                        // A special case for when an invalid number was entered
                        break;
                    case 1:
                        try {
                            // Use 1-based indexes for user friendliness
                            System.out.println("First line index:");
                            firstRowIndex = Integer.parseInt(scanner.nextLine()) - 1;

                            System.out.println("Second line index:");
                            secondRowIndex = Integer.parseInt(scanner.nextLine()) - 1;
                        } catch (NumberFormatException e) {
                            System.out.println(ERR_INVALID_INTEGER);
                            break;
                        }
                        swapLines(firstRowIndex, secondRowIndex, path, lines);
                        break;
                    case 2:
                        try {
                            // Use 1-based indexes for user friendliness
                            System.out.println("First line index:");
                            firstRowIndex = Integer.parseInt(scanner.nextLine()) - 1;
                            System.out.println("First word index:");
                            firstWordIndex = Integer.parseInt(scanner.nextLine()) - 1;

                            System.out.println("Second line index:");
                            secondRowIndex = Integer.parseInt(scanner.nextLine()) - 1;
                            System.out.println("Second word index:");
                            secondWordIndex = Integer.parseInt(scanner.nextLine()) - 1;
                        } catch (NumberFormatException e) {
                            System.out.println(ERR_INVALID_INTEGER);
                            break;
                        }
                        swapWords(firstRowIndex, firstWordIndex, secondRowIndex, secondWordIndex, path, lines);
                        break;
                    case 3:
                        System.out.println("Enter the path to the file (example: ./file.txt):");
                        path = scanner.nextLine();
                        lines = Files.readAllLines(Paths.get(path));
                        System.out.println("The new file was read successfully");
                        break;
                    case 4:
                        System.out.println("Bye");
                        break;
                    default:
                        System.out.println("Invalid action");

                }
            } while (action != 4);

        } catch (IOException e) {
            System.out.println(ERR_FILE_READ_FAILED);
        }
    }

    public static void swapWords(int firstRowIndex, int firstWordIndex, int secondRowIndex, int secondWordIndex, String path, List<String> lines) {
        // Validate input
        if (firstRowIndex < 0 || firstWordIndex < 0 || secondRowIndex < 0 || secondWordIndex < 0) {
            System.out.println(ERR_NON_POSITIVE_INDEX);
            return;
        }

        // Check if the indexes are out of bounds
        if (firstRowIndex > lines.size() || secondRowIndex > lines.size()) {
            System.out.println(ERR_INDEX_OUT_OF_BOUNDS);
            return;
        }

        // We want to preserve the tabs or spaces between the words so we split using the following positive lookahead
        // Matches an empty match if followed by spaces or tabs: "(?=[ \\t]+)"
        // This way nothing is removed from the original string after the split
        String splitRegex = "(?=[ \t]+)";

        // Check if we're swapping words on different lines or on the same one
        if (firstRowIndex != secondRowIndex) {
            // Split the lines
            String[] firstRow = lines.get(firstRowIndex).split(splitRegex);
            String[] secondRow = lines.get(secondRowIndex).split(splitRegex);

            // Check if the indexes are out of bounds
            if (firstWordIndex > firstRow.length || secondWordIndex > secondRow.length) {
                System.out.println(ERR_INDEX_OUT_OF_BOUNDS);
                return;
            }

            // Extract the words without their prefixing whitespace
            String firstWord = firstRow[firstWordIndex].replaceAll("\\s+", "");
            String secondWord = secondRow[secondWordIndex].replaceAll("\\s+", "");

            // Swap the words while maintaining the corresponding whitespace
            secondRow[secondWordIndex] = secondRow[secondWordIndex].replaceAll("\\S+", firstWord);
            firstRow[firstWordIndex] = firstRow[firstWordIndex].replaceAll("\\S+", secondWord);

            // Construct the new lines
            lines.set(firstRowIndex, String.join("", firstRow));
            lines.set(secondRowIndex, String.join("", secondRow));
        } else {
            // Split the line
            String[] row = lines.get(firstRowIndex).split(splitRegex);

            // Check if the indexes are out of bounds
            if (firstWordIndex > row.length || secondWordIndex > row.length) {
                System.out.println(ERR_INDEX_OUT_OF_BOUNDS);
                return;
            }

            // If the two word indexes are the same we don't have to do anything
            if (firstWordIndex == secondWordIndex) {
                System.out.println(ERR_IDENTICAL_INDEXES);
                return;
            }

            // Extract the words without their prefixing whitespace
            String firstWord = row[firstWordIndex].replaceAll("\\s+", "");
            String secondWord = row[secondWordIndex].replaceAll("\\s+", "");

            // Swap the words while maintaining the corresponding whitespace
            row[secondWordIndex] = row[secondWordIndex].replaceAll("\\S+", firstWord);
            row[firstWordIndex] = row[firstWordIndex].replaceAll("\\S+", secondWord);

            // Construct the new line
            lines.set(firstRowIndex, String.join("", row));
        }

        try {
            // Write the changes to the file
            Files.writeString(Paths.get(path), String.join(System.lineSeparator(), lines), StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("The changes were written to the file successfully");
        } catch (IOException e) {
            System.out.println(ERR_FILE_WRITE_FAILED);
        }
    }

    public static void swapLines(int firstRowIndex, int secondRowIndex, String path, List<String> lines) {
        // Validate input
        if (firstRowIndex < 0 || secondRowIndex < 0) {
            System.out.println(ERR_NON_POSITIVE_INDEX);
            return;
        }

        // Check if the indexes are out of bounds
        if (firstRowIndex > lines.size() || secondRowIndex > lines.size()) {
            System.out.println(ERR_INDEX_OUT_OF_BOUNDS);
            return;
        }

        // If the two line numbers are the same we don't have to do anything
        if (firstRowIndex == secondRowIndex) {
            System.out.println(ERR_IDENTICAL_INDEXES);
            return;
        }

        // Get the lines
        String firstRow = lines.get(firstRowIndex);
        String secondRow = lines.get(secondRowIndex);

        // Swap the lines
        lines.set(firstRowIndex, secondRow);
        lines.set(secondRowIndex, firstRow);

        try {
            // Write the changes to the file
            // Files.write(Paths.get(path), lines, StandardOpenOption.TRUNCATE_EXISTING);
            Files.writeString(Paths.get(path), String.join(System.lineSeparator(), lines), StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("The changes were written to the file successfully");
        } catch (IOException e) {
            System.out.println(ERR_FILE_WRITE_FAILED);
        }
    }
}
