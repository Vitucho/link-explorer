/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.composite;

import org.vituchon.linkexplorer.domain.model.procedure.DiscreteProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.GenericQueryableProcedure;
import org.vituchon.linkexplorer.domain.model.procedure.ProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.composite.SinglePageLinkInspectorStatus.Phase;
import org.vituchon.linkexplorer.domain.model.procedure.unit.LinkResolver;
import org.vituchon.linkexplorer.domain.model.procedure.unit.LinkScanner;
import org.vituchon.linkexplorer.domain.model.procedure.unit.QueryableDiscreteProcedure;
import org.vituchon.linkexplorer.domain.model.procedure.unit.URLFetcher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Coordinates the entire activy of exploring the links departing from a base url.
 */
public class SinglePageLinkInspector implements GenericQueryableProcedure<String, Collection<String>> {

    private final URLFetcher urlFetcher;
    private final LinkResolver linkResolver;
    private final LinkScanner linkScanner;
    private final SinglePageLinkInspectorStatus status;

    public static SinglePageLinkInspector newInstance() {
        return new SinglePageLinkInspector();
    }

    private SinglePageLinkInspector() {
        this.urlFetcher = new URLFetcher();
        this.linkResolver = new LinkResolver();
        this.linkScanner = new LinkScanner();
        this.status = new SinglePageLinkInspectorStatus();
    }

    public Collection<String> inspect(String url) throws InspectionException {
        Throwable t = null;
        Collection<String> links = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            status.start();
            Future<String> urlFetchCall = executorService.submit(new UrlFetchCall(urlFetcher, url));
            executorService.submit(new StatusSneakCall(urlFetcher, 50));
            String html = urlFetchCall.get();
            
            status.setPhase(Phase.SCAN);
            Future<Collection<String>> linkScannerCall = executorService.submit(new LinkScannerCall(linkScanner, html));
            executorService.submit(new StatusSneakCall(linkScanner, 5));
            links = linkScannerCall.get();

            status.setPhase(Phase.RESOLVE);
            Future<Set<String>> linkResolverCall = executorService.submit(new LinkResolverCall(linkResolver, url, links));
            executorService.submit(new StatusSneakCall(linkResolver, 5));
            links = linkResolverCall.get();

        } catch (InterruptedException | ExecutionException e) {
            t = e;
            throw new InspectionException("Exception in phase : " + status.getCurrentPhase(), e);
        } finally {
            executorService.shutdown();
            status.end(t);
        }
        return links;
    }

    public SinglePageLinkInspectorStatus getStatus() {
        return status;
    }

    @Override
    public Collection<String> perform(String url) throws InspectionException {
        return this.inspect(url);
    }

    @Override
    public ProcedureStatus getProcedureStatus() {
        return status;


    }

    private static class UrlFetchCall implements Callable<String> {

        private final URLFetcher urlFetcher;
        private final String url;

        public UrlFetchCall(URLFetcher urlFetcher, String url) {
            this.urlFetcher = urlFetcher;
            this.url = url;
        }

        @Override
        public String call() throws Exception {
            return urlFetcher.retrieveContent(url);
        }
    }

    private static class LinkScannerCall implements Callable<Collection<String>> {

        private final LinkScanner linkScanner;
        private final String html;

        public LinkScannerCall(LinkScanner linkScanner, String html) {
            this.linkScanner = linkScanner;
            this.html = html;
        }

        @Override
        public Collection<String> call() throws Exception {
            return linkScanner.scan(html);
        }
    }

    private static class LinkResolverCall implements Callable<Set<String>> {

        private final LinkResolver linkResolver;
        private final Collection<String> links;
        private final String baseUrl;

        public LinkResolverCall(LinkResolver linkResolver, String url, Collection<String> links) {
            this.linkResolver = linkResolver;
            this.baseUrl = url;
            this.links = links;
        }

        @Override
        public Set<String> call() throws Exception {
            return linkResolver.resolve(baseUrl, links);
        }
    }

    private class StatusSneakCall<T extends QueryableDiscreteProcedure> implements Callable<Void> {

        private final int pollingPeriod;
        private final T discreteProcedure;
        private volatile DiscreteProcedureStatus lastStatus;

        public StatusSneakCall(T discreteProcedure, int pollingPeriod) {
            this.pollingPeriod = pollingPeriod;
            this.discreteProcedure = discreteProcedure;
        }

        @Override
        public Void call() throws Exception {
            lastStatus = discreteProcedure.getProcedureStatus();
            while (!lastStatus.isDone() && !Thread.interrupted()) {
                Thread.sleep(pollingPeriod);
                lastStatus = discreteProcedure.getProcedureStatus();
                SinglePageLinkInspector.this.status.setDiscreteProcedureStatus(lastStatus);
            }
            SinglePageLinkInspector.this.status.setDiscreteProcedureStatus(lastStatus);
            return null;
        }

        public DiscreteProcedureStatus getLastStatus() {
            return lastStatus;
        }
    }

    public static class InspectionException extends Exception {

        public InspectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
