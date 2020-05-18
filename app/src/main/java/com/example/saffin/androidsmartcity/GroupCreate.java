package com.example.saffin.androidsmartcity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.saffin.androidsmartcity.auth.BaseActivity;
import com.example.saffin.androidsmartcity.models.GroupHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class GroupCreate extends BaseActivity {
    public List<String> liste = new ArrayList<>();

    public void assignList(List<String> l){
        liste = l;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!this.isCurrentUserLogged()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_group_create);
            this.configureToolBar();

            this.configureDrawerLayout();

            this.configureNavigationView();

            getAllGroupsForChat();
        }
    }

    public Boolean existGroup(String gid) {
        getAllGroupsForChat();
        for(String s : liste){
            if(s.equals(gid)) return true;
        }
        return false;
    }
    public void getAllGroupsForChat(){
        GroupHelper.getGroupsCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    assignList(list);
                } else {
                    Log.d("hou", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    private void createGroupsFireBase(){
        if(this.getCurrentUser() != null){
            EditText groupName = (EditText) findViewById(R.id.editTextGroup);
            String name = groupName.getText().toString();
            if(!existGroup(name)) {
                GroupHelper.createGroup(name, name).addOnFailureListener(this.onFailureListener());
                GroupHelper.createGroupUsers(name, this.getCurrentUser()).addOnFailureListener(this.onFailureListener());
                startActivity(new Intent(this, SocialGroup.class));
            }
        }
    }

    public void onClickCreateGroup(View v){
        createGroupsFireBase();
    }
    public void onClickGoAddUsers(View v){
        addUserGroup();
    }

    private void addUserGroup(){
        if(this.getCurrentUser() != null){
            EditText groupName = (EditText)findViewById(R.id.editTextGroup);
            String name = groupName.getText().toString();
            if(!name.isEmpty()) {
                GroupHelper.createGroupUsers(name, this.getCurrentUser()).addOnFailureListener(this.onFailureListener());
                startActivity(new Intent(this, SocialGroup.class));
            }
        }
    }
}
