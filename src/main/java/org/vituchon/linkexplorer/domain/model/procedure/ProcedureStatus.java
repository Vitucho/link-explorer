/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure;


public interface ProcedureStatus {
    public boolean isDone();

    public long getEnd();

    public long getStart();
}
