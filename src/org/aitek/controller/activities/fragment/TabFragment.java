package org.aitek.controller.activities.fragment;

import android.app.Fragment;
import android.widget.GridView;
import android.widget.ListView;
import org.aitek.controller.ui.ImageAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/6/13
 * Time: 4:05 PM
 */
public abstract class TabFragment extends Fragment {

    protected GridView gridView;
    protected ImageAdapter imageAdapter;
    protected ListView genresListView;

    public void notifyChangedGridData() {
        imageAdapter.notifyDataSetChanged();
    }


}
