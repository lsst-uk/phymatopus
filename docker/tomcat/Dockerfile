#
# Copyright (c) 2014, ROE (http://www.roe.ac.uk/)
# All rights reserved.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

FROM phymatopus/java:2018.06.15
MAINTAINER [Dave Morris <docker-admin@metagrid.co.uk>]

# https://tomcat.apache.org/whichversion.html
ENV TOMCAT_VERSION 8.5.24

ENV TOMCAT_DIRNAME apache-tomcat-${TOMCAT_VERSION}
ENV TOMCAT_TARFILE apache-tomcat-${TOMCAT_VERSION}.tar.gz

ENV TOMCAT_USER tomcat
ENV TOMCAT_ROOT /var/local/tomcat

#
# Expose Tomcat port
EXPOSE :8080

#
# Install the Tomcat Native Library
# RUN dnf -y install tomcat-native

#
# Create the Tomcat user.
RUN useradd --system --home "$TOMCAT_ROOT" "$TOMCAT_USER"

#
# Set our download directory.
WORKDIR /tmp

#
# Download the tar file
# Avoid using ADD - unreliable results.
# https://github.com/moby/moby/issues/34243
RUN wget --no-verbose https://archive.apache.org/dist/tomcat/tomcat-8/v${TOMCAT_VERSION}/bin/${TOMCAT_TARFILE}
RUN wget --no-verbose https://archive.apache.org/dist/tomcat/tomcat-8/v${TOMCAT_VERSION}/bin/${TOMCAT_TARFILE}.md5

#
# Check the checksum.
RUN md5sum -c "${TOMCAT_TARFILE}.md5"

#
# Unpack the tar file.
RUN tar -zxf  ${TOMCAT_TARFILE}
RUN rm ${TOMCAT_TARFILE}
RUN rm ${TOMCAT_TARFILE}.md5

#
# Remove the default webapps.
RUN rm -rf ${TOMCAT_DIRNAME}/webapps/*

#
# Relocate the unpacked directory
RUN mv ${TOMCAT_DIRNAME} ${TOMCAT_ROOT}

#
# Change ownership of our working directories.
# ** This needs to be done externally if they are mounted as host volumes.
RUN chown -R ${TOMCAT_USER}:${TOMCAT_USER} ${TOMCAT_ROOT}/logs
RUN chown -R ${TOMCAT_USER}:${TOMCAT_USER} ${TOMCAT_ROOT}/work
RUN chown -R ${TOMCAT_USER}:${TOMCAT_USER} ${TOMCAT_ROOT}/temp

#RUN chmod u=rwxs,g=rwxs ${TOMCAT_ROOT}/logs
#RUN chmod u=rwxs,g=rwxs ${TOMCAT_ROOT}/work
#RUN chmod u=rwxs,g=rwxs ${TOMCAT_ROOT}/temp

#
# Allow read access to everything.
RUN find ${TOMCAT_ROOT} -type d -exec chmod a+rx '{}' \;
RUN find ${TOMCAT_ROOT} -type f -exec chmod a+r  '{}' \;

#
# Allow execute access to the scripts.
RUN find ${TOMCAT_ROOT}/bin -name '*.sh' -exec chmod a+rx '{}' \;

#
# Create our config directory.
RUN mkdir ${TOMCAT_ROOT}/conf/Catalina
RUN mkdir ${TOMCAT_ROOT}/conf/Catalina/localhost
RUN chown ${TOMCAT_USER}:${TOMCAT_USER} ${TOMCAT_ROOT}/conf/Catalina/localhost

#
# Define our working directories as volumes.
# BUG -- ENV variable not resolved
# BUG -- VOLUME needs to be done AFTER any chown and chmod
# http://container-solutions.com/understanding-volumes-docker/#permissions-and-ownership
VOLUME /var/local/tomcat/logs
VOLUME /var/local/tomcat/work
VOLUME /var/local/tomcat/temp

#
# Add the service health check.
HEALTHCHECK \
    --interval=30s \
    --timeout=5s \
    CMD curl --head --silent http://localhost:8080/ || exit 1

#
# Run Tomcat
# BUG -- ENV variable not resolved
USER    tomcat
WORKDIR /var/local/tomcat
CMD /var/local/tomcat/bin/catalina.sh run

