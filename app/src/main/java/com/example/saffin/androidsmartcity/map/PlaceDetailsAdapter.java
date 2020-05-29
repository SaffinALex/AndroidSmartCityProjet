package com.example.saffin.androidsmartcity.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saffin.androidsmartcity.R;
import com.example.saffin.androidsmartcity.agenda.AgendaViewHolder;
import com.example.saffin.androidsmartcity.agenda.Event;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Th0ma on 29/05/2020
 */
public class PlaceDetailsAdapter extends RecyclerView.Adapter<PlaceDetailsViewHolder>{
    private List<PlaceDetails> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlaceDetailsAdapter(List<PlaceDetails> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlaceDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_details_cardview, parent, false);

        return new PlaceDetailsViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull PlaceDetailsViewHolder holder, int position) {
        PlaceDetails placeDetails = mDataset.get(position);
        holder.bind(placeDetails);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
