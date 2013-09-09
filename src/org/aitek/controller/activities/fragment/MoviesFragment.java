package org.aitek.controller.activities.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.aitek.controller.R;
import org.aitek.controller.activities.MovieDetailActivity;
import org.aitek.controller.core.MoviesManager;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.ui.ImageAdapter;
import org.aitek.controller.utils.Logger;


public class MoviesFragment extends TabFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout relativeLayout = (RelativeLayout)inflater.inflate(R.layout.movies_main, container, false);

        final MoviesManager moviesManager = Mede8erCommander.getInstance(getActivity().getApplicationContext()).getMoviesManager();
        Logger.log("activity= " + getActivity());
        imageAdapter = new ImageAdapter(getActivity());
        ArrayAdapter adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, moviesManager.getGenres());


        genresListView = (ListView) relativeLayout.findViewById(R.id.moviesListView);
        Logger.log("adapter= " + adapter);
        Logger.log("litview= " + genresListView);
        genresListView.setAdapter(adapter);
        genresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                moviesManager.setGenreFilter(genresListView.getAdapter().getItem(i).toString());
                imageAdapter.notifyDataSetChanged();
            }
        });

        gridView = (GridView) relativeLayout.findViewById(R.id.moviesGridView);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Intent fullScreenIntent = new Intent(v.getContext(), MovieDetailActivity.class);
                fullScreenIntent.putExtra(MovieDetailActivity.class.getName(), position);
                startActivity(fullScreenIntent);
            }
        });

        return relativeLayout;
    }

}