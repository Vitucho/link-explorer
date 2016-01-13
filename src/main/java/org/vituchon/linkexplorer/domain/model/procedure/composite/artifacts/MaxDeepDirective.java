/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts;

/**
 *
 * @author Administrador
 */
public class MaxDeepDirective extends BaseWorkerDirective {

    private final int maxDeep;

    public static MaxDeepDirective newInstance (int maxDeep) {
        return new MaxDeepDirective(maxDeep);
    }
    
    private MaxDeepDirective(int maxDeep) {
        this.maxDeep = maxDeep;
    }

    @Override
    protected boolean isAllowToWork(InspectorWorker worker) {
        return worker.getPartialHtmlMap().distanceToRoot(worker.getCurrentUrl()) <= maxDeep;
    }

}
