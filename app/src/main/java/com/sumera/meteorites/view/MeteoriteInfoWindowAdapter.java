package com.sumera.meteorites.view;

import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.sumera.meteorites.R;

/**
 * Created by martin on 07/09/16.
 */

public class MeteoriteInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater m_inflater;

    public MeteoriteInfoWindowAdapter(LayoutInflater inflater) {
        m_inflater = inflater;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return m_inflater.inflate(R.layout.map_info_window_meteorite, null);
    }
}
