/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.execution;

/**
 * The interface for those objects that attend to events that a GenericProcedure execution generates.
 * There two kinds of events that can happen, both are mutual exclusive, this means that only one of both can happen.
 * Each event correspont to a callback, whose signutare is declared in this interface.
 */
public interface GenericProcedureListener<O> {

    /**
     * Notifies this listener that the generic procedure was completed successfully and his output is passed as argument.
     * @param <O> The type of the output.
     * @param output The output of the genericProcedure.
     */
    public void recieve(O output);

    /**
     * Notifies this listener the occurrence of an exception, thus confirming that the procedure wasn't completed successfully.
     * @param e The exception thrown 
     */
    public void report(Exception e) ;
}
