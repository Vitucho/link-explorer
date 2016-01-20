/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts;

import org.vituchon.linkexplorer.domain.model.procedure.GenericQueryableProcedure;
import org.vituchon.linkexplorer.domain.model.procedure.ProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.execution.GenericProcedureExecutor;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vituchon.linkexplorer.domain.model.procedure.composite.NullProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.composite.SinglePageLinkInspector;

public class MultiPageLinkInspector implements GenericQueryableProcedure<String, HtmlMap> {

    private static final int MAX_INSPECTIONS = 200;
    private final BlockingQueue<String> urlsToAnalize;
    private final ConcurrentHashMap<String, Boolean> takenUrls;
    private final MultiPageLinkInspectorStatus status;
    private final WorkerDirective workerDirective;
    private final int workers;
    private HtmlMap htmlMap;

    private static final Logger LOGGER = Logger.getLogger(MultiPageLinkInspector.class.getName());

    public MultiPageLinkInspector(WorkerDirective workerDirective, int workers) {
        if (workerDirective == null) {
            throw new IllegalArgumentException("workerDirective can not be null");
        }
        this.urlsToAnalize = new LinkedBlockingDeque<>();
        this.status = new MultiPageLinkInspectorStatus();
        this.workerDirective = workerDirective;
        this.workers = workers;
        this.takenUrls = new ConcurrentHashMap<>();
    }

    @Override
    public HtmlMap perform(String baseUrl) throws Exception {
        htmlMap = null;
        try {
            status.start();
            createMap(baseUrl);
            fillMap();
        } finally {
            status.end();
        }
        return htmlMap;
    }

    HtmlMap createMap(String baseUrl) throws Exception {
        InspectorWorker rootInspectorWorker = new InspectorWorker("Papa duende", workerDirective, this);
        SinglePageLinkInspector singlePageLinkInspector = SinglePageLinkInspector.newInstance();
        GenericProcedureExecutor<String, Collection<String>> executorHelper = new GenericProcedureExecutor(singlePageLinkInspector, baseUrl);
        htmlMap = new HtmlMap(baseUrl);
        notifyStart(baseUrl, rootInspectorWorker);
        executorHelper.execute();
        ProcedureStatus lastStatus = executorHelper.getLastStatus();
        setInspectionStatus(baseUrl, lastStatus, rootInspectorWorker);
        while (!lastStatus.isDone()) {
            Thread.sleep(20);
            lastStatus = executorHelper.getLastStatus();
            setInspectionStatus(baseUrl, lastStatus, rootInspectorWorker);
        }
        Collection<String> links = executorHelper.getOutput();
        notifyEnd(baseUrl, links, rootInspectorWorker);
        return htmlMap; // TODO EVALUAR DISENO ACA... VARIABLE QUE SE USA PARA REFENCIAR UN OBJETO CALIENTE... EN EL SENTIDO DE QUE SE VA CONSTRUYENDO
    }

    private static final int UNITS_AWAIT_FOR_TERMINATION = 1;
    private static final TimeUnit TIME_UNIT_AWAIT_FOR_TERMINATION = TimeUnit.MINUTES;

    private void fillMap() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(this.workers);
        for (int i = 0; i < this.workers; i++) {
            executorService.submit(new InspectorWorker(workerDirective, this));
        }
        executorService.shutdown();
        try {
            LOGGER.info("Waiting fot the inspector worker executor servive to finish 1 ...");
            boolean finish = executorService.awaitTermination(UNITS_AWAIT_FOR_TERMINATION, TIME_UNIT_AWAIT_FOR_TERMINATION);
            LOGGER.log(Level.INFO, "Inspector worker executor servive to status after waiting 1 is : finish = {0}", finish);
            if (!finish) {
                LOGGER.warning("Waiting fot the inspector worker executor servive to finish 2 ...");
                List<Runnable> shutdownNow = executorService.shutdownNow();
                finish = executorService.awaitTermination(UNITS_AWAIT_FOR_TERMINATION, TIME_UNIT_AWAIT_FOR_TERMINATION);
                LOGGER.log(Level.WARNING, "Inspector worker executor servive to status after waiting 2 is : finish = {0} cancelled task size = {1}", new Object[]{shutdownNow, finish});
                if (!finish) {
                    LOGGER.severe("What the hack.. the executor doesn't finish properly");
                }
            }
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Excecion reported during await of termination.", e);
        }
    }

    @Override
    public MultiPageLinkInspectorStatus getProcedureStatus() {
        return status;
    }

    void setInspectionStatus(String url, ProcedureStatus status, InspectorWorker who) {
        this.status.setInspectionStatus(url, status, who);
    }

    String askNextUrl(InspectorWorker who) throws InterruptedException {
        LOGGER.log(Level.INFO, "Inspector worker {0} waiting", who.getName());
        String url = this.urlsToAnalize.poll(2L, TimeUnit.SECONDS);
        LOGGER.log(Level.INFO, "Inspector worker {0} will take {1}", new Object[]{who.getName(), url});
        if (url == null) {
            LOGGER.log(Level.WARNING, "url nula! {0}", url);

        } else if (takenUrls.containsKey(url)) {
            LOGGER.log(Level.WARNING, "url repetida! {0}", url);
        }
        return url;
    }

     synchronized void notifyEnd(String url, Collection<String> links, InspectorWorker who) {
        who.getInspectedCount().incrementAndGet();
        for (String link : links) {
            if (!url.equals(link)) {
                this.htmlMap.addLink(url, link);
                if (!this.takenUrls.containsKey(link)) {
                    this.urlsToAnalize.add(link);
                }
            }
        }
    }

    void notifyStart(String url, InspectorWorker who) {
        takenUrls.put(url, true);
        this.status.setInspectionStatus(url, NullProcedureStatus.INSTANCE, who);
    }

    public boolean allowWork() {
        return takenUrls.size() < MAX_INSPECTIONS;
    }

    public synchronized HtmlMap getPartialHtmlMap() {
        return this.htmlMap;
    }

}
