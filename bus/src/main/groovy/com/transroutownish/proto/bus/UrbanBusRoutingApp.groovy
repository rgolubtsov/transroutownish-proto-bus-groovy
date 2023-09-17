/*
 * bus/src/main/groovy/bus/UrbanBusRoutingApp.groovy
 * ============================================================================
 * Urban bus routing microservice prototype (Groovy port). Version 0.0.9
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

import org.graylog2.syslog4j.impl.unix.UnixSyslog
import org.graylog2.syslog4j.impl.unix.UnixSyslogConfig
import org.graylog2.syslog4j.SyslogIF

import java.lang.invoke.MethodHandles

import static com.transroutownish.proto.bus.UrbanBusRoutingHelper.*

/**
 * The startup class of the daemon.
 *
 * @version 0.0.9
 * @since   0.0.1
 */
class UrbanBusRoutingApp {
    /** The path and filename of the sample routes data store. */
    static final String SAMPLE_ROUTES = "./data/routes.txt"

    /**
     * The regex pattern for the element to be excluded
     * from a bus stops sequence: it is an arbitrary identifier
     * of a route, which is not used in the routes processing anyhow.
     */
    static final String ROUTE_ID_REGEX = /^\d+/

    /** The SLF4J logger. */
    static final Logger l = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass())

    /** The Unix system logger. */
    static UnixSyslog s

    /** The application properties object. */
    static Properties props

    /** The list, containing all available routes. */
    static List routes_list

    /** The debug logging enabler. */
    static boolean debug_log_enabled

    /**
     * The microservice entry point.
     *
     * @param args The array of command-line arguments.
     */
    static void main(final String[] args) {
        def routes = null

        // Getting the application properties object.
        props = _get_props()

        // Getting the path and filename of the routes data store
        // from application properties.
        def datastore = get_routes_datastore()

        if (datastore === null) { datastore = SAMPLE_ROUTES }

        def data = new File(datastore)

        try {
            routes = new Scanner(data)
        } catch (FileNotFoundException e) {
            l.error ERR_DATASTORE_NOT_FOUND
        }

        routes_list = []

        while (routes.hasNextLine()) {
            routes_list << routes.nextLine()
                .replaceFirst(ROUTE_ID_REGEX, EMPTY_STRING) + SPACE
        }

        routes.close()

        // Identifying whether debug logging is enabled.
        debug_log_enabled = is_debug_log_enabled()

        // Opening the system logger.
        // Calling <syslog.h> openlog(NULL, LOG_CONS | LOG_PID, LOG_DAEMON);
        def cfg = new UnixSyslogConfig()
        cfg.setIdent(null); cfg.setFacility(SyslogIF.FACILITY_DAEMON)
        s = new UnixSyslog(); s.initialize (SyslogIF.UNIX_SYSLOG,cfg)

        l.debug "$routes_list"
        s.debug "$routes_list"

        // Closing the system logger.
        // Calling <syslog.h> closelog();
        s.shutdown()
    }
}

// vim:set nu et ts=4 sw=4:
