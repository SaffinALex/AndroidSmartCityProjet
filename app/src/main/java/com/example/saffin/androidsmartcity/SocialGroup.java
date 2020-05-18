package com.example.saffin.androidsmartcity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.example.saffin.androidsmartcity.auth.BaseActivity;
import com.example.saffin.androidsmartcity.models.GroupHelper;
import com.example.saffin.androidsmartcity.models.MyAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
//import com.google.firebase.firestore.Query;

public class SocialGroup extends BaseActivity {
    public List<List<String>> listeUsers = new ArrayList<>();
    public List<String> listeGroups = new ArrayList<>();
    public List<Pair<List<String>, String>> listUsersByGroup = new ArrayList<>();
    public static int h= 0;

    public void assignListGroups(List<String> l){
        listeGroups = l;
    }
    public void assignListUsers(List<String> l){
        listeUsers.add(l);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!this.isCurrentUserLogged()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            setContentView(R.layout.activity_social_group);
            this.configureToolBar();

            this.configureDrawerLayout();

            this.configureNavigationView();

            getAllGroupsForChat();
            Log.d("houdiess",Integer.toString(h));
        }
    }

    public Boolean existGroup(String gid) {
        getAllGroupsForChat();
        for(String s : listeGroups){
            if(s.equals(gid)) return true;
        }
        return false;
    }


    public void getAllGroupsForChat(){
        GroupHelper.getGroupsCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public synchronized void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    listeGroups = list;
                    h++;
                    completeRecyclerview(listeGroups, listUsersByGroup);

                } else {
                    Log.d("erro", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public synchronized void getAllUsersForChat(String gid){
        GroupHelper.getGroupsUsersCollection(gid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public synchronized void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        list.add(document.get("uid").toString());
                    }
                    listeUsers.add(list);

                } else {
                    Log.d("hou", "Error getting documents: ", task.getException());
                }
            }
        });
    }



   /* public Boolean existUser(String gid, String uid) {
        getAllUsersForChat(gid);
        for(String s : listeUsers){
            if(s.equals(uid)) return true;
        }
        return false;
    }*/
    public synchronized void completeRecyclerview(List<String> list, List<Pair<List<String>, String>> l){
        final RecyclerView rv = (RecyclerView) findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        String uid = this.getCurrentUser().toString();
        rv.setAdapter(new MyAdapter(list, SocialGroup.this, l,uid, getCurrentUser()));
    }



}
