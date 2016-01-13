/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.composite;

import org.vituchon.linkexplorer.domain.model.procedure.ProcedureStatus;

/**
 *
 * @author Administrador
 */
public class NullProcedureStatus implements ProcedureStatus {

    public static final NullProcedureStatus INSTANCE = new NullProcedureStatus();

    protected NullProcedureStatus() {
    }
    
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public long getEnd() {
        return 0;
    }

    @Override
    public long getStart() {
        return 0;
    }

    @Override
    public String toString() {
        return "Null status";
    }

}
