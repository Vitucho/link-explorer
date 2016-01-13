/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure;

/**
 * Declares a generic procedure behaviour.
 * @param <O> output
 * @param <I> input
 */
public interface GenericQueryableProcedure<I,O> extends GenericProcedure<I, O>, QueryableProcedure {
}
