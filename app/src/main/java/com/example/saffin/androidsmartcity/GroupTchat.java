package com.example.saffin.androidsmartcity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.saffin.androidsmartcity.auth.BaseActivity;
import com.example.saffin.androidsmartcity.models.AdapterMessage;
import com.example.saffin.androidsmartcity.models.GroupHelper;
import com.example.saffin.androidsmartcity.models.UserHelper;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroupTchat extends BaseActivity {
    public String gid;
    public List<String> listMessage = new ArrayList<>();
    public List<String> listDate = new ArrayList<>();
    public List<String> listUser = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_tchat);
        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();
        Intent i = getIntent();
        this.gid = i.getStringExtra("gid");
        TextView title = findViewById(R.id.groupName);
        title.setText(this.gid);
        getAllMessageForChat();




    }

    public void getAllMessageForChat(){
        GroupHelper.getGroupsMessagesCollection(gid).orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public synchronized void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    List<String> dates = new ArrayList<>();
                    List<String> users = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        list.add(document.get("message").toString());
                        dates.add(document.get("date").toString());
                        users.add(document.get("uid").toString());
                    }
                  /*  for(int i = 0; i<list.size();i++){
                        List<String> tmp1 = new ArrayList<>();
                        List<String> tmp2 = new ArrayList<>();
                        List<String> tmp3 = new ArrayList<>();
                        tmp1.add(list.get(list.size()-i-1));
                        tmp2.add(dates.get(list.size()-i-1));
                        tmp3.add(users.get(list.size()-i-1));
                    }*/
                    listMessage = list;
                    listDate = dates;
                    listUser = users;
                    completeRecyclerview(listMessage, listDate, listUser);

                } else {
                    Log.d("erro", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    public void goMessage(){
        Intent i = new Intent(this, GroupTchat.class);
        i.putExtra("gid", this.gid);
        finish();
        startActivity(i);
    }
    public void onClickSendMessage(View v) {
        final String uid = this.getCurrentUser().getUid();
        final String[] firstName = {"null"};
        UserHelper.getUsersCollection().document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public synchronized void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.getId().equals(uid)) {
                        firstName[0] = document.get("firstName").toString();
                    }
                    EditText text = findViewById(R.id.myMessage);
                    Date now = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY HH:mm");
                    GroupHelper.createGroupMessages(gid, text.getText().toString(), firstName[0], formatter.format(now));
                    goMessage();
                } else {
                    Log.d("erro", "Error getting documents: ");
                }
            }
        });
    }

    public synchronized void completeRecyclerview(List<String> list, List<String> dates,List<String> users){
        final RecyclerView rv = (RecyclerView) findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        String uid = this.getCurrentUser().toString();
        rv.setAdapter(new AdapterMessage(list,dates,users));
    }
}
