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
import org.codehaus.jackson.map.ObjectMapper;
import org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts.HtmlMap;

/**
 *
 * @author victor
 */
public class JsonMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

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
            return "UPA ERROR!";
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
}
