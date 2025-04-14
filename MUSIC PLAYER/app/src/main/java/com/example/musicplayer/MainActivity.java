// MainActivity.java
package com.example.musicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SongChangeListener {

    private final List<MusicList> musicLists = new ArrayList<>();
    private RecyclerView musicRecyclerView;
    private MusicAdapter musicAdapter;
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private int currentSongListPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicRecyclerView = findViewById(R.id.musicRecyclerView);

        musicRecyclerView.setHasFixedSize(true);
        musicRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            connectToServer();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
            }
        }
    }

    private void connectToServer() {
        new ConnectToServerTask().execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            connectToServer();
        } else {
            Toast.makeText(this, "Permissions Declined By User", Toast.LENGTH_SHORT).show();
        }
    }

    private class ConnectToServerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Replace "10.0.2.2" with the actual IP address of your server
                socket = new Socket("192.168.100.133", 8080);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printWriter = new PrintWriter(socket.getOutputStream(), true);

                // Send a request to the server to get the list of songs
                printWriter.println("GET_SONG_LIST");

                // Receive the list of songs from the server
                final String songList = bufferedReader.readLine();

                // Update UI with the received list of songs
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Parse the songList and update RecyclerView with the songs
                        parseSongList(songList);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void parseSongList(String songList) {
        String[] songs = songList.split(",");
        for (String song : songs) {
            MusicList musicList = new MusicList(song, "", "", false, null);
            musicLists.add(musicList);
        }
        musicAdapter = new MusicAdapter(musicLists, this);
        musicRecyclerView.setAdapter(musicAdapter);
    }

    @Override
    public void onChanged(int position) {
        currentSongListPosition = position;
        // Implement logic to play the selected song
        // You can use the musicLists.get(currentSongListPosition) to get details of the selected song
        // Example: String selectedSong = musicLists.get(currentSongListPosition).getTitle();
    }
}
