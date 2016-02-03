package com.ontotext.ehri.georecon.place;

import com.ontotext.ehri.georecon.Tools;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A place from GeoNames.
 */
public class Place implements Comparable<Place>, Serializable {

    // coordinates of reference point: http://sws.geonames.org/2950159/ (Berlin)
    private static final double REF_POINT_LAT = 52.52437;
    private static final double REF_POINT_LON = 13.41053;

    private int geoID;
    private double latitude, longitude;
    private long population;
    private PlaceType type;
    private Place parent;

    public Place(int geoID, double latitude, double longitude, long population, PlaceType type, Place parent) {
        this.geoID = geoID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.population = population;
        this.type = type;
        this.parent = parent;
    }

    public int getGeoID() {
        return geoID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getPopulation() {
        return population;
    }

    public PlaceType getType() {
        return type;
    }

    public Place getParent() {
        return parent;
    }

    /**
     * Calculate the distance in kilometers between this place and a point on the map.
     * @param latitude The latitude of the point.
     * @param longitude The longitude of the point.
     * @return The distance in kilometers.
     */
    public double distanceTo(double latitude, double longitude) {
        return Tools.distance(this.latitude, this.longitude, latitude, longitude, "K");
    }

    /**
     * Calculate the distance in kilometers between this place and some other place.
     * @param other The other place.
     * @return The distance in kilometers.
     */
    public double distanceTo(Place other) {
        return distanceTo(other.latitude, other.longitude);
    }

    /**
     * Compare this place to another place.
     * @param o The other place.
     * @return -1 if this place is more relevant; +1 if this place is less relevant; 0 if they are the same.
     */
    public int compareTo(Place o) {
        if (this == o) return 0;
        if (geoID == o.geoID) return 0;

        // prefer countries
        if (type == PlaceType.PCL && o.type != PlaceType.PCL) return -1;
        else if (type != PlaceType.PCL && o.type == PlaceType.PCL) return 1;

        // prefer populated places
        if (type == PlaceType.PPL && o.type != PlaceType.PPL) return -1;
        else if (type != PlaceType.PPL && o.type == PlaceType.PPL) return 1;

        // prefer administrative divisions
        if (type == PlaceType.ADM && o.type != PlaceType.ADM) return -1;
        else if (type != PlaceType.ADM && o.type == PlaceType.ADM) return 1;

        // prefer more populated places
        if (population > o.population) return -1;
        else if (population < o.population) return 1;

        // prefer places closer to the reference point
        double myDist = distanceTo(REF_POINT_LAT, REF_POINT_LON);
        double oDist = o.distanceTo(REF_POINT_LAT, REF_POINT_LON);
        if (myDist < oDist) return -1;
        else if (myDist > oDist) return 1;

        // last resort
        return Integer.compare(geoID, o.geoID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        // two places are equal if they have the same GeoNames ID
        Place place = (Place) o;
        return geoID == place.geoID;
    }

    @Override
    public int hashCode() {
        return geoID;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Place{");
        sb.append("geoID=").append(geoID);
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", population=").append(population);
        sb.append(", type=").append(type);
        sb.append(", parent=").append(parent);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Construct the GeoNames URL of this place.
     * @return The GeoNames URL of this place.
     * @throws MalformedURLException
     */
    public URL toURL() throws MalformedURLException {
        return new URL("http://sws.geonames.org/" + geoID + "/");
    }
}
