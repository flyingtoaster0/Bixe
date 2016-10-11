package com.flyingtoaster.bixe.stationmap.ui;

import com.flyingtoaster.bixe.stationmap.data.providers.StationProvider;
import com.flyingtoaster.bixe.stationmap.models.Station;
import com.flyingtoaster.bixe.utils.StationFormatter;

import org.fest.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StationMapPresenterTest {

    @Mock
    private StationProvider mStationProvider;
    @Mock
    private Station mStation;
    @Mock
    private StationMapContract.View mView;
    @Mock
    private StationFormatter mStationFormatter;

    private StationMapPresenter mSubject;
    Observable<List<Station>> mLocalStationsObservable;
    Observable<List<Station>> mNetworkStationsObservable;

    TestObserver<List<Station>> mTestObserver;
    List<Station> mLocalStations;
    List<Station> mNetworkStations;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mTestObserver = new TestObserver<>();
        mLocalStations = Lists.newArrayList(mock(Station.class));
        mNetworkStations = Lists.newArrayList(mock(Station.class), mock(Station.class));
        mLocalStationsObservable = Observable.just(mLocalStations);
        mNetworkStationsObservable = Observable.just(mNetworkStations);
        when(mStationProvider.getStationsLocal()).thenReturn(mLocalStationsObservable);
        when(mStationProvider.getStations()).thenReturn(mNetworkStationsObservable);
        when(mStationProvider.putStationsLocal(mNetworkStations)).thenReturn(mNetworkStationsObservable);

        mSubject = new StationMapPresenter(mStationProvider, mStationFormatter);
        mSubject.attachView(mView);
    }

    @Test
    public void refreshStations_shouldGetStoredStations() {
        mSubject.refreshStations();

        verify(mStationProvider).getStationsLocal();
    }

    @Test
    public void refreshStations_shouldNotifyViewOfStoredMarkers() {
        mSubject.refreshStations();
        mLocalStationsObservable.subscribe(mTestObserver);
        mTestObserver.onNext(mLocalStations);

        verify(mView).updateMarkers(mLocalStations);
    }

    @Test
    public void refreshStations_shouldGetNewStationsFromNetwork() {
        mSubject.refreshStations();
        mLocalStationsObservable.subscribe(mTestObserver);
        mTestObserver.onNext(mLocalStations);

        verify(mStationProvider).getStations();
    }

    @Test
    public void refreshStations_shouldPutNewMarkersToDataSource() {
        mSubject.refreshStations();
        mLocalStationsObservable.subscribe(mTestObserver);
        mTestObserver.onNext(mLocalStations);

        verify(mStationProvider).putStationsLocal(mNetworkStations);
    }

    @Test
    public void refreshStations_shouldNotifyViewOfNewMarkers() {
        mSubject.refreshStations();
        mLocalStationsObservable.subscribe(mTestObserver);
        mTestObserver.onNext(mLocalStations);

        verify(mView).updateMarkers(mNetworkStations);
    }

    @Test
    public void refreshStations_shouldShowLoading() {
        mSubject.refreshStations();

        verify(mView).showLoading();
    }

    @Test
    public void whenRefreshCompletes_refreshStations_shouldHideLoading() {
        mSubject.refreshStations();
        mLocalStationsObservable.subscribe(mTestObserver);
        mTestObserver.onNext(mLocalStations);

        verify(mView).hideLoading();
    }

    @Test
    public void onStationSelect_notifiesViewWithFormattedStationName_bikes_andDocks() {
        when(mStationFormatter.getFormattedStationName(mStation)).thenReturn("Bathurst and Front");
        when(mStation.getAvailableBikes()).thenReturn(6);
        when(mStation.getAvailableDocks()).thenReturn(4);

        mSubject.onStationSelect(mStation);

        verify(mView).updateSelectedStationView("Bathurst and Front", "6", "4");
    }
}