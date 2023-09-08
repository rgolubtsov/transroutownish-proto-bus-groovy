/*
 * bus/src/main/groovy/bus/UrbanBusRoutingApp.groovy
 * ============================================================================
 * Urban bus routing microservice prototype (Groovy port). Version 0.0.1
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
 * The startup class of the daemon.
 *
 * @version 0.0.1
 * @since   0.0.1
 */
class UrbanBusRoutingApp {
    String getTransroutownish() {
        return '=== Trans-RoutE-Townish (Groovy port)'
    }

    /**
     * The microservice entry point.
     *
     * @param args The array of command-line arguments.
     */
    static void main(final String[] args) {
        println new UrbanBusRoutingApp().transroutownish
    }
}

// vim:set nu et ts=4 sw=4:
