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
    /** The SLF4J logger. */
    static final Logger l = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass())

    /** The Unix system logger. */
    static UnixSyslog s

    /** The application properties object. */
    static Properties props

    /** The debug logging enabler. */
    static boolean debug_log_enabled

    /**
     * The microservice entry point.
     *
     * @param args The array of command-line arguments.
     */
    static void main(final String[] args) {
        // Getting the application properties object.
        props = _get_props()

        // Getting the path and filename of the routes data store
        // from application properties.
        def datastore = get_routes_datastore()

        // Identifying whether debug logging is enabled.
        debug_log_enabled = is_debug_log_enabled()

        // Opening the system logger.
        // Calling <syslog.h> openlog(NULL, LOG_CONS | LOG_PID, LOG_DAEMON);
        def cfg = new UnixSyslogConfig()
        cfg.setIdent(null); cfg.setFacility(SyslogIF.FACILITY_DAEMON)
        s = new UnixSyslog(); s.initialize (SyslogIF.UNIX_SYSLOG,cfg)

        l.debug datastore
        s.debug datastore

        l.debug "$debug_log_enabled"
        s.debug "$debug_log_enabled"

        // Closing the system logger.
        // Calling <syslog.h> closelog();
        s.shutdown()
    }
}

// vim:set nu et ts=4 sw=4: