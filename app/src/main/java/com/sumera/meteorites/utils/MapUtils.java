package com.sumera.meteorites.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by martin on 07/09/16.
 */

public class MapUtils {

    private static final double EARTH_RADIUS = 6366198;

    /**
     * Calculate north-east and south-west corner points which ensures max zoom to bounds
     * and add to builder
     */
    public static LatLngBounds createBoundsWithMaxZoom(LatLngBounds.Builder builder, int minDistanceFromCenter) {
        LatLngBounds bounds = builder.build();

        LatLng center = bounds.getCenter();
        LatLng northEast = move(center, minDistanceFromCenter, minDistanceFromCenter);
        LatLng southWest = move(center, -minDistanceFromCenter, -minDistanceFromCenter);
        builder.include(southWest);
        builder.include(northEast);

        return builder.build();
    }

    /**
     * Create new LatLng which is located in north-east direction from start point
     */
    private static LatLng move(LatLng startPoint, double distanceToNorth, double distanceToEast) {
        double lonDiff = meterToLongitude(distanceToEast, startPoint.latitude);
        double latDiff = meterToLatitude(distanceToNorth);
        return new LatLng(startPoint.latitude + latDiff, startPoint.longitude + lonDiff);
    }

    private static double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTH_RADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }

    private static double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTH_RADIUS;
        return Math.toDegrees(rad);
    }

}
