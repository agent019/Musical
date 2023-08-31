package com.shawty.glados.musical;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GLaDOS on 5/19/2016.
 */
public class SongFragment extends Fragment {
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
        ArrayList<Song> fullList = activity.getSongList();
        if(filter != null) {
            itemList = new ArrayList<Song>();
            for (int i = 0; i < fullList.size(); i++) {
                if(type == MainActivity.ALBUMS) {
                    if(fullList.get(i).getAlbum().equals(filter))
                        itemList.add(fullList.get(i));
                } else if(type == MainActivity.ARTISTS) {
                    if(fullList.get(i).getArtist().equals(filter))
                        itemList.add(fullList.get(i));
                } else if(type == MainActivity.GENRES) {
                    if(fullList.get(i).getGenre().equals(filter))
                        itemList.add(fullList.get(i));
                }
            }
        } else {
            itemList = fullList;
        }
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

    public static SongFragment newInstance(int type, String filter) {
        SongFragment f = new SongFragment();
        Bundle b = new Bundle(2);
        b.putInt("type", type);
        b.putString("filter", filter);
        f.setArguments(b);
        return f;
    }
}
