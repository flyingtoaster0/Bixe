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

@RunWith(BixeTestRunner.class)
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
    public void getStationsShouldGetStationsFromDataSource() {
        Station station = mock(Station.class);
        when(mDataSource.getAllStations()).thenReturn(Lists.newArrayList(station));
        TestObserver<List<Station>> observer = new TestObserver<>();

        Observable<List<Station>> observable = mSubject.getStations();
        observable.subscribe(observer);

        List<List<Station>> emittedResponse = observer.values();
        assertThat(emittedResponse).isNotEmpty();
        List<Station> stationList = emittedResponse.get(0);
        assertThat(stationList).contains(station, Index.atIndex(0));
        verify(mDataSource).open();
        verify(mDataSource).close();
    }
}