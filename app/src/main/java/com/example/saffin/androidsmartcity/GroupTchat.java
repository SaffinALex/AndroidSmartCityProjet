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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroupTchat extends BaseActivity {
    public String gid;
    public List<String> listMessage = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_tchat);
        Intent i = getIntent();
        this.gid = i.getStringExtra("gid");
        TextView title = findViewById(R.id.groupName);
        title.setText(this.gid);
        getAllMessageForChat();

        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

    }

    public void getAllMessageForChat(){
        GroupHelper.getGroupsMessagesCollection(gid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public synchronized void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    listMessage = list;
                    completeRecyclerview(listMessage);

                } else {
                    Log.d("erro", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void onClickSendMessage(View v) {
        EditText text = findViewById(R.id.myMessage);
        GroupHelper.createGroupMessages(this.gid,text.getText().toString(),getCurrentUser().toString());
        Intent i = new Intent(this, GroupTchat.class);
        i.putExtra("gid", this.gid);
        startActivity(i);
    }

    public synchronized void completeRecyclerview(List<String> list){
        final RecyclerView rv = (RecyclerView) findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        String uid = this.getCurrentUser().toString();
        rv.setAdapter(new AdapterMessage(list));
    }
}
