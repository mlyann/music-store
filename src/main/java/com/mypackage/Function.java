package la1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Function {
    private final MusicStore musicStore;

    public Function() {
        this.musicStore = new MusicStore() {
            @Override
            public Album parseAlbumFile(String albumFileName) {
                return null;
            }
        };
    }

    private ArrayList<Song> searchSong (String keyword) {
        Map<String, Song> songMap = musicStore.getSongMap();
        ArrayList<Song> result = new ArrayList<>();
        for (String key : songMap.keySet()) {
            if (key.toLowerCase().contains(keyword.toLowerCase())) {
                result.add(songMap.get(key));
            }
        }
        return result;
    }

    private ArrayList<Album> searchAlbum (String keyword) {
        Map<String, Album> albumMap = musicStore.getAlbumMap();
        ArrayList<Album> result = new ArrayList<>();
        for (String key : albumMap.keySet()) {
            if (key.toLowerCase().contains(keyword.toLowerCase())) {
                result.add(albumMap.get(key));
            }
        }
        return result;
    }

    public Map<String, ArrayList> search (String keyword) {
        ArrayList<Song> songs = searchSong(keyword);
        ArrayList<Album> albums = searchAlbum(keyword);
        Map<String, ArrayList> result = new HashMap<>();
        result.put("songs", songs);
        result.put("albums", albums);
        if (result.get("songs").isEmpty() && result.get("albums").isEmpty()) {
            System.out.println("No results found for " + keyword);
            return null;
        }
        return result;
    }


}
