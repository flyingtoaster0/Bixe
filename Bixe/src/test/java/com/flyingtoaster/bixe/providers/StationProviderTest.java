package com.flyingtoaster.bixe.providers;

import com.flyingtoaster.bixe.models.Station;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import com.flyingtoaster.bixe.clients.StationClient;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class StationProviderTest {

    @Mock
    private StationClient mClient;

    private StationProvider mSubject;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mSubject = new StationProvider(mClient);
    }

    @Test
    public void whenClientReturnWithEmptyList_onNextShouldReturnEmptyList() {
        when(mClient.getStations()).thenReturn(Collections.<Station>emptyList());
        TestObserver<List<Station>> testObserver = new TestObserver<>();

        Observable<List<Station>> stationsObservable = mSubject.getStations();
        stationsObservable.subscribe(testObserver);

        List<List<Station>> emittedResponse = testObserver.values();
        assertThat(emittedResponse).isNotEmpty();
        List<Station> stationList = emittedResponse.get(0);
        assertThat(stationList).isEmpty();
    }
}