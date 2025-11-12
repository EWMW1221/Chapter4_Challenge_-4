package chapter4_challenge_diarymanager;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DiaryManager {

    private static final String ENTRIES_DIR = "entries";
    private static final String BACKUP_FILE = "diary_backup.zip";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        createEntriesDir();

        while (true) {
            System.out.println("\n=== Diary Manager ===");
            System.out.println("1. Write Entry");
            System.out.println("2. Read Entry");
            System.out.println("3. Search Entries");
            System.out.println("4. Backup Entries");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    writeEntry();
                    break;
                case "2":
                    readEntry();
                    break;
                case "3":
                    searchEntries();
                    break;
                case "4":
                    backupEntries();
                    break;
                case "5":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Create entries directory if it does not exist
    private static void createEntriesDir() {
        Path path = Paths.get(ENTRIES_DIR);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                System.out.println("Error creating entries directory: " + e.getMessage());
            }
        }
    }

    // Write a new diary entry
    private static void writeEntry() {
        System.out.println("\n--- Write New Entry ---");
        System.out.println("Type your entry (empty line to finish):");

        StringBuilder content = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).isEmpty()) {
            content.append(line).append(System.lineSeparator());
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
        String filename = ENTRIES_DIR + "/diary_" + timestamp + ".txt";

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            writer.write(content.toString());
            System.out.println("Entry saved as " + filename);
        } catch (IOException e) {
            System.out.println("Error writing entry: " + e.getMessage());
        }
    }

    // List all entries
    private static File[] listEntryFiles() {
        File folder = new File(ENTRIES_DIR);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) {
            files = new File[0];
        }
        return files;
    }

    // Read an existing diary entry
    private static void readEntry() {
        File[] files = listEntryFiles();
        if (files.length == 0) {
            System.out.println("No diary entries found.");
            return;
        }

        System.out.println("\n--- Read Entry ---");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        System.out.print("Select entry number: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > files.length) {
                System.out.println("Invalid selection.");
                return;
            }

            File fileToRead = files[choice - 1];
            System.out.println("\n--- Entry Content ---");
            try (BufferedReader reader = Files.newBufferedReader(fileToRead.toPath())) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (NumberFormatException | IOException e) {
            System.out.println("Error reading entry: " + e.getMessage());
        }
    }

    // Search diary entries for a keyword
    private static void searchEntries() {
        File[] files = listEntryFiles();
        if (files.length == 0) {
            System.out.println("No diary entries found.");
            return;
        }

        System.out.print("\nEnter keyword to search: ");
        String keyword = scanner.nextLine().toLowerCase();
        boolean found = false;

        System.out.println("\n--- Search Results ---");
        for (File file : files) {
            try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().contains(keyword)) {
                        System.out.println(file.getName());
                        found = true;
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading file " + file.getName());
            }
        }

        if (!found) {
            System.out.println("No entries found containing \"" + keyword + "\".");
        }
    }

    // Backup all diary entries into a ZIP file
    private static void backupEntries() {
        File[] files = listEntryFiles();
        if (files.length == 0) {
            System.out.println("No entries to backup.");
            return;
        }

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(BACKUP_FILE)))) {
            for (File file : files) {
                ZipEntry entry = new ZipEntry(file.getName());
                zos.putNextEntry(entry);
                Files.copy(file.toPath(), zos);
                zos.closeEntry();
            }
            System.out.println("Backup completed: " + BACKUP_FILE);
        } catch (IOException e) {
            System.out.println("Error creating backup: " + e.getMessage());
        }
    }
}
