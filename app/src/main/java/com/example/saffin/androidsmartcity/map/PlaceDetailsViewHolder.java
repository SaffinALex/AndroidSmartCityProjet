package com.example.saffin.androidsmartcity.map;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.saffin.androidsmartcity.R;
import com.example.saffin.androidsmartcity.agenda.Event;

import java.net.URI;
import java.net.URISyntaxException;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Th0ma on 29/05/2020
 */
public class PlaceDetailsViewHolder extends RecyclerView.ViewHolder {
    private ImageView mImageView;
    private TextView nom;
    private TextView adresse;
    private TextView notation;
    private TextView horaires;
    private TextView telephone;
    private TextView site_web_url;

    public PlaceDetailsViewHolder(@NonNull View itemView) {
        super(itemView);

        mImageView = (ImageView) itemView.findViewById(R.id.places_details_cardview_image);
        nom = (TextView) itemView.findViewById(R.id.places_details_cardview_nom);

        System.out.println("NOM DE NOM DANS LE CONSTRUCTEUR ? " + (nom == null));
        adresse = (TextView) itemView.findViewById(R.id.places_details_cardview_adresse);
        notation = (TextView) itemView.findViewById(R.id.places_details_cardview_notation);
        horaires = (TextView) itemView.findViewById(R.id.places_details_cardview_horaires);
        telephone = (TextView) itemView.findViewById(R.id.places_details_cardview_telephone);
        site_web_url = (TextView) itemView.findViewById(R.id.places_details_cardview_site_web);
        //site_web_url.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //puis ajouter une fonction pour remplir la cellule en fonction d'un MyObject
    public void bind(PlaceDetails placeDetails){
        nom.setText(placeDetails.getNom());

        adresse.setText("Adresse: " + placeDetails.getAdresse());
        horaires.setText("Horaires: \n" + placeDetails.getHoraires());
        notation.setText("Notation: " + placeDetails.getNotation() + " sur 5.0");
        telephone.setText("Téléphone: " + placeDetails.getTelephone());
        site_web_url.setText("Site: " + Html.fromHtml(placeDetails.getSite_web()));
    }
}
