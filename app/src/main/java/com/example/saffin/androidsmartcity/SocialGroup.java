package com.example.saffin.androidsmartcity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.example.saffin.androidsmartcity.auth.BaseActivity;
import com.example.saffin.androidsmartcity.models.GroupHelper;
import com.example.saffin.androidsmartcity.models.MyAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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


            getAllGroupsForChat(new GroupChatCallBack() {
                @Override
                public void onListGroup(QuerySnapshot q) {
                    List<String> list = new ArrayList<>();
                    List<String> listUsers = new ArrayList<>();
                    for (DocumentSnapshot document : q) {
                        list.add(document.getId());
                        listUsers = (List<String>) document.get("users");
                        listUsersByGroup.add(new Pair<List<String>, String>(listUsers, document.getId()));
                    }
                    listeGroups = list;
                    completeRecyclerview(listeGroups, listUsersByGroup);
                }
            });
        }
    }

    public Boolean existGroup(String gid) {
        //getAllGroupsForChat();
        for(String s : listeGroups){
            if(s.equals(gid)) return true;
        }
        return false;
    }


    public void getAllGroupsForChat(final GroupChatCallBack callback){
        GroupHelper.getGroupsCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    callback.onListGroup(task.getResult());
                } else {
                    Log.d("erro", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public synchronized void completeRecyclerview(List<String> list, List<Pair<List<String>, String>> l){
        final RecyclerView rv = (RecyclerView) findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        String uid = this.getCurrentUser().getUid();
        rv.setAdapter(new MyAdapter(list, SocialGroup.this, l,uid, getCurrentUser()));
    }



}
