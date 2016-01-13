/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure;

/**
 * Declares behavoiur for procedures instantes whose state is requested by clients.
 */
public interface QueryableProcedure {
    public ProcedureStatus getProcedureStatus();
}
