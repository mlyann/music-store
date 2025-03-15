package la1;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayLists {
    private final HashMap<String, Playlist> playLists;


    public PlayLists() {
        this.playLists = new HashMap<>();
    }

    public Boolean addPlayList(String name) {
        if (playLists.containsKey(name)) {
            return false;
        } else {
            playLists.put(name, new Playlist(name));
            return true;
        }
    }

    public void printPlayList(String name) {
        if (playLists.containsKey(name)) {
            playLists.get(name).printAsTable();
            System.out.println("Playlist [" + name + "] FOUND.");
        } else {
            System.out.println("Playlist [" + name + "] does not exist.");
        }
    }

    public Boolean removePlayList(int index) {
        String name = new ArrayList<>(playLists.keySet()).get(index);
        playLists.remove(name);
        return true;
    }

    public Playlist getPlayList(int index) {
        if (index < 0 || index >= playLists.size()) {
            System.out.println("Invalid index.");
            return null;
        }
        return new ArrayList<>(playLists.values()).get(index);
    }


    public int getSize() {
        return playLists.size();
    }

    public void printAllPlayLists() {
        int count = 1;
        if (playLists.isEmpty()) {
            System.out.println("No playlist found.");
        } else {
            for (String name : playLists.keySet()) {
                System.out.println("Playlist " + count++ + ": " + name);
                playLists.get(name).printAsTable();
            }
        }
    }

    public void clearAllPlayLists() {
        playLists.clear();
        System.out.println("All playlists cleared.");
    }


    public void addSongToPlayList(String playListName, Song song) {
        if (playLists.containsKey(playListName)) {
            playLists.get(playListName).addSong(song);
        } else {
            System.out.println("Playlist [" + playListName + "] does not exist.");
        }
    }

    public void removeSongFromPlayList(String playListName, Song song) {
        if (playLists.containsKey(playListName)) {
            playLists.get(playListName).removeSong(song);
        } else {
            System.out.println("Playlist [" + playListName + "] does not exist.");
        }
    }

    public ArrayList<String> getPlayListNames() {
        return new ArrayList<>(playLists.keySet());
    }

    public Playlist getPlayListByName(String name) {
        return playLists.get(name);
    }

}
