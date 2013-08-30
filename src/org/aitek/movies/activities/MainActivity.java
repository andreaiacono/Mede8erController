package org.aitek.movies.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.aitek.movies.R;
import org.aitek.movies.core.MoviesManager;
import org.aitek.movies.loaders.FileSystemScanner;
import org.aitek.movies.loaders.ImageAdapter;
import org.aitek.movies.loaders.Progressable;
import org.aitek.movies.utils.ProgressIndicator;

public class MainActivity extends Activity implements SearchView.OnQueryTextListener, ActionBar.TabListener {

    public static ImageAdapter imageAdapter;
    private GridView moviesGridView;
    private ListView moviesGenresListView;

    private GridView musicGridView;
    private ListView musicGenresListView;

    private SearchView searchView;
    private ActionBar actionBar;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_main);

        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText("Movies").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Music").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Playlists").setTabListener(this));

        try {
            MoviesManager.init(this);

            imageAdapter = new ImageAdapter(this);
            ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MoviesManager.getGenres());

            moviesGenresListView = (ListView) findViewById(R.id.listView);
            moviesGenresListView.setAdapter(adapter);
            moviesGenresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    MoviesManager.setGenreFilter(moviesGenresListView.getAdapter().getItem(i).toString());
                    imageAdapter.notifyDataSetChanged();
                }
            });

            moviesGridView = (GridView) findViewById(R.id.gridView);
            moviesGridView.setAdapter(imageAdapter);

            moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                    Intent fullScreenIntent = new Intent(v.getContext(), MovieDetailActivity.class);
                    fullScreenIntent.putExtra(MovieDetailActivity.class.getName(), position);
                    startActivity(fullScreenIntent);
                }
            });
        }
        catch (Exception ex) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error on initialization: " + ex.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            ex.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_settings:
                Toast toast = Toast.makeText(getApplicationContext(), "Settings!", Toast.LENGTH_SHORT);
                toast.show();
                return true;

            case R.id.menu_scan_mediaplayer:

                try {
                    MoviesManager.clear();
                    Progressable progressable = new FileSystemScanner();
                    progressable.setup(this);
                    new ProgressIndicator().progress("Scanning media player..", progressable);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.menuSortByTitle:
                MoviesManager.setSortField("title");
                imageAdapter.notifyDataSetChanged();
                return true;

            case R.id.menuSortByDate:
                MoviesManager.setSortField("date");
                imageAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText.length() == 0) {
            searchView.clearFocus();
        }
        MoviesManager.setGenericFilter(newText);
        imageAdapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        MoviesManager.setGenericFilter(query);
        imageAdapter.notifyDataSetChanged();
        searchView.clearFocus();

        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        switch (tab.getPosition()) {
            case 0:
                setContentView(R.layout.movies_main);
                break;
            case 1:
                setContentView(R.layout.music_main);
                break;
            case 2:
                break;
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
}