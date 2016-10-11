package com.flyingtoaster.bixe.schedulers;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

@Module
public class TestObservableModule {

    @Provides
    @MainThreadScheduler
    public Scheduler provideMainThreadScheduler() {
        return Schedulers.trampoline();
    }

    @Provides
    @IOScheduler
    public Scheduler provideIOThreadScheduler() {
        return Schedulers.trampoline();
    }

    @Provides
    @ComputationScheduler
    public Scheduler provideComputationThreadScheduler() {
        return Schedulers.trampoline();
    }
}
