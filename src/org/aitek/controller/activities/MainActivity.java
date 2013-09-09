package org.aitek.controller.activities;

import android.app.*;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.*;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;
import org.aitek.controller.R;
import org.aitek.controller.activities.fragment.MoviesFragment;
import org.aitek.controller.activities.fragment.MusicFragment;
import org.aitek.controller.activities.fragment.PlaylistFragment;
import org.aitek.controller.activities.fragment.TabFragment;
import org.aitek.controller.loaders.Mede8erScanner;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.ui.GenericProgressIndicator;
import org.aitek.controller.ui.ProgressIndicator;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;

import java.io.File;

public class MainActivity extends Activity implements SearchView.OnQueryTextListener {

    private SearchView searchView;
    private ActionBar actionBar;
    private Mede8erCommander mede8erCommander;
    private TabFragment currentTabFragment;
    private Handler dialogHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MEDE8ER_NOT_CONNECTED:

                    AlertDialog noMede8erDialog = new AlertDialog.Builder(MainActivity.this).create();
                    noMede8erDialog.setTitle(getString(R.string.no_mede8er));
                    noMede8erDialog.setMessage(getString(R.string.no_mede8er_message));
                    noMede8erDialog.show();
                    break;
            }
        }
    };

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
                Mede8erScannerTask task = new Mede8erScannerTask();
                task.execute(new String[]{});
                return true;

            case R.id.menu_reset:
                File movieFile = getApplicationContext().getFileStreamPath(Constants.MOVIES_FILE);
                movieFile.delete();
                File musicFile = getApplicationContext().getFileStreamPath(Constants.MUSIC_FILE);
                musicFile.delete();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                preferences.edit().remove(Constants.PREFERENCES_MEDE8ER_IPADDRESS);
                preferences.edit().commit();
                Logger.log("Data reset successful.");
                return true;

            case R.id.menuSortByTitle:
                mede8erCommander.getMoviesManager().setSortField("title");
                currentTabFragment.notifyChangedGridData();
                return true;

            case R.id.menuSortByDate:
                mede8erCommander.getMoviesManager().setSortField("date");
                currentTabFragment.notifyChangedGridData();
                return true;

            case R.id.menu_mediaplayer_info:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void scanMediaPlayer() {
        mede8erCommander.getMoviesManager().clear();
        mede8erCommander.getMusicManager().clear();
        GenericProgressIndicator genericProgressIndicator = new Mede8erScanner(this);
        if (genericProgressIndicator.setup()) {
            new ProgressIndicator().progress("Scanning media player..", genericProgressIndicator);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText.length() == 0) {
            searchView.clearFocus();
        }
        mede8erCommander.getMoviesManager().setGenericFilter(newText);
        updateSearch(newText);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        updateSearch(query);
        searchView.clearFocus();
        return true;
    }

    private void updateSearch(String query) {

        String tabName = getString(R.string.movies_tab);
        if (tabName.equals(actionBar.getSelectedTab().getText())) {
            mede8erCommander.getMoviesManager().setGenericFilter(query);
            currentTabFragment.notifyChangedGridData();
        } else if (tabName.equals(actionBar.getSelectedTab().getText())) {
            mede8erCommander.getMusicManager().setGenericFilter(query);
            currentTabFragment.notifyChangedGridData();
        }


    }

    /**
     * this method is called from the mede8erconnector when has loaded all the data
     */
    public void dataReady() {
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
            currentTabFragment = (TabFragment) fragment;
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            fragmentTransaction.remove(fragment);
        }

    }

    private class Mede8erScannerTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                Looper.prepare();
                Logger.log("Checking Mede8er on the network..");
                if (mede8erCommander.isMede8erConnected()){
                    Logger.log("Mede8er found!!");
                    scanMediaPlayer();
                } else {
                    Logger.log("Mede8er not found!!");
                    dialogHandler.sendMessage(Message.obtain(dialogHandler, Constants.MEDE8ER_NOT_CONNECTED));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return "boh..";
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
}