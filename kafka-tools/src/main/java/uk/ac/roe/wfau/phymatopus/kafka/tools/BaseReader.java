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
        }

    /**
     * Configuration bean implementation.
     * 
     */
    @Slf4j
    public static class ConfigurationBean extends BaseClient.ConfigurationBean implements Configuration 
        {
        /**
         * Public constructor.
         * 
         */
        public ConfigurationBean(final String servers, final String topic, final String group)
            {
            super(
                servers,
                topic,
                group
                );
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
