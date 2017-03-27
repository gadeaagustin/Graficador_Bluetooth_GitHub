package com.gibio.bt_graph;

/**
 * Created by aguus on 22/3/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IngresoActivity extends Activity implements View.OnClickListener{

    private Button bNUEVO;
    private Button bCARGAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);
        ButtonInit();
    }



    void ButtonInit() {
        bNUEVO = (Button) findViewById(R.id.bNUEVO);
        bCARGAR = (Button) findViewById(R.id.bCARGAR);

        bNUEVO.setOnClickListener(this);
        bCARGAR.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bNUEVO:
                startActivity(new Intent("android.intent.action.AnamnesisActivity"));
                break;


        }
    }
}
