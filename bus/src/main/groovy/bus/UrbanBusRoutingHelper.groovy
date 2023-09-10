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
    // Helper constants.
    static final String EMPTY_STRING = ""

    // Common error messages.
    static final String ERR_APP_PROPS_UNABLE_TO_GET
        = "Unable to get application properties."

    /** The application properties filename. */
    static final String APP_PROPS = "application.properties"

    // Application properties keys for the routes data store.
    static final String PATH_PREFIX = "routes.datastore.path.prefix"
    static final String PATH_DIR    = "routes.datastore.path.dir"
    static final String FILENAME    = "routes.datastore.filename"

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
