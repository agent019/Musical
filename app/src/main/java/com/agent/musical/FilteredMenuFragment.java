package com.agent.musical;

import com.agent.musical.adapter.MenuAdapter;
import com.agent.musical.model.MenuItem;
import com.agent.musical.model.Song;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilteredMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilteredMenuFragment extends Fragment {
    private List<MenuItem> itemList;
    private int type;
    private String filter;

    public FilteredMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FilteredMenuFragment.
     */
    public static FilteredMenuFragment newInstance(int type, String filter) {
        FilteredMenuFragment fragment = new FilteredMenuFragment();
        Bundle b = new Bundle(2);
        b.putInt("type", type);
        b.putString("filter", filter);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemList = new ArrayList<MenuItem>();
        Bundle b = getArguments();
        type = b.getInt("type");
        filter = b.getString("filter");

        createMenuItemList();
    }

    //TODO Optimize this, it sucks.
    private void createMenuItemList() {
        MainActivity activity = (MainActivity) getActivity();
        List<Song> songs = activity.getSongList();

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
            SongListFragment frag = SongListFragment.newInstance(type, s);
            MenuItem i = new MenuItem(s, frag);
            itemList.add(i);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filtered_menu, container, false);
        RecyclerView menu = (RecyclerView) view.findViewById(R.id.main_recycler_view);
        menu.setHasFixedSize(true);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        menu.setLayoutManager(manager);

        MenuAdapter adapter = new MenuAdapter(getActivity(), itemList);
        menu.setAdapter(adapter);

        return view;
    }
}