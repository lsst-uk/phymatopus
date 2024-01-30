#
# Copyright (c) 2018, ROE (http://www.roe.ac.uk/)
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

FROM phymatopus/java:2019.09.11
MAINTAINER Dave Morris <docker-admin@metagrid.co.uk>

ENV KAFKA_VERSION 1.1.0
ENV SCALA_VERSION 2.11
ENV FULL_VERSION  ${SCALA_VERSION}-${KAFKA_VERSION}

ENV KAFKA_DIRNAME kafka_${FULL_VERSION}
ENV KAFKA_TARFILE kafka_${FULL_VERSION}.tgz

#
# Set our download directory.
WORKDIR /tmp

#
# Download the tar file
ADD http://www-eu.apache.org/dist/kafka/${KAFKA_VERSION}/${KAFKA_TARFILE}       /tmp/${KAFKA_TARFILE}
ADD http://www-eu.apache.org/dist/kafka/${KAFKA_VERSION}/${KAFKA_TARFILE}.md5   /tmp/${KAFKA_TARFILE}.md5

#
# Check the checksum.
#RUN md5sum -c "${KAFKA_TARFILE}.md5"

#
# Unpack the tar file.
RUN tar -zxf ${KAFKA_TARFILE} -C /var/local
RUN rm ${KAFKA_TARFILE}
RUN rm ${KAFKA_TARFILE}.md5

#
# Link the unpacked directory
WORKDIR /var/local
RUN ln -sf ${KAFKA_DIRNAME} kafka

#
# Make this the working directory
WORKDIR /var/local/kafka

