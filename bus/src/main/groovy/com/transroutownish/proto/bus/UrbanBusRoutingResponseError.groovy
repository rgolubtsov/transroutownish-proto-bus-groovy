/*
 * bus/src/main/groovy/com/transroutownish/proto/bus/
 * UrbanBusRoutingResponseError.groovy
 * ============================================================================
 * Urban bus routing microservice prototype (Groovy port). Version 0.1.11
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
 * The POJO representation of the error message, returning in the response
 * when one of the <strong>4xx Client Error</strong> section's errors occurred
 * during processing the request.
 *
 * @version 0.1.11
 * @since   0.1.5
 */
class UrbanBusRoutingResponseError {
    /** The error message. */
    final String error

    /**
     * The accessor method for the error message.
     *
     * @return The error message.
     */
    String getError() {
        return error
    }

    /**
     * The effective constructor for the error message.
     *
     * @param _error The error message.
     */
    UrbanBusRoutingResponseError(final String _error) {
        error = _error
    }
}

// vim:set nu et ts=4 sw=4:
