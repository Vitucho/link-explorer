/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure;

public interface DiscreteProcedureStatus extends ProcedureStatus {

    public int getCurrent();

    public int getTotal();

    public int getPercentaje();
}
