/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.composite;

import org.vituchon.linkexplorer.domain.model.procedure.DiscreteProcedureStatus;

/**
 *
 * @author Administrador
 */
public class NullDiscreteProcedureStatus extends NullProcedureStatus implements DiscreteProcedureStatus {

    public static final NullDiscreteProcedureStatus INSTANCE = new NullDiscreteProcedureStatus();

    private NullDiscreteProcedureStatus() {
    }
    
    
    @Override
    public int getCurrent() {
        return 0;
    }

    @Override
    public int getTotal() {
        return 0;
    }

    @Override
    public int getPercentaje() {
        return 0;
    }
    
}
