package com.flyingtoaster.bixe.datasets;

import android.database.Cursor;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.BixeTestRunner;
import com.flyingtoaster.bixe.FakeDataUtil;
import com.flyingtoaster.bixe.activities.ContentResolverActivity;

import com.flyingtoaster.bixe.datasets.BixeContentProvider;
import com.flyingtoaster.bixe.datasets.StationDataSource;
import com.flyingtoaster.bixe.models.Station;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(BixeTestRunner.class)

public class BixeContentResolverTest {

    ActivityController<ContentResolverActivity> mController;

    @Before
    public void setup() {
        mController = Robolectric.buildActivity(ContentResolverActivity.class);
    }

    @Test
    public void shouldRegisterContentObserver() {
        ContentResolverActivity activity;
        activity = mController.create().start().resume().get();

        assertThat(activity.getContentObserver()).isNotNull();
    }

    @Test
    public void contentObserverShouldNotifyOnUpdate() {
        ContentResolverActivity activity;
        activity = mController.create().start().resume().get();
        Station stationToInsert = FakeDataUtil.getStation();
        StationDataSource dataSource = getStationDataSource();
        dataSource.open();

        dataSource.createStation(stationToInsert);

        assertThat(activity.didUpdate()).isTrue();
    }

    private StationDataSource getStationDataSource() {
        StationDataSource dataSource = new StationDataSource(BixeApplication.getAppContext());
        return dataSource;
    }
}
