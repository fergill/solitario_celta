package es.upm.miw.SolitarioCelta.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = Resultado.TABLA)
public class Resultado {
    static public final String TABLA = "resultados";

    static public final String FICHAS = "fichas";
    static public final String NOMBRE = "nombre";
    static public final String FECHA = "fecha";

    @PrimaryKey(autoGenerate = true)
    protected int uid;

    @ColumnInfo(name = FICHAS)
    protected int fichas;

    @ColumnInfo(name = NOMBRE)
    protected String jugador;

    @ColumnInfo(name = FECHA)
    protected String fecha;

    public Resultado(int fichas, String jugador, String fecha) {
        this.fichas = fichas;
        this.jugador = jugador;
        this.fecha = fecha;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getFichas() {
        return fichas;
    }

    public void setFichas(int fichas) {
        this.fichas = fichas;
    }

    public String getJugador() {
        return jugador;
    }

    public void setJugador(String jugador) {
        this.jugador = jugador;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
