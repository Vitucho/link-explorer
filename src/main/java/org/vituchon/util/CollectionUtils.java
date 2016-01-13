/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Administrador
 */
public class CollectionUtils {

    public static interface Predicate<T> {

        boolean apply(T element);
    }

    public static interface Reductor<T> {

        public T reduce(T a,T b);
    }

    public static <T> T reduce(Collection<T> col, Reductor<T> reductor) {
        T result = null;
        Iterator<T> iterator = col.iterator();
        if (iterator.hasNext()) {
            result = iterator.next();
            while (iterator.hasNext()) {
                result = reductor.reduce(result, iterator.next());
            }
        }
        return result;
    }
    
    public static <T> Collection<T> filter(Collection<T> col, Predicate<T> predicate) {
        Collection<T> result = new ArrayList<>();
        for (T element : col) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }


}
