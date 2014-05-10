package support;

public class Location {
	public Double latitude;
    public Double longitude;

    public Location(Double lat,Double lng)
    {
        latitude = lat;
        longitude  = lng;
    }

    @Override
    public String toString()
    {
        return "Latitude: "+latitude+" & Longitude: "+longitude;
    }
}
