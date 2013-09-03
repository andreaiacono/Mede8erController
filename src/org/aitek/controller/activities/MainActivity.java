package org.aitek.controller.activities;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.aitek.controller.R;
import org.aitek.controller.ui.ImageAdapter;
import org.aitek.controller.loaders.Mede8erScanner;
import org.aitek.controller.ui.GenericProgressIndicator;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.ui.ProgressIndicator;

import java.io.FileNotFoundException;

public class MainActivity extends Activity implements SearchView.OnQueryTextListener, ActionBar.TabListener {

    public static ImageAdapter imageAdapter;
    private GridView moviesGridView;
    private ListView moviesGenresListView;
    private GridView musicGridView;
    private ListView musicGenresListView;
    private SearchView searchView;
    private ActionBar actionBar;
    private Mede8erCommander mede8erCommander;

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
            mede8erCommander = Mede8erCommander.getInstance(this);
            imageAdapter = new ImageAdapter(this);
            ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mede8erCommander.getMoviesManager().getMovieGenres());

            moviesGenresListView = (ListView) findViewById(R.id.listView);
            moviesGenresListView.setAdapter(adapter);
            moviesGenresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mede8erCommander.getMoviesManager().setMovieGenreFilter(moviesGenresListView.getAdapter().getItem(i).toString());
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

        AlertDialog.Builder firstTimeDialog = new AlertDialog.Builder(MainActivity.this);
        firstTimeDialog.setTitle(getString(R.string.first_time_run));
        firstTimeDialog.setMessage(getString(R.string.first_time_run_message));

        // YES button
        firstTimeDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                scanMediaPlayer();
            }
        });

        // NO button
        firstTimeDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        AlertDialog noDataDialog = new AlertDialog.Builder(MainActivity.this).create();
                        noDataDialog.setTitle(getString(R.string.no_data));
                        noDataDialog.setMessage(getString(R.string.no_data_message));

                        noDataDialog.setButton(which, "OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        noDataDialog.show();
                    }
                });

        firstTimeDialog.show();
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
                mede8erCommander.getMoviesManager().setMovieSortField("title");
                imageAdapter.notifyDataSetChanged();
                return true;

            case R.id.menuSortByDate:
                mede8erCommander.getMoviesManager().setMovieSortField("date");
                imageAdapter.notifyDataSetChanged();
                return true;

            case R.id.menu_mediaplayer_info:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void scanMediaPlayer() {
        try {
            mede8erCommander.getMoviesManager().clear();
            GenericProgressIndicator genericProgressIndicator = new Mede8erScanner(this);
            genericProgressIndicator.setup();
            new ProgressIndicator().progress("Scanning media player..", genericProgressIndicator);
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
        mede8erCommander.getMoviesManager().setMovieGenericFilter(newText);
        imageAdapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        mede8erCommander.getMoviesManager().setMovieGenericFilter(query);
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