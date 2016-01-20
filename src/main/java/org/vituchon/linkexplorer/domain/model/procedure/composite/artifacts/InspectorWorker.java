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
    private final MultiPageLinkInspector multiPageLinkInspector;
    private volatile String currentUrl;
    private final AtomicInteger inspectedCount;

    private final static AtomicInteger NAME_COUNT = new AtomicInteger(0);
    private final static Logger LOGGER = Logger.getLogger(InspectorWorker.class.getName());

    InspectorWorker(String customName, WorkerDirective workerDirective, final MultiPageLinkInspector multiPageLinkInspector) {
        this.name = customName;
        this.multiPageLinkInspector = multiPageLinkInspector;
        this.workerDirective = workerDirective;
        this.inspectedCount = new AtomicInteger(0);
    }

    public InspectorWorker(WorkerDirective workerDirective, final MultiPageLinkInspector multiPageLinkInspector) {
        this("duende - " + NAME_COUNT.incrementAndGet(), workerDirective, multiPageLinkInspector);
        this.currentUrl = null;
    }

    @Override
    public Void call() throws Exception {
        SinglePageLinkInspector singlePageLinkInspector = SinglePageLinkInspector.newInstance();
        try {
            this.currentUrl = multiPageLinkInspector.askNextUrl(this);
            while (this.currentUrl != null && multiPageLinkInspector.allowWork() && workerDirective.hasToWork(this)) {
                multiPageLinkInspector.notifyStart(currentUrl, this);
                GenericProcedureExecutor<String, Collection<String>> executorHelper = new GenericProcedureExecutor(singlePageLinkInspector, currentUrl);
                executorHelper.execute();
                ProcedureStatus lastStatus = executorHelper.getLastStatus();
                multiPageLinkInspector.setInspectionStatus(currentUrl, lastStatus, this);
                while (!lastStatus.isDone()) {
                    Thread.sleep(20);
                    lastStatus = executorHelper.getLastStatus();
                    multiPageLinkInspector.setInspectionStatus(currentUrl, lastStatus, this);
                }
                try {
                    Collection<String> links = executorHelper.getOutput();
                    LOGGER.log(Level.INFO, "Worker {0} found in {1} these links : {2}", new Object[]{name, currentUrl, links.toString()});
                    multiPageLinkInspector.notifyEnd(currentUrl, links, this);
                } catch (ExecutionException ignore) {
                    LOGGER.log(Level.WARNING, "Worker {0} has problems : {1} ", new Object[]{name, ignore.toString()});
                }
                this.currentUrl = multiPageLinkInspector.askNextUrl(this);
            }
        } catch (InterruptedException escape) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public AtomicInteger getInspectedCount() {
        return inspectedCount;
    }

    public HtmlMap getPartialHtmlMap() {
        return this.multiPageLinkInspector.getPartialHtmlMap(); // TODO:tendria que retornar una copia!!!!
    }

    public String getName() {
        return name;
    }
}
