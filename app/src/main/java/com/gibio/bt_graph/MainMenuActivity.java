package com.gibio.bt_graph;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ButtonInit();
    }

    Button bEEG;

    void ButtonInit() {
        bEEG = (Button) findViewById(R.id.bEEG);
        bEEG.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bEEG:
                startActivity(new Intent("android.intent.action.EEG_Instructions"));
                break;

        }
    }
}