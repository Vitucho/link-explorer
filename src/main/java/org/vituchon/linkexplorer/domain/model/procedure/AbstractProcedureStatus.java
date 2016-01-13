/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure;

/**
 * Provides base common implementation to classes that implements the ProcedureStatus interface.
 */
public abstract class AbstractProcedureStatus implements ProcedureStatus {
    
    protected volatile long start;
    protected volatile long end;
    
    @Override
    public boolean isDone() {
        return end != 0;
    }

    @Override
    public long getEnd() {
        return end;
    }

    @Override
    public long getStart() {
        return start;
    }
    
}
