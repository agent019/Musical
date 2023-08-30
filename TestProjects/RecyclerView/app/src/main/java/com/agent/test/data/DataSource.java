package com.agent.test.data;

import com.agent.test.R;
import com.agent.test.model.Affirmation;

import java.util.ArrayList;
import java.util.List;

public class DataSource {
    public List<Affirmation> loadAffirmations() {
        List<Affirmation> affirmations = new ArrayList<Affirmation>();
        affirmations.add(new Affirmation(R.string.affirmation1));
        affirmations.add(new Affirmation(R.string.affirmation2));
        affirmations.add(new Affirmation(R.string.affirmation3));
        affirmations.add(new Affirmation(R.string.affirmation4));
        affirmations.add(new Affirmation(R.string.affirmation5));
        affirmations.add(new Affirmation(R.string.affirmation6));
        affirmations.add(new Affirmation(R.string.affirmation7));
        affirmations.add(new Affirmation(R.string.affirmation8));
        affirmations.add(new Affirmation(R.string.affirmation9));
        affirmations.add(new Affirmation(R.string.affirmation10));
        return affirmations;
    }
}
