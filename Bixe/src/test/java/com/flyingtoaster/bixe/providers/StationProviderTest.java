package com.flyingtoaster.bixe.providers;

import com.flyingtoaster.bixe.models.Station;

import org.fest.assertions.data.Index;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import com.flyingtoaster.bixe.clients.StationClient;
import com.google.android.collect.Lists;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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
    public void whenClientReturnsWithStations_onNextShouldReturnStations() {
        Station station = mock(Station.class);
        when(mClient.getStations()).thenReturn(Lists.newArrayList(station));
        TestObserver<List<Station>> observer = new TestObserver<>();

        Observable<List<Station>> observable = mSubject.getStations();
        observable.subscribe(observer);

        List<List<Station>> emittedResponse = observer.values();
        assertThat(emittedResponse).isNotEmpty();
        List<Station> stationList = emittedResponse.get(0);
        assertThat(stationList).contains(station, Index.atIndex(0));
    }
}