#
# Makefile
# =============================================================================
# Urban bus routing microservice prototype (Groovy port). Version 0.3.1
# =============================================================================
# A daemon written in Groovy, designed and intended to be run
# as a microservice, implementing a simple urban bus routing prototype.
# =============================================================================
# Copyright (C) 2023-2025 Radislav (Radicchio) Golubtsov
#
# (See the LICENSE file at the top of the source tree.)
#

SERV    = bus/build
DISTS   = $(SERV)/distributions
VERSION = 0.3.1

# Specify flags and other vars here.
GRADLE_W = ./gradlew
G_WFLAGS = -q
EXPORT   = export
MV       = mv
TAR      = tar
TARFLAGS = -xf
TARFLAGC = -C
UNSET    = unset

# Making the first target (JVM classes).
$(SERV):
	$(GRADLE_W) $(G_WFLAGS) compileGroovy

# Making the second target (TAR/ZIP distributions).
$(DISTS):
	$(GRADLE_W) $(G_WFLAGS) build && \
	$(EXPORT) TARGET=$(SERV) VERSION=$(VERSION) && \
	if [ ! -d $${TARGET}/bus ]; then \
	    $(TAR) $(TARFLAGS) $(DISTS)/bus-$${VERSION}.$(TAR) $(TARFLAGC) $${TARGET} && \
	    $(MV) $${TARGET}/bus-$${VERSION} $${TARGET}/bus; \
	fi; $(UNSET) TARGET VERSION

.PHONY: all clean

all: $(DISTS)

clean:
	$(GRADLE_W) $(G_WFLAGS) clean

# vim:set nu ts=4 sw=4:
