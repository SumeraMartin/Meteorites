package com.sumera.meteorites.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.sumera.meteorites.R;
import com.sumera.meteorites.model.Meteorite;
import com.sumera.meteorites.utils.DoubleUtils;

/**
 * Created by martin on 07/09/16.
 */

public class MeteoriteInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final LayoutInflater m_inflater;

    public MeteoriteInfoWindowAdapter(LayoutInflater inflater) {
        m_inflater = inflater;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = m_inflater.inflate(R.layout.map_info_window_meteorite, null);
        Meteorite meteorite = (Meteorite) marker.getTag();

        TextView textView = (TextView) view.findViewById    (R.id.name);
        textView.setText(meteorite.getName());

        textView = (TextView)view.findViewById(R.id.type);
        textView.setText(meteorite.getRecclass());

        textView = (TextView)view.findViewById(R.id.mass);
        textView.setText("" + meteorite.getMass());

        double lat = DoubleUtils.round(Double.valueOf(meteorite.getLatitude()), 2);
        double lng = DoubleUtils.round(Double.valueOf(meteorite.getLongitude()), 2);

        textView = (TextView)view.findViewById(R.id.location);
        textView.setText("(" + lat + "°" + ", " + lng + "°)" );

        return view;
    }
}
