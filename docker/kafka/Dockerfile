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

#
# Extend the original Dockerfile from Confluent.
# https://github.com/confluentinc/cp-docker-images/blob/4.0.x/debian/kafka/Dockerfile
FROM confluentinc/cp-kafka:4.0.0
MAINTAINER Dave Morris <docker-admin@metagrid.co.uk>

#
# Install our scripts.
COPY bin/phymatopus /bin/phymatopus
RUN chmod a+x /bin/phymatopus/*

#
# Override the original command.
CMD ["/bin/phymatopus/configure"]
