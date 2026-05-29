package com.anamika.spintrail.util;

import org.locationtech.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.List;

public class PolylineDecoder {

    // Decodes a Google-encoded polyline string into a list of [lat, lng] pairs
    // ORS uses this format by default for route geometry
    public static List<double[]> decode(String encoded) {
        List<double[]> points = new ArrayList<>();
        int index = 0;
        int len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            // Decode latitude
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dLat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dLat;

            // Decode longitude
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dLng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dLng;

            points.add(new double[]{lat / 1e5, lng / 1e5});
        }

        return points;
    }

    // Converts decoded points into JTS Coordinates (lng, lat) for PostGIS
    public static Coordinate[] toJtsCoordinates(List<double[]> points) {
        return points.stream()
                .map(p -> new Coordinate(p[1], p[0]))  // (lng, lat) for JTS
                .toArray(Coordinate[]::new);
    }
}