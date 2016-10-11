package com.flyingtoaster.bixe.schedulers;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Module
public class ObservableModule {

    @Provides
    @MainThreadScheduler
    public Scheduler provideMainThreadScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @IOScheduler
    public Scheduler provideIOThreadScheduler() {
        return Schedulers.io();
    }

    @Provides
    @ComputationScheduler
    public Scheduler provideComputationThreadScheduler() {
        return Schedulers.computation();
    }
}
