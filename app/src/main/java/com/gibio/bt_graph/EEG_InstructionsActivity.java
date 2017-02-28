package com.gibio.bt_graph;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

public class EEG_InstructionsActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eeg__instructions);
        ButtonInit();
    }

    Button bSIGUIENTE;

    void ButtonInit() {
        bSIGUIENTE = (Button) findViewById(R.id.bSIGUIENTE);
        bSIGUIENTE.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bSIGUIENTE:
                startActivity(new Intent("android.intent.action.Grafico"));
                break;

        }
    }
}
