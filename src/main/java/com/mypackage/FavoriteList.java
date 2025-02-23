package la1;


public class FavoriteList extends Playlist {
    public FavoriteList() {
        super();
    }
    @Override
    public String toString() {
        if (getSongs().isEmpty()) {
            return "The favorite list is empty.";
        }
        StringBuilder sb = new StringBuilder("Favorite Songs:\n");
        int index = 1;
        for (var song : getSongs()) {
            sb.append(index++).append(") ").append(song.getTitle()).append("\n");
        }
        return sb.toString();
    }
}