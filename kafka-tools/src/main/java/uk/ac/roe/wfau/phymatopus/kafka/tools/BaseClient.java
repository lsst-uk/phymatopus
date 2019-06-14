/*
 *  Copyright (C) 2018 Royal Observatory, University of Edinburgh, UK
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package uk.ac.roe.wfau.phymatopus.kafka.tools;

import lombok.extern.slf4j.Slf4j;

/**
 * Common base class for Kafka clients.
 * 
 */
public class BaseClient
    {
    /**
     * Public interface for a Kafka client configuration.
     * 
     */
    public static interface Configuration
        {
        /**
         * The bootstrap list of servers.
         * 
         */
        public String getServers();

        /**
         * The topic name.
         * 
         */
        public String getTopic();
        }

    /**
     * Configuration bean implementation.
     * 
     */
    @Slf4j
    public static class ConfigurationBean implements Configuration 
        {
        /**
         * Public constructor.
         * 
         */
        public ConfigurationBean(final String servers, final String topic)
            {
            this.servers = servers;
            this.topic   = topic;
            log.debug("servers [{}]", servers);
            log.debug("topic   [{}]", topic);
            }

        private final String servers;
        @Override
        public String getServers()
            {
            return this.servers;
            }

        private final String topic;
        @Override
        public String getTopic()
            {
            return this.topic;
            }
        }

    /**
     * Our client configuration.
     * 
     */
    protected Configuration config;

    /**
     * Public constructor.
     * @param servers The list of bootstrap Kafka server names.
     * @param topic The Kafka topic name.
     *  
     */
    public BaseClient(final Configuration config)
        {
        this.config = config;
        }
    }
