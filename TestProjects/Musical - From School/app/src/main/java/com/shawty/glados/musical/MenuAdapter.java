package com.shawty.glados.musical;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by GLaDOS on 5/19/2016.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuItemViewHolder> {
    private List<MenuItem> items;
    private Context mContext;

    public MenuAdapter(Context context, List<MenuItem> items) {
        this.mContext = context;
        this.items = items;
    }

    @Override
    public MenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_view, parent, false);
        MenuItemViewHolder menuItemViewHolder = new MenuItemViewHolder(view);
        return menuItemViewHolder;
    }

    @Override
    public void onBindViewHolder(MenuItemViewHolder holder, int position) {
        holder.setMenuItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    class MenuItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MenuItem menuItem;
        private TextView tv;

        public MenuItemViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.menu_item_text);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        public void setMenuItem(MenuItem m) {
            this.menuItem = m;
            this.tv.setText(m.getText());
        }

        @Override
        public void onClick(View v) {
            Fragment frag = menuItem.getFragment();
            if(frag != null) {
                if(mContext instanceof MainActivity) {
                    ((MainActivity) mContext).swapFragment(frag);
                }
            }
        }
    }
}


