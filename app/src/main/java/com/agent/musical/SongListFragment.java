package com.agent.musical;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agent.musical.adapter.SongListAdapter;
import com.agent.musical.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongListFragment extends Fragment {
    private List<Song> itemList;
    private int type;
    private String filter;

    public SongListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param type Parameter 1.
     * @param filter Parameter 2.
     * @return A new instance of fragment SongFragment.
     */
    public static SongListFragment newInstance(int type, String filter) {
        SongListFragment fragment = new SongListFragment();
        Bundle b = new Bundle(2);
        b.putInt("type", type);
        b.putString("filter", filter);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        Bundle b = getArguments();
        type = b.getInt("type");
        filter = b.getString("filter");
        List<Song> fullList = activity.getSongList();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        RecyclerView menu = (RecyclerView) view.findViewById(R.id.song_recycler_view);
        menu.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        menu.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(menu.getContext(),
                layoutManager.getOrientation());
        menu.addItemDecoration(dividerItemDecoration);

        SongListAdapter adapter = new SongListAdapter(getActivity(), itemList);
        menu.setAdapter(adapter);

        return view;
    }
}