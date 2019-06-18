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


import java.net.InetSocketAddress;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlert;

/**
 * Simple writer for the candidates table.
 *
 */
@Slf4j
public abstract class AbstractCassandraWriter
   {
    protected static final Integer DEFAULT_PORT = 9042 ;

    protected InetSocketAddress endpoint ;

    protected String dcname ;
    
    /**
     * Public constructor. 
     * 
     */
    public AbstractCassandraWriter(final String hostname, final String dcname)
        {
        this(
            hostname,
            DEFAULT_PORT,
            dcname
            );
        }

    /**
     * Public constructor. 
     * 
     */
    public AbstractCassandraWriter(final String hostname, final Integer port, final String dcname)
        {
        this(
            new InetSocketAddress(
                hostname,
                port
                ),
            dcname
            );
        }

    /**
     * Public constructor. 
     * 
     */
    public AbstractCassandraWriter(final InetSocketAddress endpoint, final String dcname)
        {
        this.endpoint = endpoint; 
        this.dcname   = dcname; 
        }

    private   CqlSession session ; 
    protected CqlSession session()
        {
        return this.session;
        }

    private   PreparedStatement prepared ;        
    protected PreparedStatement prepared()
        {
        return this.prepared;
        }

    /**
     * Initialise our writer.
     * 
     */
    public void init()
        {
        log.debug("init - start");
        synchronized(this)
            {
            if (this.session == null)
                {
                CqlSessionBuilder builder = CqlSession.builder().addContactPoint(
                    this.endpoint
                    ).withLocalDatacenter(
                        this.dcname
                        );
                this.session = builder.build();
                }
            if (this.prepared == null)
                {
                this.prepared = session.prepare(
                    this.statement()
                    );
                }
            }
        log.debug("init - done");
        }

    /**
     * Disconnect our cluster.
     * 
     */
    public void done()
        {
        log.debug("done- start");
        synchronized (this)
            {
            if (this.session != null)
                {
                session.close();
                session = null ;
                }
            }
        log.debug("init - done");
        }

    /**
     * Insert a candidate into the database.
     * 
     */
    public void insert(final ZtfAlert alert)
        {
        session.execute(
            bind(
                this.prepared,
                alert
                )
            );        
        }

    /**
     * The CQL prepared statement as a String.
     * 
     */
    public abstract String statement();
    
    /**
     * Bind values to a prepared statement.
     * 
     */
    public abstract BoundStatement bind(final PreparedStatement prepared, final ZtfAlert alert);
    
    }
