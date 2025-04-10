package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleMenuItem;
import edu.jsu.mcis.cs408.crosswordmagic.controller.MenuAdapter;

public class MenuActivity extends AppCompatActivity {
    // RecyclerView and adapter
    private RecyclerView recyclerView;
    private MenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.menuRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Start fetching puzzle list from server
        new FetchPuzzleList().execute();
    }

    // AsyncTask to fetch puzzle list from server
    private class FetchPuzzleList extends AsyncTask<Void, Void, ArrayList<PuzzleMenuItem>> {
        @Override
        protected ArrayList<PuzzleMenuItem> doInBackground(Void... voids) {
            ArrayList<PuzzleMenuItem> puzzles = new ArrayList<>();
            try {
                // Fetch puzzle list from server
                URL url = new URL(getString(R.string.puzzle_url));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // Read response
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Parse JSON response
                JSONArray jsonArray = new JSONArray(result.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject puzzle = jsonArray.getJSONObject(i);
                    int id = puzzle.getInt("id");
                    String name = puzzle.getString("name");
                    puzzles.add(new PuzzleMenuItem(id, name));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return puzzles;
        }

        // Update RecyclerView with puzzle list
        @Override
        protected void onPostExecute(ArrayList<PuzzleMenuItem> puzzles) {
            adapter = new MenuAdapter(MenuActivity.this, puzzles);
            recyclerView.setAdapter(adapter);
        }
    }
}
