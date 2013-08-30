package org.aitek.movies.activities;

import android.app.*;
import android.content.DialogInterface;
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

import java.io.FileNotFoundException;

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
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.movies_tab)).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.music_tab)).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.playlist_tab)).setTabListener(this));

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
        catch (FileNotFoundException e) {
            showInitDialog();
        }
        catch (Exception ex) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error on initialization: " + ex.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            ex.printStackTrace();
        }

    }

    private void showInitDialog() {

        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(MainActivity.this);
        alertDialog2.setTitle(getString(R.string.first_time_run));
        alertDialog2.setMessage(getString(R.string.first_time_run_message));

        // YES button
        alertDialog2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                scanMediaPlayer();
            }
        });

        // NO button
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        AlertDialog alertDialog1 = new AlertDialog.Builder(MainActivity.this).create();
                        alertDialog1.setTitle(getString(R.string.no_data));
                        alertDialog1.setMessage(getString(R.string.no_data_message));

                        // Setting OK Button
                        alertDialog1.setButton(which, "OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        // Showing Alert Message
                        alertDialog1.show();
                    }
                });

// Showing Alert Dialog
        alertDialog2.show();
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

                scanMediaPlayer();
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

    private void scanMediaPlayer() {
        try {
            MoviesManager.clear();
            Progressable progressable = new FileSystemScanner();
            progressable.setup(this);
            new ProgressIndicator().progress("Scanning media player..", progressable);
        }
        catch (Exception e) {
            e.printStackTrace();
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