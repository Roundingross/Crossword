package edu.jsu.mcis.cs408.crosswordmagic.model.dao;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleMenuItem;

public class WebServiceDAO {
    private static final String ROOT_URL = "http://ec2-3-142-171-53.us-east-2.compute.amazonaws.com:8080/CrosswordMagicServer/puzzle";

    public WebServiceDAO() {
    }

    public PuzzleMenuItem[] list() {
        PuzzleMenuItem[] result = null;
        try {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            Future<String> pending = pool.submit(new CallableHTTPRequest(ROOT_URL));
            String response = pending.get();
            pool.shutdown();

            JSONArray json = new JSONArray(response);
            ArrayList<PuzzleMenuItem> list = new ArrayList<>();

            for (int i = 0; i < json.length(); i++) {
                JSONObject obj = json.getJSONObject(i);
                int id = obj.getInt("id");
                String name = obj.getString("name");
                list.add(new PuzzleMenuItem(id, name));
            }
            result = list.toArray(new PuzzleMenuItem[0]);
        } catch (Exception e) {
            String TAG = "WebServiceDAO";
            Log.e(TAG, "Web request failed", e);
        }
        return result;
    }

    private static class CallableHTTPRequest implements Callable<String> {
        private final String url;

        public CallableHTTPRequest(String url) {
            this.url = url;
        }

        @Override
        public String call() {
            StringBuilder result = new StringBuilder();
            try {
                URL endpoint = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
                conn.setRequestMethod("GET");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString().trim();
        }
    }

    public JSONObject getPuzzleFromWeb(int webId) {
        try {
            String url = ROOT_URL + "?id=" + webId;
            Log.d("DEBUG", "Calling URL: " + url);

            ExecutorService pool = Executors.newSingleThreadExecutor();
            Future<String> pending = pool.submit(new CallableHTTPRequest(url));
            String response = pending.get();
            pool.shutdown();

            Log.d("DEBUG", "Web response: " + response); // <-- ADD THIS

            return new JSONObject(response);
        }
        catch (Exception e) {
            Log.d("WebServiceDAO", "Error retrieving puzzle by web ID", e);
            return null;
        }
    }


}
