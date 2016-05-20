package com.sdunning.CommunityCanvas;

import android.os.Bundle;
import android.app.Activity;

public class ChatRoom extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
