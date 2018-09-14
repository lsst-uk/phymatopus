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

/**
 * Common base class for Kafka clients.
 * 
 */
public class BaseClient
    {

    /**
     * Our target Kafka server name(s).
     * 
     */
    protected String servers;
    protected String servers()
        {
        return this.servers.trim();
        }

    /**
     * Our target Kafka topic.
     * 
     */
    protected String topic;
    protected String topic()
        {
        return this.topic.trim();
        }

    /**
     * Public constructor.
     * @param servers The list of bootstrap Kafka server names.
     * @param topic The Kafka topic name.
     *  
     */
    public BaseClient(final String servers, final String topic)
        {
        this.topic   = topic;
        this.servers = servers;
        }
    }
