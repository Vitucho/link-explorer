/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts;

import org.vituchon.linkexplorer.domain.model.procedure.ProcedureStatus;

/**
 *
 * @author victor
 */
public class SinglePageExplorationStatus {

    private final ProcedureStatus status;
    private final InspectorWorker worker;

    public SinglePageExplorationStatus(ProcedureStatus status, InspectorWorker worker) {
        this.status = status;
        this.worker = worker;
    }

    public ProcedureStatus getStatus() {
        return status;
    }

    public InspectorWorker getWorker() {
        return worker;
    }

    @Override
    public String toString() {
        return status.toString() + " by " + worker.getName() + " paginas inspeccionadas " + worker.getInspectedCount();
    }

}
