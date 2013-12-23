package org.aitek.controller.activities.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.aitek.controller.R;
import org.aitek.controller.activities.MusicDetailActivity;
import org.aitek.controller.core.MusicManager;
import org.aitek.controller.mede8er.Mede8erCommander;

public class MusicFragment extends TabFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout relativeLayout = (RelativeLayout)inflater.inflate(R.layout.music_main, container, false);
//
//        final MusicManager musicManager = Mede8erCommander.getInstance(getActivity()).getMusicManager();
//        ArrayAdapter adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, musicManager.getGenres());
//
//        genresListView = (ListView) relativeLayout.findViewById(R.id.musicListView);
//        genresListView.setAdapter(adapter);
//        genresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                musicManager.setGenreFilter(genresListView.getAdapter().getItem(i).toString());
//                imageAdapter.notifyDataSetChanged();
//            }
//        });
//
//        gridView = (GridView) relativeLayout.findViewById(R.id.musicGridView);
//        gridView.setAdapter(imageAdapter);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//
//                Intent fullScreenIntent = new Intent(v.getContext(), MusicDetailActivity.class);
//                fullScreenIntent.putExtra(MusicDetailActivity.class.getName(), position);
//                startActivity(fullScreenIntent);
//            }
//        });
//
        return relativeLayout;
    }

}