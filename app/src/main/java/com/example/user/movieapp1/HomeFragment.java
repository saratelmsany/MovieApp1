package com.example.user.movieapp1;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {
    private ImageAdapter mMovieAdapter;
    ArrayList<String> path;
    ArrayList<Response> movies;
    GridView gridView;
    View rootView;
    JSONArray jsonArray;
    JSONObject moviejsonObject;
    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateMovie() {
        FetchTask movietask = new FetchTask();
        movietask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        mMovieAdapter = new ImageAdapter(getActivity());
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        gridView = (GridView) rootView.findViewById(R.id.grid_view);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Response movie= movies.get(position);
                String path =movie.getPoster_path();
                String title= movie.getTitle();
                String date=movie.getRelease_date();
                double rate =movie.getVote_average();
                Intent in = new Intent(getActivity(), MovieDetails.class);
                in.putExtra("path",path);
                in.putExtra("title",title);
                in.putExtra("date",date);
             //   in.putExtra("rate",rate);
                startActivity(in);
            }
        });


        return rootView;
    }

    public class FetchTask extends AsyncTask<Void, Void, ArrayList<String>> {

        private final String LOG_TAG = FetchTask.class.getSimpleName();


        @Override
        protected ArrayList<String> doInBackground(Void... params) {
//            if (params.length == 0) {
//                return null;
//            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String Movie_BASE_URL = "http://api.themoviedb.org/3/movie/popular?api_key=139ac8e6e73c109199481780d48ef29a";
                URL url = new URL(Movie_BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Json " + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            Gson gson = new Gson();
            // Response response = gson.fromJson(movieJsonStr, Response.class);
            path = new ArrayList<String>();
            movies= new ArrayList<>();

            try {
                moviejsonObject = new JSONObject(movieJsonStr);
                jsonArray = moviejsonObject.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Response response = gson.fromJson(jsonObject.toString(), Response.class);
                    path.add(response.getPoster_path());
                    movies.add(response);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            // path.add(response.getPoster_path());

            return path;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            mMovieAdapter = new ImageAdapter(getActivity(), path);

            gridView = (GridView) rootView.findViewById(R.id.grid_view);
            gridView.setAdapter(mMovieAdapter);

        }
    }
}
