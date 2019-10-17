package com.example.admin.osmeuslugares.presentacion;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.admin.osmeuslugares.R;
import com.example.admin.osmeuslugares.casosdeuso.CasoUsoLugares;
import com.example.admin.osmeuslugares.modelo.Lugar;
import com.example.admin.osmeuslugares.modelo.LugaresVector;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;


public class VistaLugarActivity extends AppCompatActivity {

    /*****************************************
     * constantes para startActivityForResult()
     * que tambien se usarán en galeria
     *****************************************/
    final static int RESULTADO_EDITAR = 1;
    final static int RESULTADO_GALERIA= 2;
    final static int RESULTADO_FOTO= 3;
    private ImageView imageView;
    private Uri uriFoto;
    /************************
     * compartir datos
     *************************/
    private LugaresVector lugares;
    private CasoUsoLugares usoLugar;
    private int pos;
    private Lugar lugar;

    @Override protected void onCreate(Bundle savedInstanceState) {
       // imageView= (ImageView) findViewById(R.id.foto);//aqui no esta creado el layout
        //por eso no reserba memoria
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_lugar);
        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("pos", 0);
        lugares = ((Aplicacion) getApplication()).getLugares();
        usoLugar = new CasoUsoLugares(this, lugares);
        lugar = lugares.elemento(pos);
        actualizaVistas();
    }

    public void actualizaVistas() {
        imageView= (ImageView) findViewById(R.id.foto);
        TextView nombre = findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());
        ImageView logo_tipo = findViewById(R.id.logo_tipo);
        logo_tipo.setImageResource(lugar.getTipo().getRecurso());
        TextView tipo = findViewById(R.id.tipo);
        tipo.setText(lugar.getTipo().getTexto());
        TextView direccion = findViewById(R.id.direccion);
        direccion.setText(lugar.getDireccion());
        TextView telefono = findViewById(R.id.telefono);
        telefono.setText(Integer.toString(lugar.getTelefono()));
        TextView url = findViewById(R.id.url);
        url.setText(lugar.getUrl());
        TextView comentario = findViewById(R.id.comentario);
        comentario.setText(lugar.getComentario());
        TextView fecha = findViewById(R.id.fecha);
        fecha.setText(DateFormat.getDateInstance().format(
                new Date(lugar.getFecha())));
        TextView hora = findViewById(R.id.hora);
        hora.setText(DateFormat.getTimeInstance().format(
                new Date(lugar.getFecha())));
        RatingBar valoracion = findViewById(R.id.valoracion);
        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override public void onRatingChanged(RatingBar ratingBar,
                                                          float valor, boolean fromUser) {
                        lugar.setValoracion(valor);
                    }
                });
        //imageView.setImageDrawable(lugar.getFoto());

         ponerFoto(imageView, lugar.getFoto());
    }
    /*****************
    * MENU
    * ***************/
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vista_lugar, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        boolean opExito=true;
        switch (item.getItemId()) {
            case R.id.accion_compartir:
                break;
            case R.id.accion_llegar:
                break;
            case R.id.accion_editar:
                usoLugar.editar(pos,RESULTADO_EDITAR);
                break;
            case R.id.accion_borrar:
                eliminarLugar(pos);

                break;
            default:
                opExito=super.onOptionsItemSelected(item);
        }
        return opExito;
    }
    /*metodos*/
    public void eliminarLugar(final int posicion){
        if (lugares.tamanyo()< posicion)
            Toast.makeText(this, "Error: elemento no existe", Toast.LENGTH_LONG).show();
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Borrado de lugar")
                    .setMessage("¿Esta serguro?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            usoLugar.borrar( posicion);
                        }})
                    .setNegativeButton("Cancelar", null)
                    .show();

        }
    }
    /***********************************************
     * codigo para
     * refrescar tras editar
     * uso de galeria
    * ********************************************/
    @Override protected void onActivityResult(int requestCode, int resultCode,
                                              Intent data) {
        /***************
         * Una vez regresamos de la actividad EdicionLugarActivity
         * lo que hacemos es actualizar los valores de las vistas y
         * forzar al sistema a que repinte la vista con id scrollView1.
         * Esta vista corresponde al ScrollView que contiene todo el LAYOUT
         */
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){

            case RESULTADO_EDITAR:
                                    actualizaVistas();
                                    break;
            case RESULTADO_GALERIA: //actualizaVistas();
                                    if (resultCode == Activity.RESULT_OK) {
                                        lugar.setFoto(data.getDataString());
                                        ponerFoto(imageView, lugar.getFoto());
                                        actualizaVistas();
                                    } else {
                                            Toast.makeText(this, "Foto no cargada", Toast.LENGTH_LONG).show();
                                    }
                                    break;
            case RESULTADO_FOTO:
                                if (resultCode == Activity.RESULT_OK
                                        && lugar!=null && uriFoto!=null) {
                                    lugar.setFoto(uriFoto.toString());
                                    ponerFoto(imageView, lugar.getFoto());
                                   // actualizaVistas();
                                } else {
                                    Toast.makeText(this, "Error en captura", Toast.LENGTH_LONG).show();
                                }
                                break;

        }
        /*
        if (requestCode == RESULTADO_EDITAR) {
            actualizaVistas();
        }else if (requestCode == RESULTADO_GALERIA) {
            if (resultCode == Activity.RESULT_OK) {
                lugar.setFoto(data.getDataString());
                ponerFoto(imageView, lugar.getFoto());
            } else {
                Toast.makeText(this, "Foto no cargada", Toast.LENGTH_LONG).show();
            }
        }*/
    }
    /************************************************************
     * codigo del uso de galeria fotos
     * **********************************************************/
    public void galeria(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULTADO_GALERIA);
    }
    protected void ponerFoto(ImageView imageView, String uri) {
        if (uri != null && !uri.isEmpty() && !uri.equals("null")) {
            imageView.setImageURI(Uri.parse(uri));
        } else{
           // imageView.setImageBitmap(null);--> no tiene espacio reservado...
        }
    }
    public void tomarFoto(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               uriFoto = Uri.fromFile(new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+File.separator
                                +"img_" + (System.currentTimeMillis() / 1000) + ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(intent, RESULTADO_FOTO);
    }
    public void eliminarFoto(View view) {
        lugar.setFoto(null);
        ponerFoto(imageView, null);
        //actualizaVistas();
    }







}