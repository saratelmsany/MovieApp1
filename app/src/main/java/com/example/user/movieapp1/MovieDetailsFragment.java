package com.example.user.movieapp1;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {
    String path,date,rate,title;
    TextView tPath,tDate,tRate;
    ImageView posterview;

    public MovieDetailsFragment() {setHasOptionsMenu(true);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent in = getActivity().getIntent();
        if (in != null ) {
            path = in.getStringExtra("path");
            date = in.getStringExtra("date");
            rate = in.getStringExtra("rate");
            title = in.getStringExtra("title");

        }
        String baseURL="http://image.tmdb.org/t/p/w185";
        View rootview = inflater.inflate(R.layout.fragment_movie_details, container, false);
        Toolbar toolbar = (Toolbar)rootview.findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        tDate = (TextView)rootview.findViewById(R.id.releaseDate);
        tRate = (TextView)rootview.findViewById(R.id.rate);
        posterview = (ImageView)rootview.findViewById(R.id.posterDetail);
        Picasso.with(getContext()).load(baseURL + path).into(posterview);
        tRate.setText(rate);
        tDate.setText(date);
        return rootview;
    }
}
