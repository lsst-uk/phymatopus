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

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;

/**
 * Common base class for Kafka readers.
 * 
 * 
 */
public class BaseReader extends BaseClient
    {
    /**
     * Public interface for a Reader configuration.
     * 
     */
    public static interface Configuration extends BaseClient.Configuration
        {
        /**
         * The timeout for waiting for new messages.
         * 
         */
        public Duration getLoopTimeout();

        /**
         * The timeout for polling the server.
         * 
         */
        public Duration getPollTimeout();
        
        }

    /**
     * Configuration bean implementation.
     * 
     */
    @Slf4j
    public static class ConfigurationBean extends BaseClient.ConfigurationBean implements Configuration 
        {
        static final Boolean  DEFAULT_AUTOCOMIT = true ;
        static final Duration DEFAULT_LOOPTIMEOUT = Duration.ofMinutes(10);
        static final Duration DEFAULT_POLLTIMEOUT = Duration.ofSeconds(10);
        
        /**
         * Public constructor.
         * 
         */
        public ConfigurationBean(final String servers, final String topic, final String group)
            {
            this(
                DEFAULT_LOOPTIMEOUT,
                DEFAULT_POLLTIMEOUT,
                servers,
                topic,
                group
                );
            }
        /**
         * Public constructor.
         * 
         */
        public ConfigurationBean(final Duration looptimeout, final Duration polltimeout, final String servers, final String topic, final String group)
            {
            super(
                servers,
                topic,
                group
                );
            this.polltimeout = polltimeout;
            this.looptimeout = looptimeout;
            log.debug("polltimeout [{}]", polltimeout);
            log.debug("looptimeout [{}]", looptimeout);
            }

        private final Duration looptimeout;
        @Override
        public Duration getLoopTimeout()
            {
            return this.looptimeout;
            }

        private final Duration polltimeout;
        @Override
        public Duration getPollTimeout()
            {
            return this.polltimeout;
            }
        }

    /**
     * Our reader configuration..
     * 
     */
    protected Configuration config;

    /**
     * Public constructor.
     * @param config The reader configuration. 
     * 
     */
    public BaseReader(final Configuration config)
        {
        super(config);
        this.config = config;
        }

    }
