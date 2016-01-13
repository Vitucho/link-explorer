/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.unit;

import org.vituchon.linkexplorer.domain.model.procedure.DiscreteProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.GenericQueryableProcedure;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Performs requets to specifics URL hidding comunication details.
 */
public class URLFetcher implements QueryableDiscreteProcedure, GenericQueryableProcedure<String, String> {

    private static final int BYTES_PER_READ = 1024;
    private static final Logger LOGGER = Logger.getLogger(URL.class.getName());
    private DiscreteProcedureStatusImpl status = new DiscreteProcedureStatusImpl();

    public synchronized String retrieveContent(String spec) throws RetrieveException {
        StringBuilder sb = new StringBuilder(256);
        URL url = null;
        int contentLength = 0;
        try {
            try {
                url = new URL(spec);
                contentLength = getContentLength(url);
            } catch (MalformedURLException e) {
                throw new RetrieveException(e);
            }
            if (contentLength < 0) {
                LOGGER.log(Level.WARNING, "Content length for {0} is {1}, assuming cero.", new Object[]{url, contentLength});
                contentLength = 0;
            }
            status.start(contentLength);
            char[] buffer = new char[BYTES_PER_READ];
            try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));) {
                int read;
                read = br.read(buffer, 0, BYTES_PER_READ);
                while (read != -1) {
                    sb.append(buffer);
                    status.advance(read);
                    read = br.read(buffer, 0, BYTES_PER_READ);
                }
            } catch (IOException e) {
                throw new RetrieveException(e);
            }
        } finally {
            status.end();
        }
        return sb.toString();
    }

    private int getContentLength(URL url) throws RetrieveException {
        try {
            URLConnection conn = url.openConnection();
            return conn.getContentLength();
        } catch (IOException e) {
            throw new RetrieveException(e);
        }
    //        HttpURLConnection conn = null;
    //        try {
    //            conn = (HttpURLConnection) url.openConnection();
    //            conn.setRequestMethod("HEAD");
    //            conn.getInputStream();
    //            return conn.getContentLength();
    //        } catch (IOException e) {
    //            throw new RetrieveException(e);
    //        } finally {
    //            if (conn != null) {
    //                conn.disconnect();
    //        }
    //        }

    }

    @Override
    public DiscreteProcedureStatus getProcedureStatus() {
        return status;
    }

    @Override
    public String perform(String spec) throws RetrieveException{
        return this.retrieveContent(spec);
    }

    public static class RetrieveException extends Exception {

        public RetrieveException(Throwable cause) {
            super(cause);
        }
    }
}
