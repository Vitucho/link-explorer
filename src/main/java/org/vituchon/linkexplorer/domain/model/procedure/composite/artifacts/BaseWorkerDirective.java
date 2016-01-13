/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts;

/**
 *
 * @author Administrador
 */
public abstract class BaseWorkerDirective implements WorkerDirective {

    @Override
    public final boolean hasToWork(InspectorWorker worker) {
        return hasPendingWork(worker) && isAllowToWork(worker);
    }

    protected abstract boolean isAllowToWork(InspectorWorker worker);

    private boolean hasPendingWork(InspectorWorker worker) {
        return worker.getCurrentUrl() != null;
    }
}
