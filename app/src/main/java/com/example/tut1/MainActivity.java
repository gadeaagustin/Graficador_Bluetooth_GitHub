package com.example.tut1;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
	static LinearLayout GraphView, GraphView2;
	static GraphView graphView, graphView2;
	static GraphViewSeries Series, Series2;
	//graph value
	private static double graph2LastXValue = 0;
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
					//[LL]:
					for (int i = 0; i < (readBuf.length -4) ; i++) {
						int data_aux[] = new int[]{(int) readBuf[i]&0xff, (int) readBuf[i + 1]&0xff, (int) readBuf[i + 2]&0xff, (int) readBuf[i + 3]&0xff};
						if ((data_aux[0] & 0x80) == 0x80 && (data_aux[1] & 0x80) == 0 && (data_aux[2] & 0x80) == 0 && (data_aux[3] & 0x80) == 0) {
							double data = ProcessData(data_aux);
							i = i + 3;
							Series.appendData(new GraphViewData(graph2LastXValue, data), AutoScrollX);
							//[LL]:
							Series2.appendData(new GraphViewData(graph2LastXValue, data), AutoScrollX);


							//X-axis control
							if (graph2LastXValue >= Xview && Lock == true) {
								Series.resetData(new GraphViewData[]{});
								//[LL]:
								Series2.resetData(new GraphViewData[]{});
								graph2LastXValue = 0;
							} else graph2LastXValue += 0.1;

							if (Lock == true) {
								graphView.setViewPort(0, Xview);
								graphView2.setViewPort(0, Xview);
							} else {
								graphView.setViewPort(graph2LastXValue - Xview, Xview);
								graphView2.setViewPort(graph2LastXValue - Xview, Xview);
							}

							//refresh
							//[LL]

							GraphView.removeView(graphView);
							GraphView.addView(graphView);
							GraphView2.removeView(graphView2);
							GraphView2.addView(graphView2);
						}

						break;
					}
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
		GraphView = (LinearLayout) findViewById(R.id.Graph_1);
		// init example series data------------------- 
		Series = new GraphViewSeries("Signal", 
				new GraphViewStyle(Color.YELLOW, 2),//color and thickness of the line 
				new GraphViewData[] {new GraphViewData(0, 0)});
		graphView = new LineGraphView(  
				this // context  
				, "Graph" // heading  
				);
		graphView.setViewPort(0, Xview);
		graphView.setScrollable(true);
		graphView.setScalable(true);	
		graphView.setShowLegend(true); 
		graphView.setLegendAlign(LegendAlign.BOTTOM);
		graphView.setManualYAxis(true);
		//graphView.setManualYAxisBounds(10000, 0);
		graphView.setManualYAxisBounds(16777216, 0); // 16777216 = 2´24 maximo valor de cuentas a representar por los 24 bits. El fisio manda datos negativos?
		graphView.addSeries(Series); // data
		GraphView.addView(graphView);


		//[LL]
		//init graphview
		GraphView2 = (LinearLayout) findViewById(R.id.Graph_2);
		// init example series data-------------------
		Series2 = new GraphViewSeries("Signa2",
				new GraphViewStyle(Color.RED, 2),//color and thickness of the line
				new GraphViewData[] {new GraphViewData(0, 0)});
		graphView2 = new LineGraphView(
				this // context
				, "Graph" // heading
		);
		graphView2.setViewPort(0, Xview);
		graphView2.setScrollable(true);
		graphView2.setScalable(true);
		graphView2.setShowLegend(true);
		graphView2.setLegendAlign(LegendAlign.BOTTOM);
		graphView2.setManualYAxis(true);
		//graphView.setManualYAxisBounds(10000, 0);
		graphView2.setManualYAxisBounds(16777216, 0);
		graphView2.addSeries(Series2); // data
		GraphView2.addView(graphView2);
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
