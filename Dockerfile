#
# Dockerfile
# =============================================================================
# Urban bus routing microservice prototype (Groovy port). Version 0.3.1
# =============================================================================
# A daemon written in Groovy, designed and intended to be run
# as a microservice, implementing a simple urban bus routing prototype.
# =============================================================================
# Copyright (C) 2023 Radislav (Radicchio) Golubtsov
#
# (See the LICENSE file at the top of the source tree.)
#

# Note: Since it is supposed that all-in-one TAR distribution bundle
#       was already assembled previously and will be used inside the container
#       as is, there is no need to use official Groovy images
#       (https://hub.docker.com/_/groovy). Instead, it is recommended
#       to use any JRE-only flavors of slim (e.g. Alpine) images.
FROM       azul/zulu-openjdk-alpine:17-jre-headless-latest
USER       daemon
WORKDIR    var/tmp
COPY       bus/build/bus bus/
COPY       data data/
ENTRYPOINT ["bus/bin/bus"]

# vim:set nu ts=4 sw=4:
