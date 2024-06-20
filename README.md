# Trans-RoutE-Townish (transroutownish) :small_blue_diamond: Urban bus routing microservice prototype (Groovy port)

**A daemon written in Groovy, designed and intended to be run as a microservice,
<br />implementing a simple urban bus routing prototype**

**Rationale:** This project is a *direct* **[Groovy](https://groovy-lang.org "Apache Groovy - A multi-faceted language for the Java platform")** port of the earlier developed **urban bus routing prototype**, written in Java using **[Spring Boot](https://spring.io/projects/spring-boot "Stand-alone Spring apps builder and runner")** framework, and tailored to be run as a microservice in a Docker container. The following description of the underlying architecture and logics has been taken **[from here](https://github.com/rgolubtsov/transroutownish-proto-bus-spring-boot)** as is, without any modifications or adjustment.

Consider an IoT system that aimed at planning and forming a specific bus route for a hypothetical passenger. One crucial part of such system is a **module**, that is responsible for filtering bus routes between two arbitrary bus stops where a direct route is actually present and can be easily found. Imagine there is a fictional urban public transportation agency that provides a wide series of bus routes, which covered large city areas, such that they are consisting of many bus stop points in each route. Let's name this agency **Trans-RoutE-Townish Co., Ltd.** or in the Net representation &mdash; **transroutownish.com**, hence the name of the project.

A **module** that is developed here is dedicated to find out quickly, whether there is a direct route in a list of given bus routes between two specified bus stops. It should immediately report back to the IoT system with the result `true` if such a route is found, i.e. it exists in the bus routes list, or `false` otherwise, by outputting a simple JSON structure using the following format:

```
{
    "from"   : <starting_bus_stop_point>,
    "to"     : <ending_bus_stop_point>,
    "direct" : true
}
```

`<starting_bus_stop_point>` and `<ending_bus_stop_point>` above are bus stop IDs: unique positive integers, taken right from inputs.

A bus routes list is a plain text file where each route has its own unique ID (positive integer) and a sequence of its bus stop IDs. Each route occupies only one line in this file, so that they are all representing something similar to a list &mdash; the list of routes. The first number in a route is always its own ID. Other consequent numbers after it are simply IDs of bus stops in this route, up to the end of line. All IDs in each route are separated by whitespace, usually by single spaces or tabs, but not newline.

There are some constraints:
1. Routes are considered not to be a round trip journey, that is they are operated in the forward direction only.
2. All IDs (of routes and bus stops) must be represented by positive integer values, in the range `1 .. 2,147,483,647`.
3. Any bus stop ID may occure in the current route only once, but it might be presented in any other route too.

The list of routes is usually mentioned throughout the source code as a **routes data store**, and a sample routes data store can be found in the `data/` directory of this repo.

Since the microservice architecture for building independent backend modules of a composite system are very prevalent nowadays, this seems to be natural for creating a microservice, which is containerized and run as a daemon, serving a continuous flow of HTTP requests.

This microservice is intended to be built locally and to be run like a conventional daemon in the VM environment, as well as a containerized service, managed by Docker.

One may consider this project has to be suitable for a wide variety of applied areas and may use this prototype as: (1) a template for building a similar microservice, (2) for evolving it to make something more universal, or (3) to simply explore it and take out some snippets and techniques from it for *educational purposes*, etc.

---

## Table of Contents

* **[Building](#building)**
  * **[Creating a Docker image](#creating-a-docker-image)**
* **[Running](#running)**
  * **[Running a Docker image](#running-a-docker-image)**
  * **[Exploring a Docker image payload](#exploring-a-docker-image-payload)**
* **[Consuming](#consuming)**
  * **[Logging](#logging)**
  * **[Error handling](#error-handling)**

## Building

The microservice might be built and run successfully under **Ubuntu Server (Ubuntu 22.04.3 LTS x86-64)**. Install the necessary dependencies (`openjdk-17-jre-headless`, `groovy`, `gradle`, `make`, `docker.io`):

```
$ sudo apt-get update && \
  sudo apt-get install openjdk-17-jre-headless make docker.io -y
```

Since Groovy and Gradle packages are somehow outdated in the stock Ubuntu package repository, they are preferred to be installed through the SDKMAN! toolkit. For that, first it needs to install SDKMAN! and to `source` its initialization script:

```
$ curl -s https://get.sdkman.io | bash
...
$ . /home/<username>/.sdkman/bin/sdkman-init.sh
```

Then install latest stable versions of Groovy and Gradle via SDKMAN!:

```
$ sdk install groovy
...
$ sdk install gradle
...
```

**Build** the microservice using **Gradle Wrapper**:

```
$ ./gradlew clean
...
$ ./gradlew compileGroovy
...
$ ./gradlew build && \
  export TARGET=bus/build VERSION=0.3.1 && \
  if [ ! -d ${TARGET}/bus ]; then \
      tar -xf ${TARGET}/distributions/bus-${VERSION}.tar -C ${TARGET} && \
      mv ${TARGET}/bus-${VERSION} ${TARGET}/bus; \
  fi; unset TARGET VERSION
...
```

Or **build** the microservice using **GNU Make** (optional, but for convenience &mdash; it covers the same **Gradle Wrapper** build workflow under the hood):

```
$ make clean
$
$ make      # <== Compilation phase.
$
$ make all  # <== Assembling distributions of the microservice.
```

### Creating a Docker image

**Build** a Docker image for the microservice:

```
$ # Pull the JRE image first, if not already there:
$ sudo docker pull azul/zulu-openjdk-alpine:17-jre-headless-latest
...
$ # Then build the microservice image:
$ sudo docker build -ttransroutownish/busgrv .
...
```

## Running

**Run** the microservice using **Gradle Wrapper** (generally for development and debugging purposes):

```
$ ./gradlew -q run; echo $?
$ #               ^   ^   ^
$ #               |   |   |
$ # --------------+---+---+
$ # Whilst this is not necessary, it's beneficial knowing the exit code.
...
```

**Run** the microservice using its startup script from the extracted all-in-one TAR distribution bundle, assembled previously by the `build` (or `all`) target:

```
$ ./bus/build/bus/bin/bus; echo $?
...
```

### Running a Docker image

**Run** a Docker image of the microservice, deleting all stopped containers prior to that:

```
$ sudo docker rm `sudo docker ps -aq`; \
  export PORT=8765 && sudo docker run -dp${PORT}:${PORT} --name busgrv transroutownish/busgrv; echo $?
...
```

### Exploring a Docker image payload

The following is not necessary but might be considered interesting &mdash; to look up into the running container, and check out that the microservice's startup script, JAR file, log, and routes data store are at their expected places and in effect:

```
$ sudo docker ps -a
CONTAINER ID   IMAGE                    COMMAND         CREATED             STATUS             PORTS                                       NAMES
<container_id> transroutownish/busgrv   "bus/bin/bus"   About an hour ago   Up About an hour   0.0.0.0:8765->8765/tcp, :::8765->8765/tcp   busgrv
$
$ sudo docker exec -it busgrv sh; echo $?
/var/tmp $
/var/tmp $ java --version
openjdk 17.0.9 2023-10-17 LTS
OpenJDK Runtime Environment Zulu17.46+19-CA (build 17.0.9+8-LTS)
OpenJDK 64-Bit Server VM Zulu17.46+19-CA (build 17.0.9+8-LTS, mixed mode, sharing)
/var/tmp $
/var/tmp $ ls -al
total 24
drwxrwxrwt    1 root     root          4096 Nov 21 07:45 .
drwxr-xr-x    1 root     root          4096 Sep 28 11:18 ..
drwxr-xr-x    4 root     root          4096 Nov 21 07:20 bus
drwxr-xr-x    2 root     root          4096 Nov 21 07:20 data
drwxr-xr-x    2 daemon   daemon        4096 Nov 21 07:45 log
/var/tmp $
/var/tmp $ ls -al bus/bin/bus bus/lib/bus-0.3.1.jar data/ log/
-rwxr-xr-x    1 root     root         10843 Nov 21 07:10 bus/bin/bus
-rw-r--r--    1 root     root         33895 Nov 21 07:10 bus/lib/bus-0.3.1.jar

data/:
total 56
drwxr-xr-x    2 root     root          4096 Nov 21 07:20 .
drwxrwxrwt    1 root     root          4096 Nov 21 07:45 ..
-rw-r--r--    1 root     root         46218 Nov 21 06:10 routes.txt

log/:
total 16
drwxr-xr-x    2 daemon   daemon        4096 Nov 21 07:45 .
drwxrwxrwt    1 root     root          4096 Nov 21 07:45 ..
-rw-r--r--    1 daemon   daemon        4459 Nov 21 07:45 bus.log
/var/tmp $
/var/tmp $ netstat -plunt
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name
tcp        0      0 0.0.0.0:8765            0.0.0.0:*               LISTEN      1/java
/var/tmp $
/var/tmp $ ps ax
PID   USER     TIME  COMMAND
    1 daemon    0:04 /usr/lib/jvm/zulu17/bin/java -classpath /var/tmp/bus/lib/bus-0.3.1.jar:/var/tmp/bus/lib/groovy-4.0.15.jar:/var/tmp/bus/lib/ratpack-...
   62 daemon    0:00 sh
   89 daemon    0:00 ps ax
/var/tmp $
/var/tmp $ exit # Or simply <Ctrl-D>.
0
```

## Consuming

All the routes are contained in a so-called **routes data store**. It is located in the `data/` directory. The default filename for it is `routes.txt`, but it can be specified explicitly (if intended to use another one) in the `bus/src/main/resources/application.properties` file.

**Identify**, whether there is a direct route between two bus stops with IDs given in the **HTTP GET** request, searching for them against the underlying **routes data store**:

HTTP request param | Sample value | Another sample value | Yet another sample value
------------------ | ------------ | -------------------- | ------------------------
`from`             | `4838`       | `82`                 | `2147483647`
`to`               | `524987`     | `35390`              | `1`

The direct route is found:

```
$ curl 'http://localhost:8765/route/direct?from=4838&to=524987'
{"from":4838,"to":524987,"direct":true}
```

The direct route is not found:

```
$ curl 'http://localhost:8765/route/direct?from=82&to=35390'
{"from":82,"to":35390,"direct":false}
```

### Logging

The microservice has the ability to log messages to a logfile and to the Unix syslog facility. When running under Ubuntu Server (not in a Docker container), logs can be seen and analyzed in an ordinary fashion, by `tail`ing the `log/bus.log` logfile:

```
$ tail -f log/bus.log
...
[2023-11-21][10:25:51][INFO ]  Server started on port 8765
...
[2023-11-21][10:30:06][DEBUG]  from=4838 | to=524987
[2023-11-21][10:30:06][DEBUG]  1 =  1 2 3 4 5 6 7 8 9 987 11 12 13 4987 415 ...
...
[2023-11-21][10:30:26][DEBUG]  from=82 | to=35390
[2023-11-21][10:30:26][DEBUG]  1 =  1 2 3 4 5 6 7 8 9 987 11 12 13 4987 415 ...
...
[2023-11-21][10:35:06][INFO ]  Server stopped
...
```

Messages registered by the Unix system logger can be seen and analyzed using the `journalctl` utility:

```
$ journalctl -f
...
Nov 21 10:25:51 <hostname> java[<pid>]: Server started on port 8765
Nov 21 10:30:06 <hostname> java[<pid>]: from=4838 | to=524987
Nov 21 10:30:26 <hostname> java[<pid>]: from=82 | to=35390
Nov 21 10:35:06 <hostname> java[<pid>]: Server stopped
```

Inside the running container logs might be queried also by `tail`ing the `log/bus.log` logfile:

```
/var/tmp $ tail -f log/bus.log
...
[2023-11-21][07:45:07][INFO ]  Server started on port 8765
...
[2023-11-21][08:00:10][DEBUG]  from=4838 | to=524987
[2023-11-21][08:00:10][DEBUG]  1 =  1 2 3 4 5 6 7 8 9 987 11 12 13 4987 415 ...
...
[2023-11-21][08:00:27][DEBUG]  from=82 | to=35390
[2023-11-21][08:00:27][DEBUG]  1 =  1 2 3 4 5 6 7 8 9 987 11 12 13 4987 415 ...
...
```

And of course Docker itself gives the possibility to read log messages by using the corresponding command for that:

```
$ sudo docker logs -f busgrv
...
[2023-11-21][07:45:07][INFO ]  Server started on port 8765
...
[2023-11-21][08:00:10][DEBUG]  from=4838 | to=524987
[2023-11-21][08:00:10][DEBUG]  1 =  1 2 3 4 5 6 7 8 9 987 11 12 13 4987 415 ...
...
[2023-11-21][08:00:27][DEBUG]  from=82 | to=35390
[2023-11-21][08:00:27][DEBUG]  1 =  1 2 3 4 5 6 7 8 9 987 11 12 13 4987 415 ...
...
[2023-11-21][08:05:09][INFO ]  Server stopped
...
```

### Error handling

When the query string passed in a request, contains inappropriate input, or the URI endpoint doesn't contain anything else at all after its path, the microservice will respond with the **HTTP 400 Bad Request** status code, including a specific response body in JSON representation, like the following:

```
$ curl 'http://localhost:8765/route/direct?from=qwerty4838&to=-i-.;--089asdf../nj524987'
{"error":"Request parameters must take positive integer values, in the range 1 .. 2,147,483,647. Please check your inputs."}
```

Or even simpler:

```
$ curl http://localhost:8765/route/direct
{"error":"Request parameters must take positive integer values, in the range 1 .. 2,147,483,647. Please check your inputs."}
```
