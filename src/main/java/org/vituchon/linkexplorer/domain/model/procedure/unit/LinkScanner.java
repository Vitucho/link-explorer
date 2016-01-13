/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.unit;

import org.vituchon.linkexplorer.domain.model.procedure.DiscreteProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.GenericProcedure;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a web page in search of links.
 */
public class LinkScanner implements QueryableDiscreteProcedure, GenericProcedure<String, Collection<String>> {

    private static final Pattern HREF_WITHOUT_ANCHOR_PATTERN = Pattern.compile("<a\\b[^>]*href=\"([^#]*?)\".*?>(.*?)</a>"); // NOT WORKING!!! GRGRGRGR
    private static final Pattern HREF_PATTERN = Pattern.compile("<a\\b[^>]*href=\"(.*?)\".*?>(.*?)</a>");
    private DiscreteProcedureStatusImpl status  = new DiscreteProcedureStatusImpl();

    public synchronized Collection<String> scan(String html) {
        Set<String> links = new HashSet<>();
        try {
            status.start(html.length());
            Matcher hrefMatcher = HREF_PATTERN.matcher(html);
            int last = 0;
            while (hrefMatcher.find()) {
                String link = hrefMatcher.group(1);
                links.add(link);
                int charsConsumed = hrefMatcher.end() - last;
                status.advance(charsConsumed);
                last = hrefMatcher.end();
            }
        } finally {
            status.end();
        }
        return links;
    }
    
    @Override
    public DiscreteProcedureStatus getProcedureStatus() {
        return status;
    }

    @Override
    public Collection<String> perform(String html) {
        return this.scan(html);
    }

}
