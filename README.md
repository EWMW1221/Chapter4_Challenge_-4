# Chapter4_Challenge_DiaryManager

## Project Description
DiaryManager is a command-line diary application built in Java. It allows users to:

- Write daily diary entries.
- Read previous entries.
- Search for entries containing specific keywords.
- Backup all entries into a ZIP archive (optional feature).

Each diary entry is saved as a separate text file with a timestamp in its filename to help organize and retrieve past entries easily.

---

## Features

### Core Features
1. **Write Mode**
   - Prompts the user to write a diary entry.
   - Saves the entry as a text file with the timestamp format: `diary_YYYY_MM_DD_HH_MM_SS.txt`.

2. **Read Mode**
   - Displays a list of all diary entries.
   - Allows the user to select an entry to read.

3. **Search Functionality**
   - Allows searching through diary entries for specific keywords.

### Advanced Features (Bonus)
1. **Backup**
   - Creates a ZIP archive of all diary entries.
2. **Object Serialization**
   - Saves the application state using serialization for future enhancements.

---

## Project Folder Structure


---

## Technical Requirements

- Written in Java 8 or higher.
- Uses `BufferedReader` and `BufferedWriter` for file I/O.
- Uses `Files` API (`java.nio.file`) for file management.
- Implements a simple console menu system for user interaction.
- Handles all possible I/O exceptions gracefully.
- Uses `LocalDateTime` and `DateTimeFormatter` to generate timestamps for filenames.
- Supports searching and backup functionality.

---

## How to Run

1. Clone the repository:

```bash
git clone <repository_url>
