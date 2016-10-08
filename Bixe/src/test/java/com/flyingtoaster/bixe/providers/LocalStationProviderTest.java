package com.flyingtoaster.bixe.providers;

import com.flyingtoaster.bixe.BixeTestRunner;
import com.flyingtoaster.bixe.datasets.StationDataSource;
import com.flyingtoaster.bixe.models.Station;
import com.google.android.collect.Lists;

import org.fest.assertions.data.Index;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocalStationProviderTest {

    @Mock
    StationDataSource mDataSource;

    LocalStationProvider mSubject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mSubject = new LocalStationProvider(mDataSource);
    }

    @Test
    public void getStations_shouldGetStationsFromDataSource() {
        Station station = mock(Station.class);
        when(mDataSource.getAllStations()).thenReturn(Lists.newArrayList(station));
        TestObserver<List<Station>> observer = new TestObserver<>();

        Observable<List<Station>> observable = mSubject.getStations();
        observable.subscribe(observer);

        verify(mDataSource).open();
        verify(mDataSource).close();
        List<List<Station>> emittedResponse = observer.values();
        assertThat(emittedResponse).isNotEmpty();
        List<Station> stationList = emittedResponse.get(0);
        assertThat(stationList).contains(station, Index.atIndex(0));
    }

    @Test
    public void putStations_shouldPutStationsInDataSource() {
        Station station = mock(Station.class);
        List<Station> stations = Lists.newArrayList(station);
        TestObserver<List<Station>> observer = new TestObserver<>();

        Observable<List<Station>> observable = mSubject.putStations(stations);
        observable.subscribe(observer);

        verify(mDataSource).open();
        verify(mDataSource).close();
        verify(mDataSource).putStations(stations);
    }
}