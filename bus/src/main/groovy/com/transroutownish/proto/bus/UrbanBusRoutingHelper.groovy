/*
 * bus/src/main/groovy/bus/UrbanBusRoutingHelper.groovy
 * ============================================================================
 * Urban bus routing microservice prototype (Groovy port). Version 0.0.8
 * ============================================================================
 * A daemon written in Groovy, designed and intended to be run
 * as a microservice, implementing a simple urban bus routing prototype.
 * ============================================================================
 * Copyright (C) 2023 Radislav (Radicchio) Golubtsov
 *
 * (See the LICENSE file at the top of the source tree.)
 */

package com.transroutownish.proto.bus

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.invoke.MethodHandles

/**
 * The helper class for the daemon.
 *
 * @version 0.0.8
 * @since   0.0.1
 */
class UrbanBusRoutingHelper {
    // Helper constants.
    static final String EMPTY_STRING =   ""
    static final String BRACES       = "{}"

    // Extra helper constants.
    static final String YES = "yes"

    // Common error messages.
    static final String ERR_APP_PROPS_UNABLE_TO_GET
        = "Unable to get application properties."

    /** The application properties filename. */
    static final String APP_PROPS = "application.properties"

    // Application properties keys for the logger.
    static final String DEBUG_LOG_ENABLED = "logger.debug.enabled"

    // Application properties keys for the routes data store.
    static final String PATH_PREFIX = "routes.datastore.path.prefix"
    static final String PATH_DIR    = "routes.datastore.path.dir"
    static final String FILENAME    = "routes.datastore.filename"

    /** The SLF4J logger. */
    static final Logger l = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass())

    /**
     * Retrieves the path and filename of the routes data store
     * from application properties.
     *
     * @return The path and filename of the routes data store
     *         or <code>null</code>, if they are not defined.
     */
    static String get_routes_datastore() {
        def datastore = EMPTY_STRING

        def path_prefix = UrbanBusRoutingApp.props.getProperty(PATH_PREFIX)
        def path_dir    = UrbanBusRoutingApp.props.getProperty(PATH_DIR   )
        def filename    = UrbanBusRoutingApp.props.getProperty(FILENAME   )

        if (path_prefix !== null) { datastore += path_prefix }
        if (path_dir    !== null) { datastore += path_dir    }
        if (filename    !== null) { datastore += filename    }

        if (datastore.isEmpty()) { return null }

        return datastore
    }

    /**
     * Identifies whether debug logging is enabled by retrieving
     * the corresponding setting from application properties.
     *
     * @return <code>true</code> if debug logging is enabled,
     *         <code>false</code> otherwise.
     */
    static boolean is_debug_log_enabled() {
        def debug_log_enabled
            = UrbanBusRoutingApp.props.getProperty(DEBUG_LOG_ENABLED)

        if ((debug_log_enabled !== null)
            && (debug_log_enabled == YES)) { return true }
    }

    // Helper method. Used to get the application properties object.
    static final Properties _get_props() {
        def props = new Properties()

        def loader = UrbanBusRoutingHelper.class.getClassLoader()

        def data = loader.getResourceAsStream(APP_PROPS)

        try {
            props.load(data)
            data.close()
        } catch (IOException e) {
            l.error ERR_APP_PROPS_UNABLE_TO_GET
        }

        return props
    }
}

// vim:set nu et ts=4 sw=4:
