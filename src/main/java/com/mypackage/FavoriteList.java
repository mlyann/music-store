package la1;

import java.util.ArrayList;
import java.util.List;

public class FavoriteList extends Playlist {

    public FavoriteList() {
        super();
    }

    /**
     * 重写 addSong 方法，添加歌曲到收藏列表
     */
    @Override
    public void addSong(Song song) {
        getSongs().add(song);

    }

    /**
     * 重写 removeSong 方法，从收藏列表中移除歌曲
     */
    @Override
    public void removeSong(Song song) {
        getSongs().remove(song);
    }

    /**
     * 返回收藏列表中歌曲的数量
     */
    public int getSize() {
        return getSongs().size();
    }

    /**
     * 以表格形式打印收藏列表的详细信息
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
            if (row.size() > 6 && row.get(6) != null && !row.get(6).isBlank()) {
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
     * 重写 toString 方法，返回收藏列表的字符串表示
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
