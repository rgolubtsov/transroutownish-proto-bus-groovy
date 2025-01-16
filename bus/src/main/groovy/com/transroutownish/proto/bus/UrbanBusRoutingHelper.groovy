/*
 * bus/src/main/groovy/com/transroutownish/proto/bus/
 * UrbanBusRoutingHelper.groovy
 * ============================================================================
 * Urban bus routing microservice prototype (Groovy port). Version 0.3.1
 * ============================================================================
 * A daemon written in Groovy, designed and intended to be run
 * as a microservice, implementing a simple urban bus routing prototype.
 * ============================================================================
 * Copyright (C) 2023-2025 Radislav (Radicchio) Golubtsov
 *
 * (See the LICENSE file at the top of the source tree.)
 */

package com.transroutownish.proto.bus

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.invoke.MethodHandles

import ratpack.util.RatpackVersion

/**
 * The helper class for the daemon.
 *
 * @version 0.3.1
 * @since   0.0.1
 */
class UrbanBusRoutingHelper {
    // Helper constants.
    static final int    EXIT_FAILURE =   1 //    Failing exit status.
    static final int    EXIT_SUCCESS =   0 // Successful exit status.
    static final String EMPTY_STRING =  ""
    static final String SPACE        = " "
    static final String SLASH        = "/"
    static final String EQUALS       = "="
    static final String V_BAR        = "|"

    // Extra helper constants.
    static final String YES  = "yes"
    static final String FROM = "from"
    static final String TO   = "to"

    // Common error messages.
    static final String ERR_PORT_VALID_MUST_BE_POSITIVE_INT
        = '''Valid server port must be a positive integer value,\
 in the range 1024 .. 49151. The default value of 8080\
 will be used instead.'''
    static final String ERR_APP_PROPS_UNABLE_TO_GET
        = "Unable to get application properties."
    static final String ERR_DATASTORE_NOT_FOUND
        = "FATAL: Data store file not found. Quitting..."
    static final String ERR_CANNOT_START_SERVER
        = "FATAL: Cannot start server "
    static final String ERR_ADDR_ALREADY_IN_USE
        = "due to address requested already in use. Quitting..."
    static final String ERR_SERV_UNKNOWN_REASON
        = "for an unknown reason. Quitting..."
    static final String ERR_REQ_PARAMS_MUST_BE_POSITIVE_INTS
        = '''Request parameters must take positive integer values,\
 in the range 1 .. 2,147,483,647. Please check your inputs.'''
    static final String ERR_NOT_FOUND
        = "404 Not Found."
    static final int    ERR_EADDRINUSE_NEGATIVE = -98

    // Common notification messages.
    static final String MSG_SERVER_STARTED = "Server started on port "
    static final String MSG_SERVER_STOPPED = "Server stopped"

    /** The application properties filename. */
    static final String APP_PROPS = "application.properties"

    /** The minimum port number allowed. */
    static final int MIN_PORT = 1024

    /** The maximum port number allowed. */
    static final int MAX_PORT = 49151

    /** The default server port number. */
    static final int DEF_PORT = 8080

    // Application properties keys for the server port number.
    static final String SERVER_PORT = "server.port"

    // Application properties keys for the logger.
    static final String DEBUG_LOG_ENABLED = "logger.debug.enabled"

    // Application properties keys for the routes data store.
    static final String PATH_PREFIX = "routes.datastore.path.prefix"
    static final String PATH_DIR    = "routes.datastore.path.dir"
    static final String FILENAME    = "routes.datastore.filename"

    // REST URI path-related constants.
    static final String REST_PREFIX = "route"
    static final String REST_DIRECT = "direct"

    // HTTP response status codes.
    static final int HTTP_400 = 400
    static final int HTTP_404 = 404
    static final int HTTP_405 = 405

    // HTTP response-related constants.
    static final String MIME_TYPE    = "application/json"
    static final String HDR_ALLOW_N  = "allow"
    static final String HDR_ALLOW_V  = "GET, HEAD"
    static final String HDR_SERVER_N = "server"
    static final String HDR_SERVER_V = "Ratpack/" + RatpackVersion.getVersion()

    /**
     * The regex pattern for the leading part of a bus stops sequence,
     * before the matching element.
     */
    static final String SEQ1_REGEX = ".*\\s"

    /**
     * The regex pattern for the trailing part of a bus stops sequence,
     * after the matching element.
     */
    static final String SEQ2_REGEX = "\\s.*"

    /** The SLF4J logger. */
    static final Logger l = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass())

    /**
     * Retrieves the port number used to run the server,
     * from application properties.
     *
     * @return The port number on which the server has to be run.
     */
    static int get_server_port() {
        def server_port_ = UrbanBusRoutingApp.props.getProperty(SERVER_PORT)
        def server_port  = null

        try { server_port = Integer.parseInt(server_port_) }
        catch (NumberFormatException e) { /* Using the last `else' block. */ }

        if (server_port !== null) {
            if ((server_port >= MIN_PORT) && (server_port <= MAX_PORT)) {
                return server_port
            } else {
                l.error(ERR_PORT_VALID_MUST_BE_POSITIVE_INT); return DEF_PORT
            }
        } else {
            l.error(ERR_PORT_VALID_MUST_BE_POSITIVE_INT); return DEF_PORT
        }
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
            l.error(ERR_APP_PROPS_UNABLE_TO_GET)
        }

        return props
    }
}

// vim:set nu et ts=4 sw=4:
