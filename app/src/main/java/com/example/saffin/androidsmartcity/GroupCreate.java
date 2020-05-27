package com.example.saffin.androidsmartcity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
                    boolean t = false;
                    EditText groupName = (EditText) findViewById(R.id.editTextGroup);
                    String name = groupName.getText().toString();
                    for(String s : list){
                        if(s.equals(name)) t=true;
                    }
                    if(!t && (!name.isEmpty()) && name.length()<=20) {
                        goAddGroup(name);
                    }
                    else if(name.length()>=20){
                        Toast.makeText(GroupCreate.this.getApplicationContext(),
                                "Le nom est trop long (>20 caractéres)",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(GroupCreate.this.getApplicationContext(),
                                "Le groupe existe déjà",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("hou", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    private void goAddGroup(String name){
        GroupHelper.createGroup(name, name).addOnFailureListener(this.onFailureListener());
        GroupHelper.createGroupUsers(name, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
        startActivity(new Intent(this, SocialGroup.class));
    }
    private void createGroupsFireBase(){
        if(this.getCurrentUser() != null){
            getAllGroupsForChat();
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
                GroupHelper.createGroupUsers(name, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
                startActivity(new Intent(this, SocialGroup.class));
            }
        }
    }
}
