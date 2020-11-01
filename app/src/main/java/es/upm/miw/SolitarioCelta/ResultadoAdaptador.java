package es.upm.miw.SolitarioCelta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import es.upm.miw.SolitarioCelta.model.Resultado;

public class ResultadoAdaptador extends ArrayAdapter {
    private Context _contexto;
    private int _idLayout;
    private List<Resultado> _resultados;


    /**
     * Constructor
     *
     * @param contexto   Contexto
     * @param idLayout   Layout sobre el que representar los datos
     * @param resultados Datos a representar
     */
    public ResultadoAdaptador(Context contexto, int idLayout, List<Resultado> resultados) {
        super(contexto, idLayout, resultados);
        this._contexto = contexto;
        this._idLayout = idLayout;
        this._resultados = resultados;
        setNotifyOnChange(true);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /**
         * Comprobar si existe una lista convertible para permitir hacer scroll
         * sobre la lista que aparece en pantalla. Si no la tiene, la crea
         * con el inflador que trae el contexto de la actividad anterior
         */
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) _contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this._idLayout, null);
        }

        /**
         * Traer el resultado de la lista que este en "position"
         * Comprobar que al menos hay un resultado
         * Asignar las vistas a los datos
         */
        Resultado resultado = _resultados.get(position);
        if (resultado != null) {

            TextView tvPosicion = convertView.findViewById(R.id.tvListadoResultadosPosicion);
            TextView tvFichas = convertView.findViewById(R.id.tvListadoResultadosFichas);
            TextView tvJugador = convertView.findViewById(R.id.tvListadoResultadosNombre);
            TextView tvFecha = convertView.findViewById(R.id.tvListadoResultadosFecha);

            tvPosicion.setText(Integer.toString(position + 1));
            tvFichas.setText(Integer.toString(resultado.getFichas()));
            tvJugador.setText(resultado.getJugador());
            tvFecha.setText(resultado.getFecha());

        }
        return convertView;
    }
}
