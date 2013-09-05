package org.aitek.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import org.aitek.controller.R;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.ui.ImageAdapter;

public class MovieActivity extends Activity {

    public static ImageAdapter movieGridAdapter;
    private GridView moviesGridView;
    private ListView moviesGenresListView;
    private Mede8erCommander mede8erCommander;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mede8erCommander = Mede8erCommander.getInstance(this);

        setContentView(R.layout.movies_main);

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mede8erCommander.getMoviesManager().getGenres());

        moviesGenresListView = (ListView) findViewById(R.id.moviesListView);
        moviesGenresListView.setAdapter(adapter);
        moviesGenresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mede8erCommander.getMoviesManager().setGenreFilter(moviesGenresListView.getAdapter().getItem(i).toString());
                movieGridAdapter.notifyDataSetChanged();
            }
        });

        moviesGridView = (GridView) findViewById(R.id.moviesGridView);
        moviesGridView.setAdapter(movieGridAdapter);
        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Intent fullScreenIntent = new Intent(v.getContext(), MovieDetailActivity.class);
                fullScreenIntent.putExtra(MovieDetailActivity.class.getName(), position);
                startActivity(fullScreenIntent);
            }
        });
    }
}