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
public interface GenericProcedure<I,O>  {
    public O perform (I i) throws Exception;
}
