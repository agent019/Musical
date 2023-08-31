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
import java.util.List;

/**
 * Created by GLaDOS on 5/19/2016.
 */
public class MainFragment extends Fragment {
    private List<MenuItem> itemList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemList = new ArrayList<MenuItem>();
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

    private void createMenuItemList() {
        Fragment songFrag = SongFragment.newInstance(MainActivity.ALL, null);
        MenuItem song = new MenuItem("Songs", songFrag);
        itemList.add(song);
        Fragment artistFrag = FilteredMenuFragment.newInstance(MainActivity.ARTISTS, null);
        MenuItem artist = new MenuItem("Artists", artistFrag);
        itemList.add(artist);
        Fragment albumFrag = FilteredMenuFragment.newInstance(MainActivity.ALBUMS, null);
        MenuItem album = new MenuItem("Albums", albumFrag);
        itemList.add(album);
        Fragment genreFrag = FilteredMenuFragment.newInstance(MainActivity.GENRES, null);
        MenuItem genre = new MenuItem("Genres", genreFrag);
        itemList.add(genre);
        Fragment nowPlayingFrag = NowPlayingFragment.newInstance(MainActivity.ALL, null);
        MenuItem nowPlaying = new MenuItem("Now Playing", nowPlayingFrag);
        itemList.add(nowPlaying);
    }

    public static MainFragment newInstance() {
        MainFragment f = new MainFragment();
        return f;
    }
}
