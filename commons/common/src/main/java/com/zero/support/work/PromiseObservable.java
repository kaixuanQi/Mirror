package com.zero.support.work;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * 保证所有的事件都会被消费, 消费完后返回空值
 *
 * @param <T>
 */
public class PromiseObservable<T> extends SerialObservable<T> {
    private final LinkedList<T> promises = new LinkedList<>();
    private T mValue;


    public synchronized List<T> values(){
        List<T> list =  new ArrayList<>(promises);
        if (mValue!=null){
            list.add(mValue);
        }
        return list;
    }
    public PromiseObservable() {
        super(AppExecutor.main());
    }

    public PromiseObservable(Executor executor) {
        super(executor);
    }

    public synchronized boolean remove(T t) {
        if (t == mValue) {
            mValue = null;
            promiseNext();
            return true;
        }
        return promises.remove(t);
    }

    private void promiseNext() {
        if (mValue == null) {
            if (promises.size()!=0){
                mValue = promises.removeLast();
            }
            super.setValue(mValue);
        }
    }

    @Override
    public synchronized void setValue(T value) {
        //super.setValue(value);
        promise(value);
    }

    private synchronized void promise(T value) {
        promises.addFirst(value);
        promiseNext();
    }


    public boolean isPromise(T value) {
        return mValue==value;
    }

    public synchronized boolean contains(T value) {
        return mValue==value||promises.contains(value);
    }
}
