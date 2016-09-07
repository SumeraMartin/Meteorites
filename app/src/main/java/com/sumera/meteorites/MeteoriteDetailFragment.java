package com.sumera.meteorites;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sumera.meteorites.model.Meteorite;
import com.sumera.meteorites.model.MeteoritesCache;
import com.sumera.meteorites.utils.MapUtils;
import com.sumera.meteorites.view.MeteoriteInfoWindowAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 06/09/16.
 */

public class MeteoriteDetailFragment extends Fragment implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    public static final String METEORITE_ID_KEY = "meteorite_id";

    private Marker m_lastMarkerWithInfoWindow = null;

    private List<Meteorite> m_meteorites = new ArrayList<>();

    private GoogleMap m_googleMap;

    private View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if(arguments != null && arguments.containsKey(METEORITE_ID_KEY)) {
            String id = arguments.getString(METEORITE_ID_KEY);
            m_meteorites = new ArrayList<>();
            m_meteorites.add(MeteoritesCache.getMeteoriteById(id));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.meteorite_detail, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapView mapView = (MapView) mView.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(m_googleMap != null) {
            m_googleMap.setInfoWindowAdapter(new MeteoriteInfoWindowAdapter(getActivity().getLayoutInflater()));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_googleMap = googleMap;
        m_googleMap.setOnMarkerClickListener(this);

        if(getActivity() != null) {
            m_googleMap.setInfoWindowAdapter(new MeteoriteInfoWindowAdapter(getActivity().getLayoutInflater()));
        }

        /**
         * When
         */
        if(m_meteorites.size() == 1) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMeteoriteWithInfoWindow(0);
                }
            }, 100);
        }

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        hideInfoWindowIfPossible();
        showMeteoriteWithInfoWindow(marker);

        return true;
    }

    public boolean isInfoWindowShown() {
        return m_lastMarkerWithInfoWindow != null && m_lastMarkerWithInfoWindow.isInfoWindowShown();
    }

    public void setMeteoritesData(List<Meteorite> meteorites) {
        m_meteorites = meteorites;
    }

    public void showMeteoriteOnPositionWithInfoWi

    public void showMeteoritesDataFromToIndex(int from, int to) {
        if(m_googleMap != null && m_meteorites.size() > to) {
            m_googleMap.clear();

            addMeteoritesMarkers(m_meteorites.subList(from, to));
        }
    }

    public void showMeteoriteWithInfoWindow(int position) {
        hideInfoWindowIfPossible();
        m_googleMap.clear();

        Meteorite meteorite = m_meteorites.get(position);

        Marker marker = addMeteoriteMarkerWithInfoWindow(meteorite);
        animateCameraToInfoWindow(marker);
    }

    private void showMeteoriteWithInfoWindow(Marker marker) {
        hideInfoWindowIfPossible();
        m_googleMap.clear();

        marker = addMarker(marker.getTitle(), marker.getPosition());

        openInfoWindow(marker);
        animateCameraToInfoWindow(marker);
    }

    private void openInfoWindow(Marker marker) {
        m_lastMarkerWithInfoWindow = marker;
        marker.showInfoWindow();
    }

    private void addMeteoritesMarkers(List<Meteorite> meteorites) {
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

        for (Meteorite meteorite : meteorites) {
            Marker marker = addMeteoriteMarker(meteorite);
            latLngBuilder.include(marker.getPosition());
        }

        animateCameraToZoomOnMarkerBounds(latLngBuilder);
    }

    private Marker addMeteoriteMarkerWithInfoWindow(Meteorite meteorite) {
        Marker marker = addMeteoriteMarker(meteorite);
        openInfoWindow(marker);
        return marker;
    }

    private Marker addMeteoriteMarker(Meteorite meteorite) {
        double langtitude = Double.parseDouble(meteorite.getLangtitude());
        double longtitude = Double.parseDouble(meteorite.getLongtitude());
        LatLng position = new LatLng(langtitude, longtitude);

        return addMarker(meteorite.getName(), position);
    }

    private Marker addMarker(String title, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker());

        return m_googleMap.addMarker(markerOptions);
    }

    private void animateCameraToInfoWindow(Marker marker) {
        RelativeLayout container = (RelativeLayout) getView().findViewById(R.id.map_container);
        int containerHeight = container.getHeight();

        Projection projection = m_googleMap.getProjection();
        LatLng markerPos = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        Point markerScreenPosition = projection.toScreenLocation(markerPos);

        int x = markerScreenPosition.x;
        int y = markerScreenPosition.y - (containerHeight / 3);
        Point pointAboveMarker = new Point(x,y);

        LatLng aboveMarkerLatLng = projection.fromScreenLocation(pointAboveMarker);
        LatLngBounds.Builder builder = new LatLngBounds.Builder().include(aboveMarkerLatLng);

        LatLngBounds bounds = MapUtils.createBoundsWithMaxZoom(builder, 500000);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 5);
        m_googleMap.animateCamera(cu);

        CameraUpdate center = CameraUpdateFactory.newLatLng(aboveMarkerLatLng);
        m_googleMap.animateCamera(center);
    }

    private void animateCameraToZoomOnMarkerBounds(LatLngBounds.Builder builder) {
        LatLngBounds bounds = MapUtils.createBoundsWithMaxZoom(builder, 500000);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 5);
        m_googleMap.animateCamera(cu);
    }

    private void hideInfoWindowIfPossible() {
        if(m_lastMarkerWithInfoWindow != null) {
            m_lastMarkerWithInfoWindow.hideInfoWindow();
            m_lastMarkerWithInfoWindow = null;
        }
    }

}
