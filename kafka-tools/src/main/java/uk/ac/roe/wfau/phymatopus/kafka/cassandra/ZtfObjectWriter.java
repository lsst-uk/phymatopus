/*
 *  Copyright (C) 2019 Royal Observatory, University of Edinburgh, UK
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
package uk.ac.roe.wfau.phymatopus.kafka.cassandra;


import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlert;

/**
 * Simple writer for the objects table.
 *
 */
@Slf4j
public class ZtfObjectWriter
extends AbstractCassandraWriter
    {
    /**
     * Public constructor. 
     * 
     */
    public ZtfObjectWriter(final String hostname, final String dcname)
        {
        super(
            hostname,
            dcname
            );
        }

    @Override
    public String statement()
        {
        return null;
        }

    @Override
    public BoundStatement bind(PreparedStatement prepared, ZtfAlert object)
        {
        return null;
        }
    }
