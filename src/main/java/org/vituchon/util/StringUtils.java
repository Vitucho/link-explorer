/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.util;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Administrador
 */
public class StringUtils {
 
    public static <N> String join (Collection<N> elements, String joiner) {
        StringBuilder sb = new StringBuilder();
        Iterator<N> iterator = elements.iterator();
        if (iterator.hasNext()) {
            sb.append(iterator.next());
            while (iterator.hasNext()) {
                sb.append(joiner);
                sb.append(iterator.next());
            }
        }
        return sb.toString();
    }
}
