package com.shawty.glados.musical;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Created by GLaDOS on 5/23/2016.
 */
public class FilteredMenuFragment extends Fragment {
    private List<MenuItem> itemList;
    private int type;
    private String filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemList = new ArrayList<MenuItem>();
        Bundle b = getArguments();
        type = b.getInt("type");
        filter = b.getString("filter");

        createMenuItemList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        RecyclerView menu = (RecyclerView) view.findViewById(R.id.main_recycler_view);
        menu.setHasFixedSize(true);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        menu.setLayoutManager(manager);

        MenuAdapter adapter = new MenuAdapter(getActivity(), itemList);
        menu.setAdapter(adapter);

        return view;
    }

    //TODO Optimize this, it sucks.
    private void createMenuItemList() {
        MainActivity activity = (MainActivity) getActivity();
        ArrayList<Song> songs = activity.getSongList();

        HashSet<String> items = new HashSet<String>();
        for(int i = 0; i < songs.size(); i++) {
            if(type == MainActivity.ALBUMS) {
                items.add(songs.get(i).getAlbum());
            } else if(type == MainActivity.ARTISTS) {
                items.add(songs.get(i).getArtist());
            } else if(type == MainActivity.GENRES) {
                items.add(songs.get(i).getGenre());
            } else {
                items.add(String.valueOf(songs.get(i).getId()));
            }
        }

        for(String s : items) {
            SongFragment frag = SongFragment.newInstance(type, s);
            MenuItem i = new MenuItem(s, frag);
            itemList.add(i);
        }
    }

    public static FilteredMenuFragment newInstance(int type, String filter) {
        FilteredMenuFragment f = new FilteredMenuFragment();
        Bundle b = new Bundle(2);
        b.putInt("type", type);
        b.putString("filter", filter);
        f.setArguments(b);
        return f;
    }
}
