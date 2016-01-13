/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.unit;

import org.vituchon.linkexplorer.domain.model.procedure.DiscreteProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.QueryableProcedure;

/**
 *
 * @author Administrador
 */
public interface QueryableDiscreteProcedure extends QueryableProcedure {
    @Override
    public DiscreteProcedureStatus getProcedureStatus();
}
