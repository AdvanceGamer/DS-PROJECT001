package playlistmanager;

public class SearchResult {
    private Song song;
    private int position;

    public SearchResult(Song song, int position) {
        this.song = song;
        this.position = position;
    }

    public Song getSong() {
        return song;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Song: " + song.toString() + ", Position: " + position;
    }
}
