package org.vituchon.linkexplorer.domain.model.procedure.unit;

import org.vituchon.linkexplorer.domain.model.procedure.AbstractProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.DiscreteProcedureStatus;

/**
 * Encapsulates basic measurement data about a linear measurable or "stepeable" procedure.
 * Stepable cames from "steps", an stepeable procedure is a procedure composed by individual steps.
 * The measurement values are stored in volatile variables for a correct multithreading access and also it is properly synchronized.
 */
class DiscreteProcedureStatusImpl extends AbstractProcedureStatus implements DiscreteProcedureStatus {

    /**
     * Current step of the measured procedure.
     */
    private volatile int current;
    /**
     * Total steps of the procedure.
     */
    private volatile int total;

    /**
     * Signals the start of this process.
     *
     * @param total total steps of this process.
     */
    synchronized void start(int total) {
        this.current = 0;
        this.total = total;
        this.start = System.currentTimeMillis();
    }

    /**
     * Signals advance on this process.
     *
     * @param steps number of steps advanced.
     */
    synchronized void advance(int steps) {
        this.current += steps;
    }

    /**
     * Signals the end of this process.
     */
    synchronized void end() {
        this.current = this.total;
        this.end = System.currentTimeMillis();
    }

    @Override
    public int getCurrent() {
        return current;
    }

    @Override
    public int getTotal() {
        return total;
    }

    @Override
    public int getPercentaje() {
        return (this.total == 0) ? 0 : (this.current * 100) / this.total;
    }

    @Override
    public String toString() {
        return String.format("%d of %d, %d%%", current, total, getPercentaje());
    }

}
