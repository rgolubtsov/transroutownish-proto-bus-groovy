/*
 * bus/src/main/groovy/bus/UrbanBusRoutingHelper.groovy
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
 * The helper class for the daemon.
 *
 * @version 0.0.1
 * @since   0.0.1
 */
class UrbanBusRoutingHelper {
    // Common error messages.
    static final String ERR_APP_PROPS_UNABLE_TO_GET
        = "Unable to get application properties."

    /** The application properties filename. */
    static final String APP_PROPS = "application.properties"

    // Helper method. Used to get the application properties object.
    static final Properties _get_props() {
        def props = new Properties()

        def loader = UrbanBusRoutingHelper.class.getClassLoader()

        def data = loader.getResourceAsStream(APP_PROPS)

        try {
            props.load(data)
            data.close()
        } catch (IOException e) {
            println ERR_APP_PROPS_UNABLE_TO_GET
        }

        return props
    }
}

// vim:set nu et ts=4 sw=4:
