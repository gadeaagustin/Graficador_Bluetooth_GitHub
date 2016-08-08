package com.example.tut1;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends Activity implements View.OnClickListener {
	double Data_Graph_0 = 0, Data_Graph_1 = 0;

	public static final int MIN_Y_Grap_0 = -8000000, MIN_Y_Grap_1 = -8000000, MAX_Y_Grap_0 = 8000000, MAX_Y_Grap_1 = 8000000;
	SimpleFileDialog FileSaveDialog;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (Bluetooth.connectedThread != null) {
			Bluetooth.connectedThread.write("Q");
		}//Stop streaming
		super.onBackPressed();
	}

	//toggle Button
	static boolean Lock;//whether lock the x-axis to 0-5
	static boolean AutoScrollX;//auto scroll to the last x value
	static boolean Stream;//Start or stop streaming
	static boolean Record; //Enable Data recording when set to true
	//Button init
	Button bXminus;
	Button bXplus;
	ToggleButton tbLock;
	ToggleButton tbScroll;
	ToggleButton tbStream;
	ToggleButton tbRecord;
	//GraphView init
	//static LinearLayout GraphView_0, GraphView_1;
	static GraphView graphView_0, graphView_1;
	//static GraphViewSeries Series_0, Series_1;
	LineGraphSeries<DataPoint> Series_0, Series_1;
	//graph value
	private static double graph2LastXValue_0 = 0, graph2LastXValue_1 = 0;
	private static double Xview = 20; //60
	private static boolean DataToGraph= false;
	Button bConnect, bDisconnect;

	@Override
	protected void onResume() {
		super.onResume();
		// we're going to simulate real time with thread that append data to the graph

			new Thread(new Runnable() {

				@Override
				public void run() {
					if(DataToGraph) {
						DataToGraph= false;
						// add 1000 new entries
						for (int i = 0; i < 1000; i++) {
							if (i == 9) {
								i = 0;
							}
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									addEntry();
								}
							});

							// sleep to slow down the add of entries
							try {
								Thread.sleep(300);

							} catch (InterruptedException e) {
								// manage error ...
								Toast.makeText(getApplicationContext(), "Error en Thread!", 0).show();
							}
						}
					}
					onResume();
				}
			}).start();
		}

	// add random data to graph
	private void addEntry() {
		// here, we choose to display max 40 points on the viewport and we scroll to end
		Series_0.appendData(new DataPoint(graph2LastXValue_0, Data_Graph_0), AutoScrollX, 1000);
		Series_1.appendData(new DataPoint(graph2LastXValue_1, Data_Graph_1), AutoScrollX, 1000);
	}


/*
		private final Handler mHandler_Graph = new Handler();
		private Runnable mTimer1;
		private Runnable mTimer2;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(
					getArguments().getInt(MainActivity.ARG_SECTION_NUMBER));
		}

		@Override
		public void onResume() {
			super.onResume();

			mTimer1 = new Runnable() {
				@Override
				public void run() {
					graph2LastXValue_0 += 1d;
					Series_0.appendData(new DataPoint(graph2LastXValue_0, Data_Graph_0), AutoScrollX, 40);
					mHandler_Graph.postDelayed(this, 300);
				}
			};
			mHandler_Graph.postDelayed(mTimer1, 300);

			mTimer2 = new Runnable() {
				@Override
				public void run() {
					graph2LastXValue_1 += 1d;
					Series_1.appendData(new DataPoint(graph2LastXValue_1, Data_Graph_1), AutoScrollX, 40);
					mHandler_Graph.postDelayed(this, 200);
				}
			};
			mHandler_Graph.postDelayed(mTimer2, 1000);
		}

		@Override
		public void onPause() {
			mHandler_Graph.removeCallbacks(mTimer1);
			mHandler_Graph.removeCallbacks(mTimer2);
			super.onPause();
		}
	*/

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

							if(Record==true) {
								String DataWrite = null;;
								try {
									DataWrite = new String(readBuf, i, 4, "ISO-8859-1"); //"ISO-8859-1" encoding para que tome correctamente los extended ascii
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
								String DirToSaveType;
								if (FileSaveDialog.Get_m_dir().equals("") ==false)
									DirToSaveType= FileSaveDialog.Get_m_dir();
								else DirToSaveType= FileSaveDialog.Get_m_sdcardDirectory();
								SaveData(DataWrite,DirToSaveType ,FileSaveDialog.Get_Selected_File_Name()); //Guardo los datos en un archivo
							}
							double data = ProcessData(data_aux); //Obtengo el valor a graficar segun el formato de los datos
							int channel= (data_aux[0] & 0x38)>>3; //Obtengo el canal a graficar. Mascara= 0x38= 111000b
							i = i + 3;

							//SaveData(Double.toString(data) ,"Datos.txt"); //Guardo los datos en un archivo
							DataToGraph=true;
							switch(channel)
							{
								case 0:
									Data_Graph_0 = data;

									//Series_0.appendData(new DataPoint(graph2LastXValue_0, data), AutoScrollX, 40);
									//X-axis control
									if (graph2LastXValue_0 >= Xview && Lock == true) {
										Series_0.resetData(new DataPoint[]{});
										graph2LastXValue_0 = 0;
									} else graph2LastXValue_0 += 0.1;

									if (Lock == true) {
										graphView_0.getViewport().setMinX(0);
										graphView_0.getViewport().setMaxX(Xview);
									} else {
										graphView_0.getViewport().setMinX(graph2LastXValue_0);
										graphView_0.getViewport().setMaxX(Xview + graph2LastXValue_0);
									}

									break;

								case 1:
									Data_Graph_1 = data;

									//Series_1.appendData(new DataPoint(graph2LastXValue_1, data), AutoScrollX, 40);

									//X-axis control
									if (graph2LastXValue_1 >= Xview && Lock == true) {
										Series_1.resetData(new DataPoint[]{});
										graph2LastXValue_1 = 0;
									} else
										graph2LastXValue_1 += 0.1;

									if (Lock == true) {
										graphView_1.getViewport().setMinX(0);
										graphView_1.getViewport().setMaxX(Xview);
									} else {
										graphView_1.getViewport().setMinX(graph2LastXValue_1);
										graphView_1.getViewport().setMaxX(Xview + graph2LastXValue_1);
									}


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
		graphView_0 = (GraphView) findViewById(R.id.graphView_0);
		Series_0 = new LineGraphSeries<DataPoint>(new DataPoint[]{
				new DataPoint(0, 0),
		});
		// styling series
		Series_0.setTitle("EEG");
		Series_0.setColor(Color.YELLOW);
		//Series_0.setDrawDataPoints(true);
		//Series_0.setDataPointsRadius(10);
		Series_0.setThickness(4);
		graphView_0.getLegendRenderer().setVisible(true);
		graphView_0.getViewport().setScrollable(true);      // enable scrolling
		graphView_0.getViewport().setScalable(true);        // enable scaling
		graphView_0.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
		graphView_0.getGridLabelRenderer().setHorizontalLabelsVisible(true);
		graphView_0.getGridLabelRenderer().setVerticalLabelsVisible(true);
		graphView_0.getViewport().setXAxisBoundsManual(true);
		graphView_0.getViewport().setMinX(0);
		graphView_0.getViewport().setMaxX(Xview);
		graphView_0.addSeries(Series_0);

		// init example series data-------------------

		/*
		Series_0 = new GraphViewSeries("Signal",
				new GraphViewStyle(Color.YELLOW, 2),//color and thickness of the line
				new DataPoint[] {new DataPoint(0, 0)});
		graphView_0 = new LineGraphView(
				this // context
				, "Graph" // heading
				);
		graphView_0.setViewPort(0, Xview);
		//graphView_0.setManualYAxis(true);
		//graphView_0.setManualYAxisBounds(10000, 0);
		//graphView_0.setManualYAxisBounds(MAX_Y_Grap_0, MIN_Y_Grap_0);
*/

		graphView_1 = (GraphView) findViewById(R.id.graphView_1);
		Series_1 = new LineGraphSeries<DataPoint>(new DataPoint[]{
				new DataPoint(0, 0),
		});
		// styling series
		Series_1.setTitle("EOG");
		Series_1.setColor(Color.RED);
		//Series_1.setDrawDataPoints(true);
		//Series_1.setDataPointsRadius(10);
		Series_1.setThickness(4);
		graphView_1.getViewport().setScrollable(true);      // enable scrolling
		graphView_1.getViewport().setScalable(true);        // enable scaling
		graphView_1.getLegendRenderer().setVisible(true);
		graphView_1.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
		graphView_1.getGridLabelRenderer().setHorizontalLabelsVisible(true);
		graphView_1.getGridLabelRenderer().setVerticalLabelsVisible(true);
		graphView_1.getViewport().setXAxisBoundsManual(true);
		graphView_1.getViewport().setMinX(0);
		graphView_1.getViewport().setMaxX(Xview);
		graphView_1.addSeries(Series_1);

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
		//Record Button
		tbRecord = (ToggleButton)findViewById(R.id.tbRecord);
		tbRecord.setOnClickListener(new OnClickListener() {
		String m_chosen;
		@Override
		public void onClick(View v) {

			if (tbRecord.isChecked()){
				/////////////////////////////////////////////////////////////////////////////////////////////////
				//Create FileOpenDialog and register a callback
				/////////////////////////////////////////////////////////////////////////////////////////////////
				FileSaveDialog =  new SimpleFileDialog(MainActivity.this, "FileSave",
						new SimpleFileDialog.SimpleFileDialogListener()
						{
							@Override
							public void onChosenDir(String chosenDir)
							{
								// The code in this function will be executed when the Record button is pushed
								m_chosen = chosenDir;
								Toast.makeText(MainActivity.this, "Chosen FileOpenDialog File: " +
										m_chosen, Toast.LENGTH_LONG).show();
							}
						});

				//You can change the default filename using the public variable "Default_File_Name"
				FileSaveDialog.Default_File_Name = "Datos.txt";
				FileSaveDialog.chooseFile_or_Dir();

				/////////////////////////////////////////////////////////////////////////////////////////////////
				Record = true;
			} else{
				Record = false;
			}
		}
	});


		//init toggleButton
		Lock=true;
		AutoScrollX=true;
		Stream=true;
		Record=false;
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
			//if (Xview<30) Xview++;
			if (Xview<100) Xview++;
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
					Bluetooth.connectedThread.write("5");
					//Bluetooth.connectedThread.write(Character.toString((char)5));
			}else{
				if (Bluetooth.connectedThread != null)
					Bluetooth.connectedThread.write("Q");
			}
			break;
		case R.id.tbRecord:
			if (tbRecord.isChecked()){
				Record = true;

			}else{
				Record = false;
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

		int neg=0x00;
		if ((data[1] & 0x80) == 0x80)
			neg = 0xFF;
		int dato = (neg << 24) + (data[1] << 16) + (data[2] << 8) + (data[3]);

		//return Convert.ToDouble(dato) / Math.Pow(2, 23) * SCALE;
        return (double)dato;
	}

	//Guardar datos en un archivo:
void SaveData(String data, String Directory, String FileName) {
	try {
		// Creates a trace file in the primary external storage space of the
		// current application.
		// If the file does not exists, it is created.
		//File traceFile = new File(((Context) this).getExternalFilesDir(null), FileName);
		File traceFile = new File(Directory, FileName);
		if (!traceFile.exists())
			traceFile.createNewFile();
		// Adds a line to the trace file
		BufferedWriter writer = new BufferedWriter(new FileWriter(traceFile, true /*append*/));
		writer.write(data);
		writer.close();
		// Refresh the data so it can seen when the device is plugged in a
		// computer. You may have to unplug and replug the device to see the
		// latest changes. This is not necessary if the user should not modify
		// the files.
		MediaScannerConnection.scanFile((Context) (this),
				new String[]{traceFile.toString()},
				null,
				null);

	} catch (IOException e) {
		Log.e("com.example.tut1", "Unable to write to the TraceFile.txt file.");
	}
}

}
