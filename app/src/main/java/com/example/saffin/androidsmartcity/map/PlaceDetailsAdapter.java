package com.example.saffin.androidsmartcity.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saffin.androidsmartcity.R;
import com.example.saffin.androidsmartcity.agenda.AgendaViewHolder;
import com.example.saffin.androidsmartcity.agenda.Event;

import java.util.List;

/**
 * Created by Th0ma on 29/05/2020
 */
public class MapAdapter {
    private List<PlaceDetails> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MapAdapter(List<PlaceDetails> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AgendaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.agenda_cardview, parent, false);

        return new AgendaViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AgendaViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Event event = mDataset.get(position);
        holder.bind(event);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
