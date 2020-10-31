package es.upm.miw.SolitarioCelta;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

import es.upm.miw.SolitarioCelta.model.SCeltaViewModel;
import es.upm.miw.SolitarioCelta.model.SCeltaViewModelFactory;

public class MainActivity extends AppCompatActivity {

    protected final String LOG_TAG = "MiW";
    protected final Integer ID = 2021;
    protected SCeltaViewModel miJuegoVM;
    private SharedPreferences configuraciones;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        miJuegoVM = new ViewModelProvider(
                    this,
                    new SCeltaViewModelFactory(getApplication(), ID)
                    )
                .get(SCeltaViewModel.class);
        mostrarTablero();
        configuraciones = PreferenceManager.getDefaultSharedPreferences(this);
    }

    /**
     * Se ejecuta al pulsar una ficha
     * Las coordenadas (i, j) se obtienen a partir del nombre del recurso, ya que el botón
     * tiene un identificador en formato pXY, donde X es la fila e Y la columna
     * @param v Vista de la ficha pulsada
     */
    public void fichaPulsada(@NotNull View v) {
        String resourceName = getResources().getResourceEntryName(v.getId());
        int i = resourceName.charAt(1) - '0';   // fila
        int j = resourceName.charAt(2) - '0';   // columna

        Log.i(LOG_TAG, "fichaPulsada(" + i + ", " + j + ") - " + resourceName);
        miJuegoVM.jugar(i, j);
        Log.i(LOG_TAG, "#fichas=" + miJuegoVM.numeroFichas());

        mostrarTablero();
        if (miJuegoVM.juegoTerminado()) {
            // TODO guardar puntuación
            new AlertDialogFragment().show(getSupportFragmentManager(), "ALERT_DIALOG");
        }
    }

    /**
     * Visualiza el tablero
     */
    public void mostrarTablero() {
        RadioButton button;
        String strRId;
        String prefijoIdentificador = getPackageName() + ":id/p"; // formato: package:type/entry
        int idBoton;

        for (int i = 0; i < SCeltaViewModel.TAMANIO; i++)
            for (int j = 0; j < SCeltaViewModel.TAMANIO; j++) {
                strRId = prefijoIdentificador + i + j;
                idBoton = getResources().getIdentifier(strRId, null, null);
                if (idBoton != 0) { // existe el recurso identificador del botón
                    button = findViewById(idBoton);
                    button.setChecked(miJuegoVM.obtenerFicha(i, j) == SCeltaViewModel.FICHA);
                }
            }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcAjustes:
                startActivity(new Intent(this, SCeltaPrefs.class));
                return true;
            case R.id.opcAcercaDe:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.aboutTitle)
                        .setMessage(R.string.aboutMessage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
            case R.id.opcReiniciarPartida:
                new AlertDialogReiniciar().show(getSupportFragmentManager(),
                        "ALERT_DIALOG");
                return true;
            case R.id.opcGuardarPartida:
                this.guardarPartida();
                return true;
            case R.id.opcRecuperarPartida:
                if (miJuegoVM.numeroFichas() != 32) {
                    new AlertDialogRecuperar().show(getSupportFragmentManager(),
                            "ALERT_DIALOG");
                }
                return true;

            // TODO!!! resto opciones

            default:
                this.mostrarMensaje(getString(R.string.txtSinImplementar));
        }
        return true;
    }

    private void mostrarMensaje(String message) {
        Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG
        ).show();
    }

    private boolean memoriaUtilizada() {
        return configuraciones.getBoolean(
                getString(R.string.tarjetaSD),
                getResources().getBoolean(R.bool.default_tarjetaSD));
    }

    private String nombreFichero() {
        return getString(R.string.default_nombreFichero);
    }

    public void guardarPartida() {
        try {
            String juegoSerializado = miJuegoVM.serializaTablero();
            FileOutputStream fos;
            if (!memoriaUtilizada()) {
                File dir = getFilesDir();
                File file = new File(dir, nombreFichero());
                if (file.exists()) {
                    file.delete();
                }
                fos = openFileOutput(nombreFichero(), Context.MODE_APPEND);
            } else {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String path = getExternalFilesDir(null) + "/" + nombreFichero();
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete();
                    }
                    fos = new FileOutputStream(path, true);
                } else {
                    this.mostrarMensaje(getString(R.string.txtErrorTarjetaSD));
                    return;
                }
            }
            fos.write(juegoSerializado.getBytes());
            fos.write('\n');
            fos.close();
            this.mostrarMensaje(getString(R.string.partidaGuardadaOk));
        } catch (Exception ex) {
            this.mostrarMensaje(ex.getMessage());
        }
    }

    public void recuperarPartida() {
        try {
            BufferedReader fin;
            if (!memoriaUtilizada()) {
                File dir = getFilesDir();
                File file = new File(dir, nombreFichero());
                if (file.exists()) {
                    fin = new BufferedReader(
                            new InputStreamReader(openFileInput(nombreFichero()))
                    );
                } else {
                    throw new Exception(getString(R.string.txtErrorRecuperarPartida));
                }
            } else {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String path = getExternalFilesDir(null) + "/" + nombreFichero();
                    File file = new File(path);
                    if (file.exists()) {
                        fin = new BufferedReader(new FileReader(new File(path)));
                    } else {
                        throw new Exception(getString(R.string.txtErrorRecuperarPartida));
                    }
                } else {
                    this.mostrarMensaje(getString(R.string.txtErrorTarjetaSD));
                    return;
                }
            }
            String line = fin.readLine();
            fin.close();
            this.miJuegoVM.deserializaTablero(line);
            this.mostrarTablero();
        } catch (Exception ex) {
            this.mostrarMensaje(ex.getMessage());
        }
    }
}
