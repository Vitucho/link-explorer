/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts;

import org.vituchon.linkexplorer.domain.model.procedure.ProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.execution.GenericProcedureExecutor;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vituchon.linkexplorer.domain.model.procedure.composite.SinglePageLinkInspector;

/**
 *
 * @author Administrador
 */
public class InspectorWorker implements Callable<Void> {
    
    private final String name;
    private final WorkerDirective workerDirective;
    private final MultiPageLinkInspector owner;
    private volatile String currentUrl;
    private final AtomicInteger inspectedCount;
    
    private final static AtomicInteger NAME_COUNT = new AtomicInteger(0);
    private final static Logger LOGGER = Logger.getLogger(InspectorWorker.class.getName());

    public InspectorWorker(WorkerDirective workerDirective, final MultiPageLinkInspector owner) {
        this.owner = owner;
        this.workerDirective = workerDirective;
        this.currentUrl = null;
        this.inspectedCount = new AtomicInteger(0);
        this.name = "worker_"  + NAME_COUNT.incrementAndGet();
    }

    @Override
    public Void call() throws Exception {
        SinglePageLinkInspector singlePageLinkInspector = SinglePageLinkInspector.newInstance();
        try {
            this.currentUrl = owner.askNextUrl(this);
            while (owner.allowWork() && workerDirective.hasToWork(this)) {
                owner.notifyStart(currentUrl);
                GenericProcedureExecutor<String, Collection<String>> executorHelper = new GenericProcedureExecutor(singlePageLinkInspector, currentUrl);
                executorHelper.execute();
                ProcedureStatus lastStatus = executorHelper.getLastStatus();
                owner.setInspectionStatus(currentUrl, lastStatus);
                while (!lastStatus.isDone()) {
                    Thread.sleep(20);
                    lastStatus = executorHelper.getLastStatus();
                    owner.setInspectionStatus(currentUrl, lastStatus);
                }
                try {
                    Collection<String> links = executorHelper.getOutput();
                    LOGGER.log(Level.INFO, "Worker {0} found in {1} these links : {2}", new Object[]{name, currentUrl, links.toString()});
                    owner.notifyEnd(currentUrl,links);
                }
                catch (ExecutionException ignore) {
                    LOGGER.log(Level.WARNING, "Worker {0} has problems : {1} ", new Object[]{name, ignore.toString()});
                }
                inspectedCount.incrementAndGet();
                this.currentUrl = owner.askNextUrl(this);
            }
        } catch (InterruptedException escape) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public int getInspectedCount() {
        return inspectedCount.get();
    }
    
    public HtmlMap getPartialHtmlMap() {
        return this.owner.getPartialHtmlMap(); // TODO:tendria que retornar una copia!!!!
    }

    public String getName() {
        return name;
    }
}
