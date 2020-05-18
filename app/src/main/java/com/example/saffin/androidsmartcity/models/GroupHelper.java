package com.example.saffin.androidsmartcity.models;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by Saffin on 02/05/2020.
 */

public class GroupHelper {
    private static final String COLLECTION_NAME = "groups";
    private static final String SUBCOLLECTION_NAME = "groups_messages";
    private static final String SUBCOLLECTION_NAME2 = "groups_users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getGroupsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static CollectionReference getGroupsMessagesCollection(String gid){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(gid).collection(SUBCOLLECTION_NAME);
    }

    public static CollectionReference getGroupsUsersCollection(String gid){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(gid).collection(SUBCOLLECTION_NAME2);
    }

    // --- CREATE ---

    public static Task<Void> createGroup(String gid, String name) {
        Group groupToCreate = new Group(gid, name);
        // 2 - Add a new User Document to Firestore
        return GroupHelper.getGroupsCollection()
                .document(gid) // Setting uID for Document
                .set(groupToCreate); // Setting object for Document
    }
    public static Task<DocumentReference> createGroupUsers(String gid, FirebaseUser user) {
        return GroupHelper.getGroupsUsersCollection(gid)
                .add(user);
    }
    public static void deleteGroupUser(final String gid, final String uid) {
        GroupHelper.getGroupsUsersCollection(gid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> list = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            if(document.get("uid").toString() == uid ) {
                                GroupHelper.getGroupsUsersCollection(gid).document(GroupHelper.getGroupsUsersCollection(gid).getId()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("error", "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("error", "Error deleting document", e);
                                            }
                                        });
                            }

                        }
                    } else {
                        Log.d("hou", "Error getting documents: ", task.getException());
                    }
                }
            });
        }

    public static Task<DocumentReference> createGroupMessages(String gid, String content, String uid) {
        Message m = new Message(content, uid);
        return GroupHelper.getGroupsMessagesCollection(gid)
                .add(m);
    }


    // --- GET ---
    public static Query getAllMessageForChat(String gid){
        return GroupHelper.getGroupsMessagesCollection(gid)
                .orderBy("date")
                .limit(50);
    }



   /* public static Query getUserInGroup(FirebaseUser uid, String gid){
        Query query = getAllUsersForChat(gid);
        return query.whereEqualTo("uid", uid);
    } */

    public static Task<DocumentSnapshot> getGroup(String gid){
        return GroupHelper.getGroupsCollection().document(gid).get();
    }
    public static Task<DocumentSnapshot> getGroupMessages(String gid, String mid){
        return GroupHelper.getGroupsCollection().document(gid).collection(SUBCOLLECTION_NAME).document(mid).get();
    }
    public static Task<DocumentSnapshot> getGroupUsers(String gid, String uid){
        return GroupHelper.getGroupsCollection().document(gid).collection(SUBCOLLECTION_NAME2).document(uid).get();
    }


    // --- UPDATE ---

    public static Task<Void> updateName(String name, String gid) {
        return GroupHelper.getGroupsCollection().document(gid).update("name", name);
    }


    // --- DELETE ---

    public static Task<Void> deleteGroup(String gid) {
        return GroupHelper.getGroupsCollection().document(gid).delete();
    }
}
