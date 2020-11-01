package es.upm.miw.SolitarioCelta.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ResultadoDAO {
    @Query("SELECT * FROM " + Resultado.TABLA + " ORDER BY " + Resultado.FICHAS + " ASC")
    List<Resultado> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Resultado resultado);

    @Query("DELETE FROM " + Resultado.TABLA)
    void deleteAll();
}