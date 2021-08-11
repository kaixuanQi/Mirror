package com.zero.support.work;


import android.util.Log;

public abstract class PromiseTask<Param, Result> extends Task<Param, Response<Result>> {
    private volatile boolean canceled;

    @Override
    public boolean cancel(boolean interrupt) {
        canceled = true;
        return super.cancel(interrupt);
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public Response<Result> doWork(Param input) {
        try {
            return Response.success(process(input));
        } catch (Throwable e) {
            e.printStackTrace();
            if (isCanceled()) {
                return Response.cancel(null);
            }
            return Response.error(WorkExceptionConverter.convert(e), e.getMessage(), e);
        }
    }

    protected abstract Result process(Param input) throws Throwable;
}
