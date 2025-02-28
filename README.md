# Music Library App (CSC 335 LA1)

Welcome to the **Music Library App**! This console-based Java application allows you to manage a music library, search for songs/albums, create playlists, rate songs, and maintain a list of favorites. It is a project for **CSC 335 LA1**, taught by **Instructor Malenie Lotz** with the assistance of **TA Jenny Yu**. The authors of this project are **Haocheng Cao** and **Minglai Yang**.

---

## Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [Project Structure](#project-structure)
4. [Getting Started](#getting-started)
5. [How to Use](#how-to-use)
6. [Sample Flow](#sample-flow)
7. [Author & Acknowledgments](#author--acknowledgments)

---

## Overview

The **Music Library App** is a simple command-line interface (CLI) program that simulates the functionalities of managing music data:

- **Search and load songs** from a "Store" into a local user library.
- **View and handle albums** within the store or user library.
- **Create and manage playlists**, add or remove songs, and play them.
- **Rate songs** and mark them as favorites.
- **View your entire library** of songs, albums, artists, and more.

This project helps students practice object-oriented programming (OOP) concepts and Java coding skills by simulating a real-world application with menus, data manipulation, and user interactions.

---

## Features

1. **Song Management**
    - Input/load song details manually into the library.
    - Search for songs by title or artist (in both Store and User Library).
    - View song details (title, artist, genre, year, rating, and whether it is a favorite).

2. **Album Management**
    - Search for albums by album title or artist.
    - Load entire albums from the Store into the user library.
    - View album info and associated tracks.

3. **Library Management**
    - View all songs, albums, and artists in the user library.
    - Organize songs into multiple playlists.
    - Mark/unmark songs as favorites.

4. **Playlists**
    - Create new playlists, open existing ones, and add/remove songs.
    - Shuffle or play an entire playlist.

5. **Favorites**
    - Maintain a dedicated “Favorite List.”
    - Easily add/remove songs to/from the favorites.
    - Play songs directly from the favorites list.

6. **Song Rating System**
    - Rate songs on a scale of 1–5.
    - Automatically add songs rated 5 stars to Favorites.

---

## Project Structure
```
la1/
├── MainUI.java         // The main class containing the CLI flow (menus, input, etc.)
├── MusicStore.java     // Manages store-related data (songs, albums available to load)
├── LibraryModel.java   // Core logic for the user’s library, playlists, favorites, rating
├── Song.java           // Represents a single Song entity
├── Album.java          // Represents an Album entity containing multiple songs
├── TablePrinter.java   // Utility class for printing tables in the console
└── 
```
- **MainUI**  
  Contains all the user interface logic and menu navigation in a console environment.

- **MusicStore**  
  Manages a collection of available songs/albums that can be loaded into the user library.

- **LibraryModel**  
  Core business logic and data handling for user’s library, playlists, favorites, and ratings.

- **Song** / **Album**  
  Data model classes representing individual songs and albums.

- **TablePrinter**  
  Utility class used for formatting and printing lists of data in a table-like structure to the console.

---

## Getting Started

### Prerequisites
- **Java 8** or higher installed on your system.
- A Java-compatible IDE (e.g., IntelliJ, Eclipse, VS Code) or the ability to compile via the command line.

### Installation & Compilation
1. Clone or download this repository:
   ```bash
   git clone https://github.com/mlyann/music-store.git
    ```
	2.	Open the project in your preferred IDE or navigate into the project folder via terminal:
    ```bash
    cd music-store
    ```

	3.	Ensure that the package structure (la1) is respected if you are using an IDE.
	4.	Compile the code (if using command line):
    ```bash
    javac la1/*.java
    ```


Running the Application
•	After compilation, run the main application class:
```bash
java la1.MainUI
```

## How to Use

Once you run the program:
1.	Main Menu
•	Search (Songs/Albums in Store or Library)
•	PlayingList (a quick, single “now-playing” playlist)
•	View all PlayLists (manages multiple playlists)
•	Favorite List (view/edit your favorite songs)
•	Library Lists (shows all songs/albums/artists in your library)
•	Load Songs (manually input song data)
•	Quit the application
2.	Navigating the Menus
Use the numeric options presented to you in the console to navigate between submenus. Press 0 (zero) to go back or exit, and follow the on-screen prompts.
3.	Adding/Removing Songs
•	Enter songs manually via the Load Songs option.
•	Search the Store for songs or albums by a keyword, then add them to your library.
4.	Creating & Managing Playlists
•	Create new playlists, open existing ones, and add/remove songs.
•	Shuffle or play an entire playlist.
5.	Favorites & Rating
•	Rate a song from 1 to 5.
•	Songs rated 5 will automatically become favorites.
•	Access favorites from the Favorite List submenu.
6.	Playing Music
•	Choose songs or playlists to “play.”
•	Although this is a simulation, the interface will respond as if the song is being played.

## Sample Flow

Below is a brief example of how a typical session might proceed in the console:

1. **Main Menu**  
   ```
   ---------- 🎵 MAIN MENU 🎵 ----------
   1) 🔍 Search
   2) 🎧 PlayingList
   3) 📝 View all PlayLists
   4) ❤️ Favorite List
   5) 🏠 Library Lists
   6) ➕ Load Songs single
   0) 🚪 Quit the application
      👉 Enter your choice:
   ```

2. **Choose "6" to Load a Song**  
   - Enter the song details in the format: `title, artist, genre, year`  
     Example: `Shape of You, Ed Sheeran, Pop, 2017`

3. **Search in the Store**  
   - Navigate to `Search -> Store`.  
   - Enter a keyword to find an album or a song in the Store.  
   - Load it into your library.

4. **View Your Library**  
   - Check the newly added songs in the `Library Lists` section.

5. **Create a Playlist**  
   - Go to `View all PlayLists` -> `Add a new playlist` -> `Open it` -> `Add a song`.  
   - You can play or shuffle the playlist.

6. **Rate a Song**  
   - Select the song from your library or playlist menu.  
   - Provide a star rating between `1` to `5`.

7. **Exit the Application**  
   - From the **Main Menu**, choose `"0"` to quit.



## Author & Acknowledgments
•	Authors:
•	[Haocheng Cao](https://github.com/Boldthinkingcat)
•	[Minglai Yang](https://ymingl.com/)
•	Instructor: [Malenie Lotz](https://www.cs.arizona.edu/person/melanie-lotz) (CSC 335)
•	Teaching Assistant: Jenny Yu
