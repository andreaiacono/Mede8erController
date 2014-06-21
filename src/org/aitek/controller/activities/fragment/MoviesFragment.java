package org.aitek.controller.activities.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.aitek.controller.R;
import org.aitek.controller.activities.MainActivity;
import org.aitek.controller.activities.MovieDetailActivity;
import org.aitek.controller.core.MoviesManager;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.ui.ImageAdapter;
import org.aitek.controller.utils.Logger;


public class MoviesFragment extends TabFragment {

    private MainActivity mainActivity;

    public MoviesFragment(MainActivity mainActivity) {

        this.mainActivity = mainActivity;
    }

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.movies_main, container, false);
        final MoviesManager moviesManager = Mede8erCommander.getInstance(getActivity()).getMoviesManager();
        imageAdapter = new ImageAdapter(getActivity());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (moviesManager.getGenres() != null) {

            ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, moviesManager.getGenres());
            genresListView = (ListView) relativeLayout.findViewById(R.id.moviesListView);
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
                    if (mainActivity != null && !mainActivity.isInCacheMode()) {
                        Intent fullScreenIntent = new Intent(v.getContext(), MovieDetailActivity.class);
                        fullScreenIntent.putExtra(MovieDetailActivity.class.getName(), position);
                        startActivity(fullScreenIntent);
                    }
                    else {
                        Logger.toast("Movie detail disabled while in cache mode.", mainActivity);
                    }
                }
            });
        }
        return relativeLayout;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}