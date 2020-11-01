package es.upm.miw.SolitarioCelta;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

public class AlertDialogBorrarResultados extends AppCompatDialogFragment {

    @NotNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MejoresResultadosActivity mejoresResultados = (MejoresResultadosActivity) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(mejoresResultados);
        builder
                .setTitle(R.string.txtDialogoBorrarResultadosTitulo)
                .setMessage(R.string.txtDialogoBorrarResultadosPregunta)
                .setPositiveButton(
                        getString(R.string.txtDialogoAfirmativo),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mejoresResultados.resultadoRepositorio.resultadoDAO().deleteAll();

                                /**
                                 * Para que se visualice la lista vacía al borrar los resultado
                                 */
                                mejoresResultados.adaptadorMejoresResultados = new ResultadoAdaptador(
                                        mejoresResultados,
                                        R.layout.resultado_item,
                                        mejoresResultados.resultadoRepositorio.resultadoDAO().getAll()
                                );

                                mejoresResultados.lvListadoResultado.setAdapter(mejoresResultados.adaptadorMejoresResultados);
                            }
                        }
                )
                .setNegativeButton(
                        getString(R.string.txtDialogoNegativo),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Snackbar.make(mejoresResultados.lvListadoResultado,
                                        "Acción de borrado cancelada", Snackbar.LENGTH_LONG).show();
                            }
                        }
                );
        return builder.create();
    }
}
