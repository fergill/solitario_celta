package es.upm.miw.SolitarioCelta;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.jetbrains.annotations.NotNull;

public class AlertDialogReiniciar extends AppCompatDialogFragment {
    @NotNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity main = (MainActivity) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder
                .setTitle(R.string.reiniciarTitle)
                .setMessage(R.string.reiniciarMessage)
                .setPositiveButton(
                        getString(R.string.txtDialogoAfirmativo),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                main.miJuegoVM.reiniciar();
                                main.mostrarTablero();
                                main.cronometroComando.resetCronometro();
                                main.cronometroComando.startCronometro();
                            }
                        }
                )
                .setNegativeButton(
                        getString(R.string.txtDialogoNegativo),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                main.mostrarTablero();
                                main.cronometroComando.startCronometro();
                            }
                        }
                );

        return builder.create();
    }
}
