package com.example.tut1;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

//TODO: [LL]: Obtener el canal implicito en el paquete y graficar segun corresponda

public class MainActivity extends Activity implements View.OnClickListener{

    public static final int MIN_Y_Grap_0 = -8000000, MIN_Y_Grap_1 = -8000000, MAX_Y_Grap_0 = 8000000, MAX_Y_Grap_1 = 8000000;


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (Bluetooth.connectedThread != null) {
			Bluetooth.connectedThread.write("Q");}//Stop streaming
		super.onBackPressed();
	}

	//toggle Button
	static boolean Lock;//whether lock the x-axis to 0-5
	static boolean AutoScrollX;//auto scroll to the last x value
	static boolean Stream;//Start or stop streaming
	//Button init
	Button bXminus;
	Button bXplus;
	ToggleButton tbLock;
	ToggleButton tbScroll;
	ToggleButton tbStream;
	//GraphView init
	static LinearLayout GraphView_0, GraphView_1;
	static GraphView graphView_0, graphView_1;
	static GraphViewSeries Series_0, Series_1;
	//graph value
	private static double graph2LastXValue_0 =0, graph2LastXValue_1 =0;
	private static int Xview=10;
	Button bConnect, bDisconnect;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
				case Bluetooth.SUCCESS_CONNECT:
					Bluetooth.connectedThread = new Bluetooth.ConnectedThread((BluetoothSocket) msg.obj);
					Toast.makeText(getApplicationContext(), "Connected!", 0).show();
					String s = "successfully connected";
					Bluetooth.connectedThread.start();
					break;
				case Bluetooth.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
					for (int i = 0; i < (readBuf.length -4) ; i++) {  //[LL]: Barro el buffer de 1024 bytes
						int data_aux[] = new int[]{(int) readBuf[i]&0xff, (int) readBuf[i + 1]&0xff, (int) readBuf[i + 2]&0xff, (int) readBuf[i + 3]&0xff};
						if ((data_aux[0] & 0x80) == 0x80 && (data_aux[1] & 0x80) == 0 && (data_aux[2] & 0x80) == 0 && (data_aux[3] & 0x80) == 0) {
							double data = ProcessData(data_aux); //Obtengo el valor a graficar segun el formato de los datos
							int channel= (data_aux[0] & 0x38)>>3; //Obtengo el canal a graficar. Mascara= 0x38= 111000b
							i = i + 3;

							switch(channel)
							{
								case 0:
									Series_0.appendData(new GraphViewData(graph2LastXValue_0, data), AutoScrollX);
									//X-axis control
									if (graph2LastXValue_0 >= Xview && Lock == true) {
										Series_0.resetData(new GraphViewData[]{});
										graph2LastXValue_0 = 0;
									} else graph2LastXValue_0 += 0.1;

									if (Lock == true) {
										graphView_0.setViewPort(0, Xview);
									} else {
										graphView_0.setViewPort(graph2LastXValue_0 - Xview, Xview);
									}
									//refresh
									GraphView_0.removeView(graphView_0);
									GraphView_0.addView(graphView_0);
									break;

								case 1:
									Series_1.appendData(new GraphViewData(graph2LastXValue_1, data), AutoScrollX);
									//X-axis control
									if (graph2LastXValue_1 >= Xview && Lock == true) {
										Series_1.resetData(new GraphViewData[]{});
										graph2LastXValue_1 = 0;
									} else graph2LastXValue_1 += 0.1;

									if (Lock == true) {
										graphView_1.setViewPort(0, Xview);
									} else {
										graphView_1.setViewPort(graph2LastXValue_1 - Xview, Xview);
									}
									//refresh
									GraphView_1.removeView(graphView_1);
									GraphView_1.addView(graphView_1);
									break;
							}
						}
					}
					break;
				default:
					break;
			}
        }

		public boolean isFloatNumber(String num) {
			//Log.d("checkfloatNum", num);
			try {
				Double.parseDouble(num);
			} catch (NumberFormatException nfe) {
				return false;
			}
			return true;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//Hide title
		this.getWindow().setFlags(WindowManager.LayoutParams.
				FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//Hide Status bar
		setContentView(R.layout.activity_main);
		//set background color
		LinearLayout background = (LinearLayout)findViewById(R.id.bg);
		background.setBackgroundColor(Color.BLACK);
		init();
		ButtonInit();
	}

	void init(){
		Bluetooth.gethandler(mHandler);

		//init graphview
		GraphView_0 = (LinearLayout) findViewById(R.id.Graph_0);
		// init example series data------------------- 
		Series_0 = new GraphViewSeries("Signal",
				new GraphViewStyle(Color.YELLOW, 2),//color and thickness of the line 
				new GraphViewData[] {new GraphViewData(0, 0)});
		graphView_0 = new LineGraphView(
				this // context  
				, "Graph" // heading  
				);
		graphView_0.setViewPort(0, Xview);
		graphView_0.setScrollable(true);
		graphView_0.setScalable(true);
		graphView_0.setShowLegend(true);
		graphView_0.setLegendAlign(LegendAlign.BOTTOM);
		graphView_0.setManualYAxis(true);
		//graphView_0.setManualYAxisBounds(10000, 0);
		graphView_0.setManualYAxisBounds(MAX_Y_Grap_0, MIN_Y_Grap_0);
		graphView_0.addSeries(Series_0); // data
		GraphView_0.addView(graphView_0);


		//[LL]
		//init graphview
		GraphView_1 = (LinearLayout) findViewById(R.id.Graph_1);
		// init example series data-------------------
		Series_1 = new GraphViewSeries("Signa2",
				new GraphViewStyle(Color.RED, 2),//color and thickness of the line
				new GraphViewData[] {new GraphViewData(0, 0)});
		graphView_1 = new LineGraphView(
				this // context
				, "Graph" // heading
		);
		graphView_1.setViewPort(0, Xview);
		graphView_1.setScrollable(true);
		graphView_1.setScalable(true);
		graphView_1.setShowLegend(true);
		graphView_1.setLegendAlign(LegendAlign.BOTTOM);
		graphView_1.setManualYAxis(true);
		graphView_1.setManualYAxisBounds(MAX_Y_Grap_1, MIN_Y_Grap_1);
		graphView_1.addSeries(Series_1); // data
		GraphView_1.addView(graphView_1);
	}

	void ButtonInit(){
		bConnect = (Button)findViewById(R.id.bConnect);
		bConnect.setOnClickListener(this);
		bDisconnect = (Button)findViewById(R.id.bDisconnect);
		bDisconnect.setOnClickListener(this);
		//X-axis control button
		bXminus = (Button)findViewById(R.id.bXminus);
		bXminus.setOnClickListener(this);
		bXplus = (Button)findViewById(R.id.bXplus);
		bXplus.setOnClickListener(this);
		//
		tbLock = (ToggleButton)findViewById(R.id.tbLock);
		tbLock.setOnClickListener(this);
		tbScroll = (ToggleButton)findViewById(R.id.tbScroll);
		tbScroll.setOnClickListener(this);
		tbStream = (ToggleButton)findViewById(R.id.tbStream);
		tbStream.setOnClickListener(this);
		//init toggleButton
		Lock=true;
		AutoScrollX=true;
		Stream=true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.bConnect:
			startActivity(new Intent("android.intent.action.BT1"));
			break;
		case R.id.bDisconnect:
			Bluetooth.disconnect();
			break;
		case R.id.bXminus:
			if (Xview>1) Xview--;
			break;
		case R.id.bXplus:
			if (Xview<30) Xview++;
			break;
		case R.id.tbLock:
			if (tbLock.isChecked()){
				Lock = true;
			}else{
				Lock = false;
			}
			break;
		case R.id.tbScroll:
			if (tbScroll.isChecked()){
				AutoScrollX = true;
			}else{
				AutoScrollX = false;
			}
			break;
		case R.id.tbStream:
			if (tbStream.isChecked()){
				if (Bluetooth.connectedThread != null)
					Bluetooth.connectedThread.write("E");
			}else{
				if (Bluetooth.connectedThread != null)
					Bluetooth.connectedThread.write("Q");
			}
			break;
		}
	}

    /*
	* ProcessData recibe un array int[] con los 4 bytes del paquete y obtiene de él el valor de 24 bits implicito en el paquete
	*Protocolo de recepción para el Fisioremoto:
	* Formato de los Datos, son 4 bytes:  1,X,CH2,CH1,CH0,b23,b22,b21 | 0,b20,b19,b18,b17,b16,b15,b14 | 0,b13,b12,b11,b10,b9,b8,b7 | 0,b6,b5,b4,b3,b2,b1,b0
	**/
	double ProcessData(int[] data)
	{
		//data[0] parte alta alta
		//data[1] parte alta
		//data[2] parte media
		//data[3] parte baja

		int aux = 0;

		aux = (int)((data[2] << 7) & 0xff);
		data[3] = (int)((data[3] | aux) & 0xff);
		data[2] = (int)((data[2] >> 1) & 0xff);

		aux = (int)((data[1] << 6) & 0xff);
		data[2] = (int)((data[2] | aux) & 0xff);
		data[1] = (int)((data[1] >> 2) & 0xff);

		aux = (int)((data[0] << 5) & 0xff);
		data[1] = (int)(data[1] | aux);

		//UInt32 dato = (UInt32)(data[1] << 16) + (UInt32)(data[2] << 8) + (UInt32)(data[3]);
		int neg=0x00;
		if ((data[1] & 0x80) == 0x80)
			neg = 0xFF;
		int dato = (neg << 24) + (data[1] << 16) + (data[2] << 8) + (data[3]);

		//return Convert.ToDouble(dato) / Math.Pow(2, 23) * SCALE;
        return (double)dato;
	}

}
