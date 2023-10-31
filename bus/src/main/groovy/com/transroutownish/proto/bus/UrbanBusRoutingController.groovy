/*
 * bus/src/main/groovy/com/transroutownish/proto/bus/
 * UrbanBusRoutingController.groovy
 * ============================================================================
 * Urban bus routing microservice prototype (Groovy port). Version 0.1.10
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

import ratpack.error.ClientErrorHandler

import ratpack.handling.Context

import ratpack.http.Status

import ratpack.jackson.Jackson

import ratpack.server.RatpackServer

import static com.transroutownish.proto.bus.UrbanBusRoutingHelper.*

/**
 * The controller class of the daemon.
 *
 * @version 0.1.10
 * @since   0.0.9
 */
class UrbanBusRoutingController {
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

            l.info(MSG_SERVER_STARTED + server_port)
            s.info(MSG_SERVER_STARTED + server_port)
        }

        /**
         * Gets called just before the server is about to be stopped.
         *
         * @param event The metadata object containing the stop event info.
         */
        void onStop(final StopEvent event) {
            def registry = event.getRegistry()

            def s = registry.get(UnixSyslog)

            l.info(MSG_SERVER_STOPPED)
            s.info(MSG_SERVER_STOPPED)

            // Closing the system logger.
            // Calling <syslog.h> closelog();
            s.shutdown()
        }
    }

    /**
     * The client error handler class for the daemon's controller.
     * <br />
     * <br />It handles HTTP errors from the 4xx Client Error section
     * of status codes.
     */
    class UrbanBusRoutingClientError implements ClientErrorHandler {
        /**
         * Gets called when one of the <strong>4xx Client Error</strong>
         * section's errors occurred.
         *
         * @param ctx         The context of the HTTP handler to deal
         *                    with a request/response pair.
         * @param status_code The 4xx status code that explains
         *                    the client error.
         */
        void error(final Context ctx, final int status_code) {
            def uri = ctx.getRequest().getUri()

            def status = Status.IM_A_TEAPOT // <== HTTP 418 I'm a teapot
            def body   = EMPTY_STRING       // <== No body in the response.

            switch (status_code) {
                case HTTP_400:
                    status = Status.BAD_REQUEST
                    body   = ERR_REQ_PARAMS_MUST_BE_POSITIVE_INTS

                    break
                case HTTP_404:
                    status = Status.NOT_FOUND
                    body   = ERR_NOT_FOUND

                    break
                case HTTP_405:
                    status = Status.METHOD_NOT_ALLOWED

                    break
                default:
                    l.debug('''HTTP 418 I'm a teapot:\
 it's issued for the following URI:''')
            }

            l.debug(uri)

            def resp = ctx.header(HDR_SERVER_N, HDR_SERVER_V)
                          .getResponse().status(status)

            if (body.isEmpty()) {
                resp.send(MIME_TYPE, body)
            } else {
                ctx.render(Jackson.json(
                    new UrbanBusRoutingResponseError(body)
                ))
            }
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
            s // <== The Unix system logger.
        ) = args

        // Creating the Ratpack web server based on the configuration provided.
        def server = RatpackServer.of(
            srvSpec     ->
            srvSpec.serverConfig(
                srvConf ->
                srvConf.props(UrbanBusRoutingApp.props)
            ).registryOf(
                regSpec ->
                regSpec.add(new UrbanBusRoutingService())
                       .add(new UrbanBusRoutingClientError())
                       .add(server_port)
                       .add(s)
            ).handlers(
                chain   -> // GET /route/direct
                chain.get("$REST_PREFIX$SLASH$REST_DIRECT", ctx -> {
                    def params = ctx.getRequest().getQueryParams()

                    def from_ = params.from
                    def to_   = params.to

                    if (debug_log_enabled) {
                        l.debug(FROM + EQUALS + from_ + SPACE + V_BAR + SPACE
                              + TO   + EQUALS + to_)

                        s.debug(FROM + EQUALS + from_ + SPACE + V_BAR + SPACE
                              + TO   + EQUALS + to_)
                    }

                    // --------------------------------------------------------
                    // --- Parsing and validating request params - Begin ------
                    // --------------------------------------------------------
                    def is_request_malformed = false

                    def from = 0
                    def to   = 0

                    try {
                        from = Integer.parseInt(from_)
                        to   = Integer.parseInt(to_  )

                        if ((from < 1) || (to < 1)) {
                            is_request_malformed = true
                        }
                    } catch (NumberFormatException e) {
                        is_request_malformed = true
                    }
                    // --------------------------------------------------------
                    // --- Parsing and validating request params - End --------
                    // --------------------------------------------------------

                    ctx.header(HDR_SERVER_N, HDR_SERVER_V)

                    if (is_request_malformed) {
                        ctx.getResponse().status(Status.BAD_REQUEST)

                        ctx.render(Jackson.json(
                            new UrbanBusRoutingResponseError(
                                ERR_REQ_PARAMS_MUST_BE_POSITIVE_INTS)
                        ))
                    } else {
                        // Performing the routes processing to find out
                        // the direct route.
                        def direct = false // find_direct_route(from, to)

                        ctx.render(Jackson.json(
                            new UrbanBusRoutingResponseOk(from, to, direct)
                        ))
                    }
                })
            )
        )

        // Trying to start up the Ratpack web server.
        try {
            server.start()
        } catch (Exception e) {
            if ((e instanceof io.netty.channel.unix.Errors.NativeIoException)
                && (e.expectedErr() === ERR_EADDRINUSE_NEGATIVE)) {

                l.error(ERR_CANNOT_START_SERVER + ERR_ADDR_ALREADY_IN_USE)
            } else {
                l.error(ERR_CANNOT_START_SERVER + ERR_SERV_UNKNOWN_REASON)
            }
        }
    }
}

// vim:set nu et ts=4 sw=4:
