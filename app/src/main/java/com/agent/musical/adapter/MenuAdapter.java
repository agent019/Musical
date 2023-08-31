package com.agent.musical.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.agent.musical.MainActivity;
import com.agent.musical.R;
import com.agent.musical.model.MenuItem;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuItemViewHolder> {
    private List<MenuItem> items;
    private Context mContext;

    public MenuAdapter(Context context, List<MenuItem> items) {
        this.mContext = context;
        this.items = items;
    }

    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

