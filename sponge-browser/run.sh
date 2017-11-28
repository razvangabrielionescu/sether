#!/bin/sh

export JAVA_HOME=/home/flavius/workjfx/jdk1.8.0_144
mvn exec:java -Dexec.mainClass="org.mware.sponge.browser.SpongeBrowserManager" -Dexec.args="6789 localhost" -Djava.rmi.server.hostname=localhost -Dprocess.java.home=$JAVA_HOME
