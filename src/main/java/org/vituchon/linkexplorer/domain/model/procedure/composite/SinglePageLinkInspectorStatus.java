/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.composite;

import org.vituchon.linkexplorer.domain.model.procedure.AbstractProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.DiscreteProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.EnumerableProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.composite.SinglePageLinkInspectorStatus.Phase;
import org.vituchon.linkexplorer.domain.model.procedure.unit.QueryableDiscreteProcedure;

/**
 *
 * @author Administrador
 */
public class SinglePageLinkInspectorStatus extends AbstractProcedureStatus implements EnumerableProcedureStatus<Enum<Phase>>, QueryableDiscreteProcedure {

    private volatile DiscreteProcedureStatus discreteProcedureStatus = NullDiscreteProcedureStatus.INSTANCE;
    private volatile Phase phase = Phase.NONE;
    private volatile Throwable throwable = null;

    synchronized void start() {
        this.start = System.currentTimeMillis();
        setPhase(Phase.FECHT);
    }

    synchronized void end(Throwable t) {
        this.end = System.currentTimeMillis();
        this.throwable = t;
        setPhase(Phase.DONE);
    }

    synchronized void setDiscreteProcedureStatus(DiscreteProcedureStatus discreteProcedureStatus) {
        this.discreteProcedureStatus = discreteProcedureStatus;
    }

    synchronized void setPhase(Phase phase) {
        this.phase = phase;
        this.discreteProcedureStatus = NullDiscreteProcedureStatus.INSTANCE;
    }

    @Override
    public Enum<Phase> getCurrentPhase() {
        return phase;
    }

    @Override
    public DiscreteProcedureStatus getProcedureStatus() {
        return this.discreteProcedureStatus;
    }

    public Throwable getThrowable() {
        return throwable;
    }
    

    @Override
    public boolean isDone() {
        return Phase.DONE.equals(this.phase);
    }

    public static enum Phase {

        NONE, FECHT, SCAN, RESOLVE, DONE;
    }

    @Override
    public String toString() {

        return String.format("In phase %s -> %d of %d : %d%%.%s", 
                this.phase, this.discreteProcedureStatus.getCurrent(), 
                this.discreteProcedureStatus.getTotal(), this.discreteProcedureStatus.getPercentaje(), 
                this.throwable != null ? " Aborted due to : " +  this.throwable.getMessage() : "");
    }
}
