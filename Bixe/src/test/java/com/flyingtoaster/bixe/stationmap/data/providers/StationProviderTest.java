package com.flyingtoaster.bixe.stationmap.data.providers;

import com.flyingtoaster.bixe.TestBixeApplication;
import com.flyingtoaster.bixe.stationmap.data.clients.StationClient;
import com.flyingtoaster.bixe.stationmap.data.database.StationDataSource;
import com.flyingtoaster.bixe.stationmap.models.Station;

import org.fest.assertions.data.Index;
import org.fest.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StationProviderTest {

    @Mock
    private StationClient mClient;
    @Mock
    private StationDataSource mDataSource;

    private StationProvider mSubject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mSubject = new StationProvider(mClient, mDataSource, Schedulers.trampoline(), Schedulers.trampoline());
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

    @Test
    public void getStations_shouldGetStationsFromDataSource() {
        Station station = mock(Station.class);
        when(mDataSource.getAllStations()).thenReturn(Lists.newArrayList(station));
        TestObserver<List<Station>> observer = new TestObserver<>();

        Observable<List<Station>> observable = mSubject.getStationsLocal();
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

        Observable<List<Station>> observable = mSubject.putStationsLocal(stations);
        observable.subscribe(observer);

        verify(mDataSource).open();
        verify(mDataSource).close();
        verify(mDataSource).putStations(stations);
    }
}