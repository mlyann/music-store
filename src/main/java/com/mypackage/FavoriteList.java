package la1;

import java.util.ArrayList;
import java.util.List;

public class FavoriteList extends Playlist {

    public FavoriteList() {
        super();
    }

    /**
     * Overrides the addSong method to add a given song to the favorite list.
     */
    @Override
    public void addSong(Song song) {
        getSongs().add(song);

    }

    /**
     * Overrides the removeSong method to remove the specified song from the favorite list.
     */
    @Override
    public void removeSong(Song song) {
        getSongs().remove(song);
    }


    public int getSize() {
        return getSongs().size();
    }


    /**
     *  Overrides the printAsTable method to print detailed information of the favorite list in a table format.
     *  and then prints the table using the TablePrinter.
     */
    @Override
    public void printAsTable(String name) {
        if (getSongs().isEmpty()) {
            System.out.println("The playlist is empty.");
            return;
        }
        ArrayList<ArrayList<String>> playString = new ArrayList<>();
        for (Song s : getSongs()) {
            playString.add(s.toStringList());
        }

        // table head
        List<String> header = new ArrayList<>();
        header.add("No.");
        header.add("Title");
        header.add("Artist");
        header.add("Genre");
        header.add("Year");
        header.add("Favorite");
        header.add("Rating  ");


        boolean anyAlbum = false;
        for (List<String> row : playString) {
            if (row.size() > 6 && row.get(6) != null) {
                anyAlbum = true;
                break;
            }
        }
        if (anyAlbum) {
            header.add("Album");
        }

        // Combine into a 2D structure for TablePrinter.
        List<List<String>> tableRows = new ArrayList<>();
        tableRows.add(header);

        int index = 1;
        for (Song s : getSongs()) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(index++));
            ArrayList<String> info = s.toStringList();
            row.add(info.get(0)); // Title
            row.add(info.get(1)); // Artist
            row.add(info.get(2)); // Genre
            row.add(info.get(3)); // Year
            row.add(info.get(4)); // Favorite
            row.add(info.get(5)); // Rating
            if (anyAlbum) {
                String album = (info.size() > 6) ? info.get(6) : "";
                row.add(album == null ? "" : album);
            }
            tableRows.add(row);
        }

        // TablePrinter prints information in a table
        TablePrinter.printDynamicTable(name, tableRows);
    }

    /**
     * Overrides the toString method to return a string representation of the favorite list.
     * Lists each song with its index, or returns a message indicating that the favorite list is empty if there are no songs.
     */
    @Override
    public String toString() {
        if (getSongs().isEmpty()) {
            return "The favorite list is empty.";
        }
        StringBuilder sb = new StringBuilder("Favorite Songs:\n");
        int index = 1;
        for (Song song : getSongs()) {
            sb.append(index++).append(") ").append(song.getTitle()).append("\n");
        }
        return sb.toString();
    }
}
