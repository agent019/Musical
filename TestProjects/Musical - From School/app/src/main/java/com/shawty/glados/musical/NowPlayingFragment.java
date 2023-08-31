package com.shawty.glados.musical;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by GLaDOS on 6/1/2016.
 */
public class NowPlayingFragment extends Fragment {
    private ArrayList<Song> itemList;
    private int type;
    private String filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        Bundle b = getArguments();
        type = b.getInt("type");
        filter = b.getString("filter");
        ArrayList<Song> temp = activity.getCurrentSongList();
        if(temp != null)
            itemList = temp;
        else
            itemList = new ArrayList<Song>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_fragment, container, false);
        RecyclerView menu = (RecyclerView) view.findViewById(R.id.song_recycler_view);
        menu.setHasFixedSize(true);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        menu.setLayoutManager(manager);

        SongAdapter adapter = new SongAdapter(getActivity(), itemList);
        menu.setAdapter(adapter);

        return view;
    }

    public static NowPlayingFragment newInstance(int type, String filter) {
        NowPlayingFragment f = new NowPlayingFragment();
        Bundle b = new Bundle(2);
        b.putInt("type", type);
        b.putString("filter", filter);
        f.setArguments(b);
        return f;
    }
}
