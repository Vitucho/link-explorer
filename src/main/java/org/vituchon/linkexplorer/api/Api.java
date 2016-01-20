/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.api;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vituchon.linkexplorer.logging.AppLogging;
import java.io.IOException;
import org.vituchon.linkexplorer.domain.model.procedure.ProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts.HtmlMap;

/**
 * Defines the API to work with the services provided.
 */
public class Api {

    private static final Logger LOGGER = Logger.getLogger(Api.class.getName());

    static {
        AppLogging.init();
    }

    public static void main(String args[]) throws IOException, InterruptedException, ExecutionException {

        UrlExplorer urlExplorer = new UrlExplorer.Builder().setRoot("http://www.chau.hol.es").build();
        urlExplorer.begin();
        ProcedureStatus lastStatus = urlExplorer.getLastStatus();
        while (!lastStatus.isDone()) {
            LOGGER.log(Level.INFO, "Last status is {0}", lastStatus.toString());
            Thread.sleep(500);
            lastStatus = urlExplorer.getLastStatus();
        }
        LOGGER.log(Level.INFO, "Last status is {0}", lastStatus.toString());
        HtmlMap htmlResourceMap = urlExplorer.getOutput();
        String json = JsonMapper.toJson(htmlResourceMap);
        System.out.println(json);

    }

}
