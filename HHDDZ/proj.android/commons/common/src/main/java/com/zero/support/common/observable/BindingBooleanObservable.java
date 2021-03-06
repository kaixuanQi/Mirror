package com.zero.support.common.observable;


import androidx.databinding.ObservableBoolean;

import com.zero.support.work.Observable;
import com.zero.support.work.Observer;

public class BindingBooleanObservable extends ObservableBoolean implements Observer<Boolean> {
    private final Observable<Boolean> observable;

    public BindingBooleanObservable(Observable<Boolean> observable) {
        this.observable = observable;
        observable.observe(this, true);
    }

    @Override
    public void onChanged(Boolean aBoolean) {
        super.set(aBoolean);
    }

    @Override
    public void set(boolean value) {
        observable.setValue(value);
    }

    public void close() {
        observable.remove(this);
    }
}
