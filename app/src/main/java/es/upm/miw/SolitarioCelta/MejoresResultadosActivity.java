package es.upm.miw.SolitarioCelta;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.upm.miw.SolitarioCelta.model.ResultadoRepositorio;

public class MejoresResultadosActivity extends AppCompatActivity {

    ListView lvListadoResultado;
    ResultadoAdaptador adaptadorMejoresResultados;
    ResultadoRepositorio resultadoRepositorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mejores_resultados);

        lvListadoResultado = findViewById(R.id.lvListadoResultados);

        resultadoRepositorio = Room.databaseBuilder(
                getApplicationContext(),
                ResultadoRepositorio.class,
                ResultadoRepositorio.BASE_DATOS
        )
                .allowMainThreadQueries()
                .build();

        adaptadorMejoresResultados = new ResultadoAdaptador(
                this,
                R.layout.resultado_item,
                resultadoRepositorio.resultadoDAO().getAll()
        );
        lvListadoResultado.setAdapter(adaptadorMejoresResultados);

        FloatingActionButton fabBorrarListadoResultados = findViewById(R.id.fabBorrarListadoResultados);
        fabBorrarListadoResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialogBorrarResultados().show(getSupportFragmentManager(), "ALERT_DIALOG");
            }
        });
    }
}
