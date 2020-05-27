package com.example.saffin.androidsmartcity;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Th0ma on 24/05/2020
 */
public class User implements Serializable {
    private String mFirstName;
    private String mLastName;
    private String mPseudo;
    private String mEmail;
    private String mCity_Name;
    private LatLng mCity_Coordinates;
    private String mURL_Profil_Picture;
    private String mUID;
    private int mAge;

    public User() {
        mFirstName = "Default_FirstName";
        mLastName = "Default_LastName";
        mPseudo = "Default_Pseudo";
        mEmail = "Default@mail.fr";
        mCity_Name = "Montpellier";
        mCity_Coordinates = new LatLng(43.61092,3.87723);
        mURL_Profil_Picture = "Default_URL";
        mUID = "Default_UID";
        mAge = 0;
    }

    public User(String pseudo, String email, String city_Name) {
        mPseudo = pseudo;
        mEmail = email;
        mCity_Name = city_Name;
        mFirstName = "Default_FirstName";
        mLastName = "Default_LastName";
        /** What about city coordinates **/
        mURL_Profil_Picture = "Default_URL";
        mUID = "Default_UID";
        mAge = 0;
    }

    public User(String firstName, String lastName, String pseudo, String email, String city_Name, LatLng city_Coordinates, String URL_Profil_Picture, String UID, int age) {
        mFirstName = firstName;
        mLastName = lastName;
        mPseudo = pseudo;
        mEmail = email;
        mCity_Name = city_Name;
        mCity_Coordinates = city_Coordinates;
        mURL_Profil_Picture = URL_Profil_Picture;
        mUID = UID;
        mAge = age;
    }

    public String serialize() {
        return mFirstName.toString() + ";"
                + mLastName.toString() + ";"
                + mPseudo.toString() + ";"
                + mEmail.toString() + ";"
                + mCity_Name.toString() + ";"
                + mCity_Coordinates.toString() + ";"
                + mURL_Profil_Picture.toString() + ";"
                + mUID.toString() + ";"
                + Integer.toString(mAge) + "\n";
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getPseudo() {
        return mPseudo;
    }

    public void setPseudo(String pseudo) {
        mPseudo = pseudo;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getCity_Name() {
        return mCity_Name;
    }

    public void setCity_Name(String city_Name) {
        mCity_Name = city_Name;
    }

    public LatLng getCity_Coordinates() {
        return mCity_Coordinates;
    }

    public void setCity_Coordinates(LatLng city_Coordinates) {
        mCity_Coordinates = city_Coordinates;
    }

    public String getURL_Profil_Picture() {
        return mURL_Profil_Picture;
    }

    public void setURL_Profil_Picture(String URL_Profil_Picture) {
        mURL_Profil_Picture = URL_Profil_Picture;
    }

    public String getUID() {
        return mUID;
    }

    public void setUID(String UID) {
        mUID = UID;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        mAge = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (mAge != user.mAge) return false;
        if (mFirstName != null ? !mFirstName.equals(user.mFirstName) : user.mFirstName != null)
            return false;
        if (mLastName != null ? !mLastName.equals(user.mLastName) : user.mLastName != null)
            return false;
        if (mPseudo != null ? !mPseudo.equals(user.mPseudo) : user.mPseudo != null) return false;
        if (!mEmail.equals(user.mEmail)) return false;
        if (!mCity_Name.equals(user.mCity_Name)) return false;
        if (mCity_Coordinates != null ? !mCity_Coordinates.equals(user.mCity_Coordinates) : user.mCity_Coordinates != null)
            return false;
        if (mURL_Profil_Picture != null ? !mURL_Profil_Picture.equals(user.mURL_Profil_Picture) : user.mURL_Profil_Picture != null)
            return false;
        return mUID != null ? mUID.equals(user.mUID) : user.mUID == null;
    }

    @Override
    public int hashCode() {
        int result = mFirstName != null ? mFirstName.hashCode() : 0;
        result = 31 * result + (mLastName != null ? mLastName.hashCode() : 0);
        result = 31 * result + (mPseudo != null ? mPseudo.hashCode() : 0);
        result = 31 * result + mEmail.hashCode();
        result = 31 * result + mCity_Name.hashCode();
        result = 31 * result + (mCity_Coordinates != null ? mCity_Coordinates.hashCode() : 0);
        result = 31 * result + (mURL_Profil_Picture != null ? mURL_Profil_Picture.hashCode() : 0);
        result = 31 * result + (mUID != null ? mUID.hashCode() : 0);
        result = 31 * result + mAge;
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "FirstName='" + mFirstName + '\'' +
                ", LastName='" + mLastName + '\'' +
                ", Pseudo='" + mPseudo + '\'' +
                ", Email='" + mEmail + '\'' +
                ", City_Name='" + mCity_Name + '\'' +
                ", City_Coordinates='" + mCity_Coordinates.toString() + '\'' +
                ", URL_Profil_Picture='" + mURL_Profil_Picture + '\'' +
                ", UID='" + mUID + '\'' +
                ", Age=" + mAge +
                '}';
    }
}