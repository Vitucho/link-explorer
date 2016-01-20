/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.api;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts.HtmlMap;
import org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts.InspectorWorker;
import org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts.MultiPageLinkInspectorStatus;
import org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts.SinglePageExplorationStatus;

/**
 *
 * @author victor
 */
public class JsonMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(Api.class.getName());

    public static String toJson(HtmlMap htmlMap) {
        try {
            Map<String, List<String>> pages = htmlMap.toSiteMap();
            Site site = new Site();
            for (Map.Entry<String, List<String>> entry : pages.entrySet()) {
                Page page = new Page();
                page.url = entry.getKey();
                page.links = entry.getValue();
                site.pages.add(page);
            }
            return MAPPER.writeValueAsString(site);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Upa error al Jsonificar!", ex);
            throw new IllegalStateException("Error al jsonificar", ex);
        }
    }

    static class Site {

        private List<Page> pages = new LinkedList<>();

        public List<Page> getPages() {
            return pages;
        }

    }

    static class Page {

        private String url;
        private List<String> links = new LinkedList<>();

        public String getUrl() {
            return url;
        }

        public List<String> getLinks() {
            return links;
        }

    }

    public static String toJson(MultiPageLinkInspectorStatus status) {
        try {
            List<UrlStatus> urlStatuses = new LinkedList<>();
            ExplorerStatus explorerStatus = new ExplorerStatus(status.getCurrentPhase().toString(), urlStatuses);
            Set<Map.Entry<String, SinglePageExplorationStatus>> inspectionSet = status.getInspections().entrySet();

            for (Map.Entry<String, SinglePageExplorationStatus> inspection : inspectionSet) {
                String url = inspection.getKey();
                SinglePageExplorationStatus singlePageStatus = inspection.getValue();
                InspectorWorker worker = singlePageStatus.getWorker();
                String workerInfo = worker.getName() + " paginas inspeccionadas " + worker.getInspectedCount();
                UrlStatus urlStatus = new UrlStatus(workerInfo, url, singlePageStatus.getStatus().toString());
                urlStatuses.add(urlStatus);
            }
            return MAPPER.writeValueAsString(explorerStatus);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Upa error al Jsonificar!", ex);
            throw new IllegalStateException("Error al jsonificar", ex);
        }
    }

    public static class ExplorerStatus {

        private final String phase;
        private final List<UrlStatus> urlsTaken;

        public ExplorerStatus(String phase, List<UrlStatus> urlsTaken) {
            this.phase = phase;
            this.urlsTaken = urlsTaken;
        }

        public String getPhase() {
            return phase;
        }

        public List<UrlStatus> getUrlsTaken() {
            return urlsTaken;
        }

    }

    public static class UrlStatus {

        private final String workerThreadInfo;
        private final String url;
        private final String status;

        public UrlStatus(String workerThreadInfo, String url, String status) {
            this.workerThreadInfo = workerThreadInfo;
            this.url = url;
            this.status = status;
        }

        public String getWorkerThreadInfo() {
            return workerThreadInfo;
        }

        public String getUrl() {
            return url;
        }

        public String getStatus() {
            return status;
        }

    }
}
