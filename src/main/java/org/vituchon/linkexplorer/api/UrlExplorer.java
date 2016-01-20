/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vituchon.linkexplorer.domain.model.procedure.ProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts.HtmlMap;
import org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts.MaxDeepDirective;
import org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts.MultiPageLinkInspector;
import org.vituchon.linkexplorer.domain.model.procedure.execution.GenericProcedureExecutor;
import org.vituchon.linkexplorer.domain.model.procedure.execution.GenericProcedureListener;

/**
 *
 * @author Administrador
 */
public class UrlExplorer {

    private static final Logger logger = Logger.getLogger(UrlExplorer.class.getName());
    private final int maxDeep;
    private final String root;
    private final int workersDesired;

    private final GenericProcedureExecutor<String, HtmlMap> executor;

    private UrlExplorer(int maxDeep, String root, int workersDesired) {
        this.maxDeep = maxDeep;
        this.root = root;
        this.workersDesired = workersDesired;
        MultiPageLinkInspector inspector = new MultiPageLinkInspector(MaxDeepDirective.newInstance(maxDeep));
        this.executor = new GenericProcedureExecutor<>(inspector, root, new MultiPageListener(logger));
    }

    public void begin() {
        try {
            executor.execute();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Problem while starting the explorer", ex);
        }
    }

    public ProcedureStatus getLastStatus () {
        return this.executor.getLastStatus();
    }

    public int getMaxDeep() {
        return maxDeep;
    }

    public String getRoot() {
        return root;
    }

    public int getWorkersDesired() {
        return workersDesired;
    }

    public HtmlMap getOutput() throws InterruptedException, ExecutionException {
        return this.executor.getOutput();
    }

    public static class Builder {

        private int maxDeep = 2;
        private String root = null;
        private int workersDesired = 2;

        public Builder() {
        }

        public Builder setMaxDeep(int maxDeep) {
            this.maxDeep = maxDeep;
            return this;
        }

        public Builder setRoot(String root) {
            try {
                validateUrl(root);
                this.root = root;
                return this;
            } catch (MalformedURLException ex) {
                throw new IllegalArgumentException("A valid url is required",ex);
            }
        }

        public Builder setWorkersDesired(int workersDesired) {
            this.workersDesired = workersDesired;
            return this;
        }

        public UrlExplorer build() {
            if (root == null) {
                throw new IllegalArgumentException("The root url is mandatory");
            }
            return new UrlExplorer(maxDeep, root, workersDesired);
        }
    }

    private static void validateUrl(String urlAsString) throws MalformedURLException {
        try {
            new URL(urlAsString);
        } catch (MalformedURLException ex) {
            throw ex;
        }
    }
    

    private static class MultiPageListener implements GenericProcedureListener<HtmlMap> {

        private final Logger logger;

        public MultiPageListener(final Logger logger) {
            this.logger = logger;
        }
        
        
        @Override
        public void recieve(HtmlMap map) {
            logger.log(Level.INFO,"Process finish without error");
        }

        @Override
        public void report(Exception e) {
            logger.log(Level.SEVERE,"Process finish with error",e);
        }
    }
}
