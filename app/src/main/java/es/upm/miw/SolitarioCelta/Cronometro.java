package es.upm.miw.SolitarioCelta;

import android.os.SystemClock;
import android.widget.Chronometer;

public class Cronometro {

    Chronometer cronometro;
    boolean corriendo;
    long diferenciaTiempo;

    public Cronometro() {
    }

    public Cronometro(Chronometer cronometro) {
        this.cronometro = cronometro;
    }

    public Chronometer getCronometro() {
        return cronometro;
    }

    public void setCronometro(Chronometer cronometro) {
        this.cronometro = cronometro;
    }

    public boolean getCorriendo() {
        return corriendo;
    }

    public void setCorriendo(boolean corriendo) {
        this.corriendo = corriendo;
    }

    public void startCronometro(){
        if (!corriendo) {
            this.cronometro.setBase(SystemClock.elapsedRealtime() - diferenciaTiempo);
            this.cronometro.start();
            corriendo = true;
        }
    }

    public void pauseCronometro(){
        if (corriendo){
            this.cronometro.stop();
            diferenciaTiempo = SystemClock.elapsedRealtime() - cronometro.getBase();
            corriendo = false;
        }
    }

    public void resetCronometro(){
        this.cronometro.setBase(SystemClock.elapsedRealtime());
        diferenciaTiempo = 0;
    }
}
