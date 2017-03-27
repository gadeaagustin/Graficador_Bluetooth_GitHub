package com.gibio.bt_graph;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by aguus on 22/3/2017.
 */

public class AnamnesisActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private String MailDestinatario = "gibio.pacientes@gmail.com";
    private String MailAsunto = "Nuevo Paciente";
    private String DatosPersonales, Antecedentes, SignosVitales, Electro, Antropo, MailCuerpo;

    //Datos personales
    private EditText etNombre, etApellido, etEdad, etEmail, etOcupacion;
    private RadioButton btHombre, btMujer;
    private Spinner spPaises, spProvincias;
    private Button bENVIAR;

    //Antecedentes Medicos
    private CheckBox chkBebidasAlcoholicas, chkUsoDrogas, chkTabaquismo;
    private CheckBox chkTos, chkExpectoracion, chkHemoptisis, chkAsma, chkNeumonía;
    private CheckBox chkDbt, chkHta, chkTbc;
    private EditText  etOtrasEnfermedades;

    //Signos Vitales
    private EditText etFC, etTA, etFR, etPulso, etTaxilar, etPesoHab, etPesoAct;
    private EditText etTalla, etBMI, etPreSis, etPreDia, etDeltax;

    //Electrocardiograma
    private EditText etRitmo, etFCelectro, etEjeQRS, etOndaP, etQRS, etOndaT, etST, etQTC;

    //Mediciones Antropomedicas
    private EditText etCarotidaCuello, etCuelloHombro, etHombroBraquial, etHombroRadial, etCarotidaFemoral;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anamnesis);
        ObjetosInit();
    }

    void ObjetosInit(){

        //Cargo los editText de Datos personales
        etNombre=(EditText)findViewById(R.id.etNombre);
        etApellido=(EditText)findViewById(R.id.etApellido);
        etEmail=(EditText)findViewById(R.id.etEmail);
        etEdad=(EditText)findViewById(R.id.etEdad);
        etOcupacion=(EditText)findViewById(R.id.etOcupacion);

        btHombre=(RadioButton)findViewById(R.id.btHombre);
        btMujer=(RadioButton)findViewById(R.id.btMujer);


        //Cargo lista de paises y provincias
        spPaises = (Spinner)findViewById(R.id.spPaises);
        spProvincias = (Spinner)findViewById(R.id.spProvincias);

        ArrayAdapter<CharSequence> adaptadorPaises = ArrayAdapter.createFromResource(this, R.array.array_Paises, android.R.layout.simple_spinner_item);
        adaptadorPaises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaises.setAdapter(adaptadorPaises);

        //Se aplica listener para saber que item ha sido seleccionado
        //y poder usarlo en el método "onItemSelected"
        spPaises.setOnItemSelectedListener(this);


        //Cargo Antecedentes
        chkBebidasAlcoholicas = (CheckBox)findViewById(R.id.chkBebidasAlcoholicas);
        chkUsoDrogas = (CheckBox)findViewById(R.id.chkUsoDrogas);
        chkTabaquismo = (CheckBox)findViewById(R.id.chkTabaquismo);


        chkTos = (CheckBox)findViewById(R.id.chkTos);
        chkExpectoracion = (CheckBox)findViewById(R.id.chkExpectoracion);
        chkHemoptisis = (CheckBox)findViewById(R.id.chkHemoptisis);
        chkAsma = (CheckBox)findViewById(R.id.chkAsma);
        chkNeumonía = (CheckBox)findViewById(R.id.chkNeumonía);
        chkDbt = (CheckBox)findViewById(R.id.chkDbt);
        chkHta = (CheckBox)findViewById(R.id.chkHta);
        chkTbc = (CheckBox)findViewById(R.id.chkTbc);

        etOtrasEnfermedades=(EditText)findViewById(R.id.etOtrasEnfermedades);

        //Cargo los editText de Signos Vitales
        etFC=(EditText)findViewById(R.id.etFC);
        etTA=(EditText)findViewById(R.id.etTA);
        etFR=(EditText)findViewById(R.id.etFR);
        etPulso=(EditText)findViewById(R.id.etPulso);
        etTaxilar=(EditText)findViewById(R.id.etTaxilar);
        etPesoHab=(EditText)findViewById(R.id.etPesoHab);
        etPesoAct=(EditText)findViewById(R.id.etPesoAct);
        etTalla=(EditText)findViewById(R.id.etTalla);
        etBMI=(EditText)findViewById(R.id.etBMI);
        etPreSis=(EditText)findViewById(R.id.PreSis);
        etPreDia=(EditText)findViewById(R.id.etPreDia);
        etDeltax=(EditText)findViewById(R.id.etDeltax);

        //Cargo los editText de Electrocardiograma
        etRitmo=(EditText)findViewById(R.id.etRitmo);
        etFCelectro=(EditText)findViewById(R.id.etFCelectro);
        etEjeQRS=(EditText)findViewById(R.id.etEjeQRS);
        etOndaP=(EditText)findViewById(R.id.etOndaP);
        etQRS=(EditText)findViewById(R.id.etQRS);
        etOndaT=(EditText)findViewById(R.id.etOndaT);
        etST=(EditText)findViewById(R.id.etST);
        etQTC=(EditText)findViewById(R.id.etQTC);

        //Cargo los editText de Mediciones Antropomedicas
        etCarotidaCuello=(EditText)findViewById(R.id.etCarotidaCuello);
        etCuelloHombro=(EditText)findViewById(R.id.etCuelloHombro);
        etHombroBraquial=(EditText)findViewById(R.id.etHombroBraquial);
        etHombroRadial=(EditText)findViewById(R.id.etHombroRadial);
        etCarotidaFemoral=(EditText)findViewById(R.id.etCarotidaFemoral);


        //Boton para enviar datos
        bENVIAR = (Button) findViewById(R.id.bENVIAR);
        bENVIAR.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bENVIAR:
                Cargar_Datos();


                //Compruebo que se completo los datos personales
                if(etNombre.getText().toString().equals("") || etApellido.getText().toString().equals("") ||
                        etEmail.getText().toString().equals("") || etEdad.getText().toString().equals("")
                        ||etOcupacion.getText().toString().equals(""))
                {
                    String txtEnviado = "Complete datos personales";
                    Toast.makeText(this, txtEnviado, Toast.LENGTH_LONG).show();
                    etNombre.requestFocus();
                }else {


                    // es necesario un intent que levante la actividad deseada
                    Intent itSend = new Intent(android.content.Intent.ACTION_SEND);
                    // vamos a enviar texto plano a menos que el checkbox esté marcado
                    itSend.setType("plain/text");

                    //colocamos los datos para el envío
                    itSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{MailDestinatario.toString()});
                    itSend.putExtra(android.content.Intent.EXTRA_SUBJECT, MailAsunto.toString());
                    itSend.putExtra(android.content.Intent.EXTRA_TEXT, MailCuerpo.toString());

                    startActivity(itSend);

                    String txtEnviado = "Datos enviados";
                    Toast.makeText(this, txtEnviado, Toast.LENGTH_LONG).show();
                    startActivity(new Intent("android.intent.action.MAIN"));

                }
                break;
        }
    }

    void Cargar_Datos(){
        //Creo el Asunto del mail con el nombre y apellido del paciente
        MailAsunto = MailAsunto +" "+ etNombre.getText() +" "+ etApellido.getText();
        //Creo el Cuerpo del mensaje
        String sexo =" ";
        if (btHombre.isChecked()) sexo = "Hombre";
        else if(btMujer.isChecked()) sexo = "Mujer";

        DatosPersonales ="Datos Personales: "+"-------------------------"+"\n"+ "Nombre: "+etNombre.getText()+"\n" + "Apellido: "+etApellido.getText()+"\n"+
                    "Edad: "+ etEdad.getText()+"\n"+ "Email: "+etEmail.getText()+"\n"+ "Sexo: "+sexo.toString()+"\n"+
                    "Pais: "+spPaises.getSelectedItem()+"\n" + "Provincia: " +spProvincias.getSelectedItem()+"\n"+
                    "Ocupación: "+ etOcupacion.getText()+"\n";

        Antecedentes ="Habitos Toxicos: "+"-------------------------"+"\n"+ "Bebidas Alcóholicas: "+ chkBebidasAlcoholicas.isChecked()+"\n"+
                    "Uso de drogas de abuso: "+chkUsoDrogas.isChecked()+"\n"+ "Tabaquismo: "+ chkTabaquismo.isChecked()+"\n"+
                    "Patologias: "+"\n" + "Enfermedades Respiratorias: "+"-------------------------"+"\n" +"Tos: "+chkTos.isChecked()+"\n"+
                    "Expectoración: "+chkExpectoracion.isChecked()+"\n"+ "Hemoptisis: "+ chkHemoptisis.isChecked()+"\n"+
                    "Asma: "+chkAsma.isChecked()+"\n"+ "Neumonía: "+chkNeumonía.isChecked()+"\n"+
                    "Enfermedades Cardiovasculares: "+"-------------------------"+"\n"+ "DBT: "+chkDbt.isChecked()+"\n"+ "HTA: "+chkHta.isChecked()+"\n"+
                    "TBC: "+chkTbc.isChecked()+"\n" + "Otras Enfermedades: "+etOtrasEnfermedades.getText()+"\n";

        SignosVitales = "Signos Vitales: "+"-------------------------"+"\n"+"FC: "+etFC.getText()+"\n"+"TA: "+etTA.getText()+"\n"+
                    "FR: "+etFR.getText()+"\n"+"Pulso: "+etPulso.getText()+"\n"+ "T° Axilar: "+etTaxilar.getText()+"\n"+
                    "Peso Habitual: "+etPesoHab.getText()+"\n"+"Peso Actual: "+etPesoAct.getText()+"\n"+
                    "Talla: "+etTalla.getText()+"\n"+"BMI: "+etBMI.getText()+"\n"+"Presión Sistótica: "+etPreSis.getText()+"\n"+
                    "Presión Diastólica: "+etPreDia.getText()+"\n"+ "Delta X: "+etDeltax.getText()+"\n";

        Electro = "Electrocardiograma: "+"-------------------------"+"\n"+ "Ritmo: "+etRitmo.getText()+"\n"+ "FC electro: "+etFCelectro.getText()+"\n"+
                "Eje QRS: "+etEjeQRS.getText()+"\n"+"Onda P: "+etOndaP.getText()+"\n"+"QRS: "+etQRS.getText()+"\n"+
                "Onda T: "+etOndaT.getText()+"\n"+"ST: "+etST.getText()+"\n"+"QTC: "+etQTC.getText()+"\n";

        Antropo = "Mediciones Antropomedicas: "+"-------------------------"+"\n"+ "Carótida - Cuello: "+etCarotidaCuello.getText()+"\n"+
                "Cuello - Hombro: "+etCuelloHombro.getText()+"\n"+ "Hombro Braquial: "+etHombroBraquial.getText()+"\n"+
                "Hombro - Radial: "+etHombroRadial.getText()+"\n"+ "Carótida - Femoral: "+etCarotidaFemoral.getText()+"\n";


        MailCuerpo ="-------------------------" + DatosPersonales +"-------------------------"
                    + Antecedentes + "-------------------------" + SignosVitales
                    + "-------------------------" + Electro + "-------------------------" + Antropo ;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Se guarda en array de enteros los arrays de las provincias
        int[] localidades = {R.array.array_Argentina,R.array.array_Brasil, R.array.array_Paraguay, R.array.array_Uruguay};

        //Construcción del "adaptador" para el segundo Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                localidades[position],//En función del pais, se carga el array que corresponda del XML
                android.R.layout.simple_spinner_item);

        //Se carga el tipo de vista para el adaptador
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Se aplica el adaptador al Spinner de localidades
        spProvincias.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
