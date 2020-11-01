package es.upm.miw.SolitarioCelta;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.upm.miw.SolitarioCelta.model.Resultado;
import es.upm.miw.SolitarioCelta.model.ResultadoRepositorio;
import es.upm.miw.SolitarioCelta.model.SCeltaViewModel;
import es.upm.miw.SolitarioCelta.model.SCeltaViewModelFactory;

public class MainActivity extends AppCompatActivity {

    static final String CRONO_TIEMPO = "tiempo";
    protected final String LOG_TAG = "MiW";
    protected final Integer ID = 2021;
    protected final String ALERT_DIALOG_TAG = "ALERT_DIALOG";
    public Cronometro cronometroComando;
    protected SCeltaViewModel miJuegoVM;
    ResultadoRepositorio resultadoRepositorio;
    ColorStateList colorState;
    private SharedPreferences preferencias;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private Chronometer cronometro;
    private String prefFirma;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultadoRepositorio = Room.databaseBuilder(
                getApplicationContext(),
                ResultadoRepositorio.class,
                ResultadoRepositorio.BASE_DATOS
        )
                .allowMainThreadQueries()
                .build();

        cronometro = findViewById(R.id.chCronometro);

        cronometroComando = new Cronometro(cronometro);
        cronometroComando.resetCronometro();
        cronometroComando.startCronometro();

        miJuegoVM = new ViewModelProvider(
                this,
                new SCeltaViewModelFactory(getApplication(), ID)
        )
                .get(SCeltaViewModel.class);
        mostrarTablero();

        if (savedInstanceState != null) {
            cronometro.setBase(savedInstanceState.getLong(CRONO_TIEMPO));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(CRONO_TIEMPO, cronometro.getBase());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        colorState = getColor();
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("pref_NombreJugador")) {
                    miJuegoVM.reiniciar();
                    cronometroComando.resetCronometro();
                    cronometroComando.startCronometro();

                }
            }
        };
        preferencias.registerOnSharedPreferenceChangeListener(listener);

        obtenerNombreJugador();
        mostrarTablero();
    }

    @Override
    protected void onStart() {
        super.onStart();

        preferencias = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        prefFirma = preferencias.getString(
                getString(R.string.default_NombreJugador),
                getString(R.string.key_NombreJugador)
        );
        boolean prefSync = preferencias.getBoolean("sync", false);

        Log.i(LOG_TAG, "onSTART(): Firma = " + prefFirma);
        Log.i(LOG_TAG, "sync = " + ((prefSync) ? "on" : "off"));
    }

    public void editarPrefs(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Se ejecuta al pulsar una ficha
     * Las coordenadas (i, j) se obtienen a partir del nombre del recurso, ya que el botón
     * tiene un identificador en formato pXY, donde X es la fila e Y la columna
     *
     * @param v Vista de la ficha pulsada
     */

    public void fichaPulsada(@NotNull View v) {
        String resourceName = getResources().getResourceEntryName(v.getId());
        int i = resourceName.charAt(1) - '0';   // fila
        int j = resourceName.charAt(2) - '0';   // columna

        int fichas = miJuegoVM.numeroFichas();
        Log.i(LOG_TAG, "fichaPulsada(" + i + ", " + j + ") - " + resourceName);
        miJuegoVM.jugar(i, j);
        Log.i(LOG_TAG, "#fichas=" + fichas);

        mostrarTablero();
        if (miJuegoVM.juegoTerminado()) {
            String fecha = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

            resultadoRepositorio.resultadoDAO().insert(
                    new Resultado(
                            fichas,
                            obtenerNombreJugador(),
                            fecha
                    )
            );

            Toast.makeText(this, "Resultados guardados en BBDD ", Toast.LENGTH_LONG).show();
            new AlertDialogFragment().show(getSupportFragmentManager(), ALERT_DIALOG_TAG);
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
        TextView tvFichas = findViewById(R.id.tvFichas);

        tvFichas.setText("Fichas en tablero: " + String.valueOf(miJuegoVM.numeroFichas()));

        for (int i = 0; i < SCeltaViewModel.TAMANIO; i++)
            for (int j = 0; j < SCeltaViewModel.TAMANIO; j++) {
                strRId = prefijoIdentificador + i + j;
                idBoton = getResources().getIdentifier(strRId, null, null);
                if (idBoton != 0) { // existe el recurso identificador del botón
                    button = findViewById(idBoton);
                    button.setChecked(miJuegoVM.obtenerFicha(i, j) == SCeltaViewModel.FICHA);
                    button.setButtonTintList(getColor());
                }
            }
    }

    private String obtenerNombreJugador() {
        String nombreJugador = preferencias.getString(
                getString(R.string.key_NombreJugador),
                getString(R.string.default_NombreJugador)
        );
        Log.i(LOG_TAG, "Nombre del jugador: " + nombreJugador);

        return nombreJugador;
    }

    public ColorStateList getColor() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String index_color = SP.getString(getString(R.string.default_ColorKey), "1");
        int color = Color.BLACK;

        switch (index_color) {
            case "2":
                color = Color.RED;
                break;
            case "3":
                color = Color.BLUE;
                break;
            case "4":
                color = Color.CYAN;
                break;
        }

        return new ColorStateList(
                new int[][]{
                        new int[]{color}
                },
                new int[]{color}
        );
    }

    private void mostrarMensaje(String message) {
        Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG
        ).show();
    }

    private boolean memoriaUtilizada() {
        return preferencias.getBoolean(
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

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcPausarPartida:
                if (cronometroComando.getCorriendo())
                    cronometroComando.pauseCronometro();
                else
                    cronometroComando.startCronometro();
                return true;
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
                cronometroComando.pauseCronometro();
                new AlertDialogReiniciar().show(getSupportFragmentManager(),
                        ALERT_DIALOG_TAG);
                return true;
            case R.id.opcGuardarPartida:
                cronometroComando.pauseCronometro();
                guardarPartida();
                return true;
            case R.id.opcRecuperarPartida:
                cronometroComando.pauseCronometro();
                new AlertDialogRecuperar().show(getSupportFragmentManager(),
                        ALERT_DIALOG_TAG);
                return true;
            case R.id.opcMejoresResultados:
                startActivity(new Intent(this, MejoresResultadosActivity.class));
                return true;
            default:
                this.mostrarMensaje(getString(R.string.txtSinImplementar));
        }
        return true;
    }

}
