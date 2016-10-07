package clients;

import com.flyingtoaster.bixe.models.Station;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

public class StationClient extends OkHttpClient {

    private static final String API_URL = "http://www.bikesharetoronto.com/stations/json";


    public List<Station> getStations() {
        return null;
    }
}
