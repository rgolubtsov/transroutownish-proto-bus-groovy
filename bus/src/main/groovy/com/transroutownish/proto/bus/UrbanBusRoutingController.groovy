/*
 * bus/src/main/groovy/com/transroutownish/proto/bus/
 * UrbanBusRoutingController.groovy
 * ============================================================================
 * Urban bus routing microservice prototype (Groovy port). Version 0.1.0
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

import org.graylog2.syslog4j.impl.unix.UnixSyslog

import ratpack.service.Service
import ratpack.service.StartEvent
import ratpack.service.StopEvent

import ratpack.server.RatpackServer

import static com.transroutownish.proto.bus.UrbanBusRoutingHelper.*

/**
 * The controller class of the daemon.
 *
 * @version 0.1.0
 * @since   0.0.9
 */
class UrbanBusRoutingController {
    // Helper constants.
    static final String REST_PREFIX = "route"
    static final String REST_DIRECT = "direct"

    /** The SLF4J logger. */
    static final Logger l = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass())

    /**
     * The service class for the daemon.
     * <br />
     * <br />It is used solely to log events of starting/stopping
     * the bundled web server.
     */
    class UrbanBusRoutingService implements Service {
        /**
         * Gets called when the server is about to be started.
         *
         * @param event The metadata object containing the startup event info.
         */
        void onStart(final StartEvent event) {
            def registry = event.getRegistry()

            def s = registry.get(UnixSyslog)
            def server_port = registry.get(Integer)

            l.info "$MSG_SERVER_STARTED$server_port"
            s.info "$MSG_SERVER_STARTED$server_port"
        }

        /**
         * Gets called just before the server is about to be stopped.
         *
         * @param event The metadata object containing the stop event info.
         */
        void onStop(final StopEvent event) {
            def registry = event.getRegistry()

            def s = registry.get(UnixSyslog)

            l.info MSG_SERVER_STOPPED
            s.info MSG_SERVER_STOPPED

            // Closing the system logger.
            // Calling <syslog.h> closelog();
            s.shutdown()
        }
    }

    /**
     * Starts up the bundled web server.
     *
     * @param args The list containing the server port number to listen on,
     *             as the first element.
     */
    void startup(final List args) {
        def(server_port,
            debug_log_enabled,
            routes_list,
            syslog
        ) = args

//      l.debug "$debug_log_enabled"
//      s.debug "$debug_log_enabled"

//      l.debug "$routes_list"
//      s.debug "$routes_list"

        // Creating the Ratpack web server based on the configuration provided.
        def server = RatpackServer.of(
            srvSpec     ->
            srvSpec.serverConfig(
                srvConf ->
                srvConf.props(UrbanBusRoutingApp.props)
            ).registryOf(
                regSpec ->
                regSpec.add(new UrbanBusRoutingService())
                       .add(server_port)
                       .add(syslog)
            ).handlers(
                chain   ->
                chain.get("$REST_PREFIX$SLASH$REST_DIRECT",
                    ctx ->
                    ctx.render("Not yet implemented.")
                )
            )
        )

        // Starting up the Ratpack web server.
        server.start()
    }
}

// vim:set nu et ts=4 sw=4:
