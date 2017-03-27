package com.gibio.bt_graph;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends Activity implements View.OnClickListener{

    private Button bEEG;
    private Button bANAMNESIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ButtonInit();
    }



    void ButtonInit() {
        bEEG = (Button) findViewById(R.id.bEEG);
        bANAMNESIS = (Button) findViewById(R.id.bANAMNESIS);

        bEEG.setOnClickListener(this);
        bANAMNESIS.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bEEG:
                startActivity(new Intent("android.intent.action.EEG_Instructions"));
                break;

            case R.id.bANAMNESIS:
                startActivity(new Intent("android.intent.action.IngresoActivity"));
                break;

        }
    }
}