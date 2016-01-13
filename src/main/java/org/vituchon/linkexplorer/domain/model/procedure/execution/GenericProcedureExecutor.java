/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.execution;

import org.vituchon.linkexplorer.domain.model.procedure.GenericProcedure;
import org.vituchon.linkexplorer.domain.model.procedure.GenericQueryableProcedure;
import org.vituchon.linkexplorer.domain.model.procedure.ProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.QueryableProcedure;
import org.vituchon.linkexplorer.domain.model.procedure.composite.NullProcedureStatus;
import java.util.concurrent.*;

/**
 * Leverages the task of invoking a procedure with an associated monitor object that tracks his progress updating the status that can be queried at any time.
 */
public class GenericProcedureExecutor<I, O> {

    private final GenericProcedure<I, O> genericProcedure;
    private final QueryableProcedure queryableProcedure;
    private final I input;
    private Future<O> procedureFuture;
    private final GenericProcedureListener<O> outputListener;
    private volatile ProcedureStatus lastStatus;
    private static final int POLLING_PERIOD = 25;

    public GenericProcedureExecutor(GenericQueryableProcedure<I,O> genericQueryableProcedure, I input, GenericProcedureListener<O> outputListener) {
        this.genericProcedure = genericQueryableProcedure;
        this.queryableProcedure = genericQueryableProcedure;
        this.input = input;
        this.outputListener = outputListener;
        this.lastStatus = NullProcedureStatus.INSTANCE;
    }

    public GenericProcedureExecutor(GenericQueryableProcedure<I,O> genericQueryableProcedure, I input) {
        this(genericQueryableProcedure, input, (GenericProcedureListener<O>) NullProcedureListener.INSTANCE);
    }

    public void execute() {
        ProcedureCall<I> procedureCall = new ProcedureCall(genericProcedure, input);
        ProcedureStatusRefreshCall inspectStatusSneakCall = new ProcedureStatusRefreshCall(queryableProcedure, POLLING_PERIOD);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        procedureFuture = executorService.submit(procedureCall);
        executorService.submit(inspectStatusSneakCall);
        executorService.shutdown();
    }

    public O getOutput() throws InterruptedException, ExecutionException {
        if (procedureFuture == null) {
            throw new IllegalStateException("Procedure not started, unable to get or retireve any output.");
        } else {
            return procedureFuture.get();
        }
    }

    public ProcedureStatus getLastStatus() {
        return lastStatus;
    }

    private class ProcedureCall<I> implements Callable<O> {

        private final GenericProcedure<I, O> genericProcedure;
        private final I input;

        public ProcedureCall(GenericProcedure<I, O> genericProcedure, I input) {
            this.genericProcedure = genericProcedure;
            this.input = input;
        }

        @Override
        public O call() throws Exception {
            O output = null;
            try {
                output = genericProcedure.perform(this.input);
                outputListener.recieve(output);
            } catch (Exception e) {
                outputListener.report(e);
                throw e;
            }
            return output;
        }
    }

    private class ProcedureStatusRefreshCall implements Callable<Void> {

        private final int pollingPeriod;
        private final QueryableProcedure queryableProcedure;

        public ProcedureStatusRefreshCall(QueryableProcedure queryableProcedure, int pollingPeriod) {
            this.pollingPeriod = pollingPeriod;
            this.queryableProcedure = queryableProcedure;
        }

        @Override
        public Void call() throws Exception {
            lastStatus = queryableProcedure.getProcedureStatus();
            while (!lastStatus.isDone() && !Thread.interrupted()) {
                Thread.sleep(pollingPeriod);
                lastStatus = queryableProcedure.getProcedureStatus();
            }
            return null;
        }
    }

    private enum NullProcedureListener implements GenericProcedureListener<Object> {
        INSTANCE;

        @Override
        public void recieve(Object output) {
        }

        @Override
        public void report(Exception e) {
        }
    }
}
