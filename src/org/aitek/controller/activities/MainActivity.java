package org.aitek.controller.activities;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.aitek.controller.R;
import org.aitek.controller.ui.ImageAdapter;
import org.aitek.controller.loaders.Mede8erScanner;
import org.aitek.controller.ui.GenericProgressIndicator;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.ui.ProgressIndicator;

public class MainActivity extends Activity implements SearchView.OnQueryTextListener {

    public static ImageAdapter movieGridAdapter;
    public static ImageAdapter musicGridAdapter;
    private GridView moviesGridView;
    private ListView moviesGenresListView;
    private GridView musicGridView;
    private ListView musicGenresListView;

    private SearchView searchView;
    private ActionBar actionBar;
    private Mede8erCommander mede8erCommander;
    private boolean areAllImagesSaved = false;
    private int savedImageCounter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mede8erCommander = Mede8erCommander.getInstance(this);

        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab moviesTab = actionBar.newTab().setText(getString(R.string.movies_tab));
        ActionBar.Tab musicTab = actionBar.newTab().setText(getString(R.string.music_tab));
        ActionBar.Tab playlistTab = actionBar.newTab().setText(getString(R.string.playlist_tab));

        Fragment moviesFragment = new MoviesFragment();
        Fragment musicFragment = new MusicFragment();
        Fragment playlistFragment = new PlaylistFragment();

        moviesTab.setTabListener(new MyTabsListener(moviesFragment));
        musicTab.setTabListener(new MyTabsListener(musicFragment));
        playlistTab.setTabListener(new MyTabsListener(playlistFragment));

        actionBar.addTab(moviesTab);
        actionBar.addTab(musicTab);
        actionBar.addTab(playlistTab);
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

            case R.id.menu_reset:
                return true;

            case R.id.menuSortByTitle:
                mede8erCommander.getMoviesManager().setSortField("title");
                movieGridAdapter.notifyDataSetChanged();
                return true;

            case R.id.menuSortByDate:
                mede8erCommander.getMoviesManager().setSortField("date");
                movieGridAdapter.notifyDataSetChanged();
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
        mede8erCommander.getMoviesManager().setGenericFilter(newText);
        movieGridAdapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        mede8erCommander.getMoviesManager().setGenericFilter(query);
        movieGridAdapter.notifyDataSetChanged();
        searchView.clearFocus();

        return true;
    }


    public void imageSaved() {
        savedImageCounter ++;
    }

    /**
     * this method is called from the mede8erconnector when has loaded all the data
     */
    public void dataReady() {
    }

    public class MoviesFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.movies_main, container, false);
        }

    }

    public class MusicFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.music_main, container, false);
        }

    }

    public class PlaylistFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.playlist_main, container, false);
        }

    }


    class MyTabsListener implements ActionBar.TabListener {
        public Fragment fragment;

        public MyTabsListener(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            fragmentTransaction.replace(R.id.fragment_container, fragment);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            fragmentTransaction.remove(fragment);
        }

    }
}