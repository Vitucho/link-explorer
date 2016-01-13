/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure;

/**
 *
 * @author Administrador
 */
public interface QueueableProcedure<I,O> {
    
    public void offer (I... input);
    public O take ();
}
