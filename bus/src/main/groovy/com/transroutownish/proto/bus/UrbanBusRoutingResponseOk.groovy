/*
 * bus/src/main/groovy/com/transroutownish/proto/bus/
 * UrbanBusRoutingResponseOk.groovy
 * ============================================================================
 * Urban bus routing microservice prototype (Groovy port). Version 0.3.0
 * ============================================================================
 * A daemon written in Groovy, designed and intended to be run
 * as a microservice, implementing a simple urban bus routing prototype.
 * ============================================================================
 * Copyright (C) 2023 Radislav (Radicchio) Golubtsov
 *
 * (See the LICENSE file at the top of the source tree.)
 */

package com.transroutownish.proto.bus

/**
 * The POJO representation of successful message, returning in the response.
 *
 * @version 0.3.0
 * @since   0.1.5
 */
class UrbanBusRoutingResponseOk {
    /** The starting bus stop point. */
    final int from

    /** The ending bus stop point. */
    final int to

    /**
     * The indicator of the presence of a direct route
     * from <code>from</code> to <code>to</code>.
     */
    final boolean direct

    /**
     * The accessor method for the starting bus stop point.
     *
     * @return The starting bus stop point.
     */
    int getFrom() {
        return from
    }

    /**
     * The accessor method for the ending bus stop point.
     *
     * @return The ending bus stop point.
     */
    int getTo() {
        return to
    }

    /**
     * The accessor method for the indicator of the presence of a direct route.
     *
     * @return The indicator of the presence of a direct route.
     */
    boolean isDirect() {
        return direct
    }

    /**
     * The effective constructor for successful message.
     *
     * @param _from   The starting bus stop point.
     * @param _to     The ending   bus stop point.
     * @param _direct The indicator of the presence of a direct route.
     */
    UrbanBusRoutingResponseOk(final int     _from,
                              final int     _to,
                              final boolean _direct) {

        from   = _from
        to     = _to
        direct = _direct
    }
}

// vim:set nu et ts=4 sw=4:
