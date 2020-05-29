package com.example.saffin.androidsmartcity.map;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Th0ma on 29/05/2020
 */
public class PlaceDetails implements Serializable {
    private String _id;
    private String nom;
    private String adresse;
    private String horaires;
    private String notation;
    private String site_web;
    private String telephone;
    private String image_url;

    public PlaceDetails(List<String> params){
        this._id = params.get(0);
        this.nom = params.get(1);
        this.adresse = params.get(2);
        this.horaires = params.get(3);
        this.notation = params.get(4);
        this.site_web = params.get(5);
        this.telephone = params.get(6);
        this.image_url = params.get(7);
    }

    public PlaceDetails(String [] params){
        this._id = params[0];
        this.nom = params[1];
        this.adresse = params[2];
        this.horaires = params[3];
        this.notation = params[4];
        this.site_web = params[5];
        this.telephone = params[6];
        this.image_url = params[7];
    }

    public PlaceDetails(String _id, String nom, String adresse, String horaires, String notation, String site_web, String telephone, String image_url) {
        this._id = _id;
        this.nom = nom;
        this.adresse = adresse;
        this.horaires = horaires;
        this.notation = notation;
        this.site_web = site_web;
        this.telephone = telephone;
        this.image_url = image_url;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getHoraires() {
        return horaires;
    }

    public void setHoraires(String horaires) {
        this.horaires = horaires;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getSite_web() {
        return site_web;
    }

    public void setSite_web(String site_web) {
        this.site_web = site_web;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public String toString() {
        return "PlaceDetails{" +
                "_id='" + _id + '\'' +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", horaires='" + horaires + '\'' +
                ", notation='" + notation + '\'' +
                ", site_web='" + site_web + '\'' +
                ", telephone='" + telephone + '\'' +
                ", image_url='" + image_url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlaceDetails that = (PlaceDetails) o;

        if (!_id.equals(that._id)) return false;
        if (!nom.equals(that.nom)) return false;
        if (!adresse.equals(that.adresse)) return false;
        if (horaires != null ? !horaires.equals(that.horaires) : that.horaires != null)
            return false;
        if (notation != null ? !notation.equals(that.notation) : that.notation != null)
            return false;
        if (site_web != null ? !site_web.equals(that.site_web) : that.site_web != null)
            return false;
        if (telephone != null ? !telephone.equals(that.telephone) : that.telephone != null)
            return false;
        return image_url != null ? image_url.equals(that.image_url) : that.image_url == null;
    }

    @Override
    public int hashCode() {
        int result = _id.hashCode();
        result = 31 * result + nom.hashCode();
        result = 31 * result + adresse.hashCode();
        result = 31 * result + (horaires != null ? horaires.hashCode() : 0);
        result = 31 * result + (notation != null ? notation.hashCode() : 0);
        result = 31 * result + (site_web != null ? site_web.hashCode() : 0);
        result = 31 * result + (telephone != null ? telephone.hashCode() : 0);
        result = 31 * result + (image_url != null ? image_url.hashCode() : 0);
        return result;
    }
}
