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

public class MusicActivity extends Activity {

    public static ImageAdapter musicGridAdapter;
    private GridView musicGridView;
    private ListView musicGenresListView;
    private Mede8erCommander mede8erCommander;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mede8erCommander = Mede8erCommander.getInstance(this);

        setContentView(R.layout.movies_main);

        setContentView(R.layout.music_main);

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mede8erCommander.getMusicManager().getGenres());

        musicGenresListView = (ListView) findViewById(R.id.musicListView);
        musicGenresListView.setAdapter(adapter);
        musicGenresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mede8erCommander.getMusicManager().setGenreFilter(musicGenresListView.getAdapter().getItem(i).toString());
                musicGridAdapter.notifyDataSetChanged();
            }
        });

        musicGridView = (GridView) findViewById(R.id.musicGridView);
        musicGridView.setAdapter(musicGridAdapter);
        musicGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Intent fullScreenIntent = new Intent(v.getContext(), MusicDetailActivity.class);
                fullScreenIntent.putExtra(MusicDetailActivity.class.getName(), position);
                startActivity(fullScreenIntent);
            }
        });
    }
}