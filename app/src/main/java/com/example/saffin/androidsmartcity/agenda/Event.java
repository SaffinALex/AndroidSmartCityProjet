package com.example.saffin.androidsmartcity.agenda;

import java.io.Serializable;

/**
 * Created by Th0ma on 28/05/2020
 */
public class Event implements Serializable {
    private String jour;
    private String heures;
    private String titre;
    private String details;

    public Event(String [] data) throws Error{
        if(data.length == 4){
            jour = data[0];
            heures = data[1];
            titre = data[2];
            details = data[3];
        }
        else{
            throw new Error("Trying To Use Event Constructor Without The Right Amount Of Data");
        }
    }

    public Event(String jour, String heures, String titre, String details) {
        this.jour = jour;
        this.heures = heures;
        this.titre = titre;
        this.details = details;
    }

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }

    public String getHeures() {
        return heures;
    }

    public void setHeures(String heures) {
        this.heures = heures;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (!heures.equals(event.heures)) return false;
        if (!titre.equals(event.titre)) return false;
        return details != null ? details.equals(event.details) : event.details == null;
    }

    @Override
    public int hashCode() {
        int result = heures.hashCode();
        result = 31 * result + titre.hashCode();
        result = 31 * result + (details != null ? details.hashCode() : 0);
        return result;
    }

    public String serialize(){
        return jour + ";" + heures + ";" + titre + ";" + details + "\n";
    }

    @Override
    public String toString() {
        return "Event{" +
                "jour='" + jour + '\'' +
                ", heure='" + heures + '\'' +
                ", titre='" + titre + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
