package com.agent.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agent.test.R;
import com.agent.test.model.Affirmation;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    public Context context;
    public List<Affirmation> dataset;

    public ItemAdapter(Context context, List<Affirmation> dataset)
    {
        this.context = context;
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Affirmation aff = dataset.get(position);
        holder.textView.setText(context.getResources().getString(aff.resId));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView;
        public ItemViewHolder(View itemView)
        {
            super(itemView);
            textView = itemView.findViewById(R.id.item_title);
        }
    }
}
