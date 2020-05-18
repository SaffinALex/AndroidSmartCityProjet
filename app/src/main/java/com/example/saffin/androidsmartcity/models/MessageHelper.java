package com.example.saffin.androidsmartcity.models;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Saffin on 02/05/2020.
 */

public class MessageHelper {
    private static final String COLLECTION_NAME = "messages";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getMessagesCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createMessage(String mid, String date, String message, String uid) {
        Message messageToCreate = new Message(mid, date, message, uid);
        // 2 - Add a new User Document to Firestore
        return MessageHelper.getMessagesCollection()
                .document(mid) // Setting uID for Document
                .set(messageToCreate); // Setting object for Document
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getMessage(String mid){
        return MessageHelper.getMessagesCollection().document(mid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateDate(String date, String mid) {
        return MessageHelper.getMessagesCollection().document(mid).update("date", date);
    }
    public static Task<Void> updateMessage(String message, String mid) {
        return MessageHelper.getMessagesCollection().document(mid).update("message", message);
    }
    public static Task<Void> updateUid(String uid, String mid) {
        return MessageHelper.getMessagesCollection().document(mid).update("uid", uid);
    }


    // --- DELETE ---

    public static Task<Void> deleteMessage(String mid) {
        return MessageHelper.getMessagesCollection().document(mid).delete();
    }
}
