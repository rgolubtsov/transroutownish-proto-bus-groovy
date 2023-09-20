/*
 * bus/src/main/groovy/com/transroutownish/proto/bus/
 * UrbanBusRoutingController.groovy
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

import java.lang.invoke.MethodHandles

import ratpack.server.RatpackServer

import static com.transroutownish.proto.bus.UrbanBusRoutingHelper.*

/**
 * The controller class of the daemon.
 *
 * @version 0.0.9
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
     * Starts up the bundled web server.
     *
     * @param args The list containing the server port number to listen on,
     *             as the first element.
     */
    void startup(final List args) {
        def server_port       = args[0]
        def debug_log_enabled = args[1]
        def routes_list       = args[2]
        def s                 = args[3]

//      l.debug "$debug_log_enabled"
//      s.debug "$debug_log_enabled"

//      l.debug "$routes_list"
//      s.debug "$routes_list"

        // Creating the Ratpack web server based on the configuration provided.
        def server = RatpackServer.of(
            srvSpec     ->
            srvSpec.serverConfig(
                srvConf ->
                srvConf.port(server_port)
            ).registryOf(
                regSpec ->
                regSpec.add(String.class, EMPTY_STRING)
            ).handlers(
                chain   ->
                chain.get("$REST_PREFIX$SLASH$REST_DIRECT",
                    ctx ->
                    ctx.render(null)
                )
            )
        )

        // Starting up the Ratpack web server.
        server.start()
    }
}

// vim:set nu et ts=4 sw=4:
