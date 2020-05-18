package com.example.saffin.androidsmartcity.models;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Saffin on 29/04/2020.
 */

public class UserHelper {
    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture, String age, String firstName, String secondName) {
        User userToCreate = new User(uid, username, firstName, secondName, urlPicture, age);
        Log.d("coucou", "coucou");
        // 2 - Add a new User Document to Firestore
        return UserHelper.getUsersCollection()
                .document(uid) // Setting uID for Document
                .set(userToCreate); // Setting object for Document
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }
    public static Task<Void> updateAge(String age, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("age", age);
    }
    public static Task<Void> updateSecondName(String username, String uid){
        return UserHelper.getUsersCollection().document(uid).update("secondName", username);
    }
    public static Task<Void> updateFirstName(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("firstName", username);
    }
    public static Task<Void> updateMail(String mail, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("mail", mail);
    }


    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}
