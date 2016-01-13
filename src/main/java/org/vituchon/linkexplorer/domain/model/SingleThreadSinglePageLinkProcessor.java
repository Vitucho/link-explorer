/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model;

import org.vituchon.linkexplorer.domain.model.procedure.unit.LinkResolver;
import org.vituchon.linkexplorer.domain.model.procedure.unit.LinkResolver.ResolverException;
import org.vituchon.linkexplorer.domain.model.procedure.unit.LinkScanner;
import org.vituchon.linkexplorer.domain.model.procedure.unit.URLFetcher;
import org.vituchon.linkexplorer.domain.model.procedure.unit.URLFetcher.RetrieveException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Coordinates the entire activy of exploring the links departing from a base url.
 */
public class SingleThreadSinglePageLinkProcessor {

    private final ConcurrentHashMap<String, Object> exploredLinks;
    private final String baseURL;

    public SingleThreadSinglePageLinkProcessor(String baseURL) {
        this.exploredLinks = new ConcurrentHashMap<>();
        this.baseURL = baseURL;
    }
    
    public void voyage() throws ResolverException, RetrieveException {
        LinkScanner linkScanner = new LinkScanner();
        LinkResolver linkResolver = new LinkResolver();
        URLFetcher urlFetcher = new URLFetcher();

        String html = urlFetcher.retrieveContent(baseURL);
        Collection<String> links = linkScanner.scan(html);
        links = linkResolver.resolve(baseURL, links);
        for (String link : links) {
            System.out.println(link);
        }
    }
}
