package com.agent.musical;

import com.agent.musical.adapter.MenuAdapter;
import com.agent.musical.model.MenuItem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private List<MenuItem> itemList;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemList = new ArrayList<MenuItem>();
        createMenuItemList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
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
        // Fragment nowPlayingFrag = NowPlayingFragment.newInstance(MainActivity.ALL, null);
        // MenuItem nowPlaying = new MenuItem("Now Playing", nowPlayingFrag);
        // itemList.add(nowPlaying);
    }
}