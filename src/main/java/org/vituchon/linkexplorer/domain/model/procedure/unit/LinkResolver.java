/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.unit;

import org.vituchon.linkexplorer.domain.model.procedure.DiscreteProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.GenericProcedure;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validates common html links and resolve them to absolute url, given a base url.
 */
public class LinkResolver implements QueryableDiscreteProcedure, GenericProcedure<LinkResolver.LinkResolverInput, Set<String>> {

    private DiscreteProcedureStatusImpl status = new DiscreteProcedureStatusImpl();

    public synchronized Set<String> resolve(String url, Collection<String> links) throws ResolverException {
        Set<String> resolvedLinks = new HashSet<>();
        status.start(links.size());
        URL baseURL;
        try {
            baseURL = new URL(url);
        } catch (MalformedURLException ex) {
            throw new ResolverException("Cannot use the Url : " + url + " as base",ex);
        }
        for (String link : links) {
            if (valid(link)) {
                try {
                    URL absoluteUrl = new URL(baseURL, link);
                    resolvedLinks.add(absoluteUrl.toExternalForm());
                } catch (MalformedURLException ex) {
                    throw new ResolverException("Cannot make the link absolute. Url: " + url + " Link " + link,ex);
                }
            }
            status.advance(1);
        }
        status.end();
        return resolvedLinks;
    }

    private boolean valid(String s) {
        if (s.matches("javascript:.*|mailto:.*") ||  // if it is not real link...
                s.startsWith("#")) { // if it is an anchor...
            return false; // then is not admitted.
        }
        return true;
    }

    private String makeAbsolute(String baseUrl, String link) throws ResolverException {

        if (link.matches("^#$")) {
            return baseUrl;
        }
        if (link.matches("^#.*")) {
            return link + baseUrl;
        }

        if (link.matches("http:/*.*") || link.matches("https:/*.*")) {
            return link;
        }
        if (link.matches("^\\?.*")) {
            return baseUrl + link;
        }
        if (link.matches("/.*") && baseUrl.matches(".*$[^/]")) {
            return baseUrl + "/" + link;
        }
        if (link.matches("[^/].*") && baseUrl.matches(".*[^/]")) {
            return baseUrl + "/" + link;
        }
        if (link.matches("/.*") && baseUrl.matches(".*[/]")) {
            return baseUrl + link;
        }
        if (link.matches("/.*") && baseUrl.matches(".*[^/]")) {
            return baseUrl + link;
        } else {
            return baseUrl + "/" + link;
        }
        //throw new ResolverException("Cannot make the link absolute. Url: " + url + " Link " + link);
    }

    @Override
    public DiscreteProcedureStatus getProcedureStatus() {
        return status;
    }

    @Override
    public Set<String> perform(LinkResolverInput input) throws Exception {
        return this.resolve(input.url, input.links);
    }

    public static class ResolverException extends Exception {

        public ResolverException(String message) {
            super(message);
        }
        public ResolverException(String message,Throwable cause) {
            super(message,cause);
        }
    }

    public static class LinkResolverInput {

        private final String url;
        private final Collection<String> links;

        public static LinkResolverInput newInstance(String url, Collection<String> links) {
            return new LinkResolverInput(url, links);
        }

        private LinkResolverInput(String url, Collection<String> links) {
            this.url = url;
            this.links = links;
        }
    }
}
