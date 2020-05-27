package com.example.saffin.androidsmartcity;

import com.google.firebase.firestore.QuerySnapshot;

interface GroupChatCallBack {
    void onListGroup(QuerySnapshot q);
}
