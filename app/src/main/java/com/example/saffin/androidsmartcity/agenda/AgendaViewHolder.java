package com.example.saffin.androidsmartcity.agenda;

import android.view.View;
import android.widget.TextView;

import com.example.saffin.androidsmartcity.R;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Th0ma on 28/05/2020
 */
public class AgendaViewHolder extends RecyclerView.ViewHolder {
    private TextView hourView;
    private TextView titleView;
    private TextView detailsView;

    //itemView est la vue correspondante à 1 cellule
    public AgendaViewHolder(View itemView) {
        super(itemView);

        //c'est ici que l'on fait nos findView

        hourView = (TextView) itemView.findViewById(R.id.agenda_cardview_heure);
        titleView = (TextView) itemView.findViewById(R.id.agenda_cardview_titre);
        detailsView = (TextView) itemView.findViewById(R.id.agenda_cardview_details);
    }

    //puis ajouter une fonction pour remplir la cellule en fonction d'un MyObject
    public void bind(Event event){
        hourView.setText(event.getHeures());
        titleView.setText(event.getTitre());
        detailsView.setText(event.getDetails());
    }
}
