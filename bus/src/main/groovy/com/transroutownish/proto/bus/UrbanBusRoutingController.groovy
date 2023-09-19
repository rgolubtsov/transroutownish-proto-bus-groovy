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

//import static com.transroutownish.proto.bus.UrbanBusRoutingHelper.*

/**
 * The controller class of the daemon.
 *
 * @version 0.0.9
 * @since   0.0.9
 */
class UrbanBusRoutingController {
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

//      l.debug "$server_port"
//      s.debug "$server_port"

//      l.debug "$debug_log_enabled"
//      s.debug "$debug_log_enabled"

//      l.debug "$routes_list"
//      s.debug "$routes_list"

/*
        RatpackServer.of(srvSpec ->
                         srvSpec.serverConfig(ServerConfig.embedded())
             .registryOf(regSpec ->
                         regSpec.add(String.class, EMPTY_STRING))
               .handlers(chain   ->
                         chain.get("$REST_PREFIX$SLASH$REST_DIRECT",
                         ctx     ->
                         ctx.render(null))
*/
    }
}

// vim:set nu et ts=4 sw=4:
