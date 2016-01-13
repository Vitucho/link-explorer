/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure;

public interface EnumerableProcedureStatus<T extends Enum<?>> extends ProcedureStatus  {

    public T getCurrentPhase();

}
