package es.upm.miw.SolitarioCelta.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Resultado.class}, version = 1, exportSchema = false)
public abstract class ResultadoRepositorio extends RoomDatabase {
    public static final String BASE_DATOS = Resultado.TABLA + ".db";

    public abstract ResultadoDAO resultadoDAO();
}
