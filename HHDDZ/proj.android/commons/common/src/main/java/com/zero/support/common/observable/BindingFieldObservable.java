package com.zero.support.common.observable;


import androidx.databinding.ObservableField;

import com.zero.support.work.Observable;
import com.zero.support.work.Observer;

public class BindingFieldObservable<T> extends ObservableField<T> implements Observer<T> {
    private final Observable<T> observable;

    public BindingFieldObservable(Observable<T> observable) {
        this.observable = observable;
        observable.observe(this, true);
    }

    @Override
    public void onChanged(T t) {
        super.set(t);
    }

    @Override
    public void set(T value) {
        if (value!=get()){
            observable.setValue(value);
        }
    }

    public void close() {
        observable.remove(this);
    }
}
