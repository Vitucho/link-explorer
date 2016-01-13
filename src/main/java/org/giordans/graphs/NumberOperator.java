/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.giordans.graphs;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 *
 * @author Administrador
 */
enum NumberOperator implements Comparator<Number> {
    INSTANCE;
    
    @Override
    public int compare(Number a, Number b) {
        return new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString()));
    }
    
    Number add (Number a,Number b) {
        return new BigDecimal(a.toString()).add(new BigDecimal(b.toString())); 
    }
 
    Number min (Number a,Number b) {
        BigDecimal _a = new BigDecimal(a.toString());
        BigDecimal _b = new BigDecimal(b.toString());
        int comparision = _a.compareTo(_b);
        return comparision <= 0 ? _a : _b;
    }

    Number subtract(Number a, Number b) {
        return new BigDecimal(a.toString()).subtract(new BigDecimal(b.toString()));
    }
}
