package com.agent.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.agent.test.adapter.ItemAdapter;
import com.agent.test.data.DataSource;
import com.agent.test.model.Affirmation;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataSource d = new DataSource();
        List<Affirmation> dataset = d.loadAffirmations();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new ItemAdapter(this, dataset));
        recyclerView.setHasFixedSize(true);
    }
}