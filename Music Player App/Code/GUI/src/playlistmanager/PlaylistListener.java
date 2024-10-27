package playlistmanager;

public interface PlaylistListener {
    void onSongChanged(String newSongName);
    void simulateSeekbar(int currentTime, int songDuration);
    void showMessageDialog(String message);
}
