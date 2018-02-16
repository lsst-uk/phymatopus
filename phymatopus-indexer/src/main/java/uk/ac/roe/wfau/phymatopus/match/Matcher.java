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

package uk.ac.roe.wfau.phymatopus.match;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 */
@Slf4j
@Component
public class Matcher
    {

    /**
     * Public constructor.
     * 
     */
    public Matcher()
        {
        }

    /**
     * The matcher database type.
     * 
     */
    @Value("${databasetype:}")
    protected String databasetype;
    protected String databasetype()
        {
        return this.databasetype;
        }

    /**
     * The matcher database host.
     * 
     */
    @Value("${databasehost:}")
    protected String databasehost;
    protected String databasehost()
        {
        return this.databasehost;
        }

    /**
     * The matcher database port.
     * 
     */
    @Value("${databaseport:}")
    protected String databaseport;
    protected String databaseport()
        {
        return this.databaseport;
        }
    
    /**
     * The matcher database name.
     * 
     */
    @Value("${databasename:}")
    protected String databasename;
    protected String databasename()
        {
        return this.databasename;
        }

    /**
     * The matcher database user name.
     * 
     */
    @Value("${databaseuser:}")
    protected String databaseuser;
    protected String databaseuser()
        {
        return this.databaseuser;
        }

    /**
     * The matcher database password.
     * 
     */
    @Value("${databasepass:}")
    protected String databasepass;
    protected String databasepass()
        {
        return this.databasepass;
        }

    /**
     * The matcher table name.
     * 
     */
    @Value("${tablename:sources}")
    protected String tablename;
    protected String tablename()
        {
        return this.tablename;
        }
    
    /**
     * Our JDBC {@link DataSource}.
     *
     */
    private DataSource source ;

    /**
     * Our JDBC {@link Driver}.
     *
     */
    public Driver driver()
        {
        return new org.hsqldb.jdbc.JDBCDriver();
        }

    /**
     * Generate our database connection url.
     * 
     */
    public String url()
        {
        final StringBuilder builder = new StringBuilder(); 

        builder.append("jdbc:hsqldb:hsql://");
        builder.append(this.databasehost());
        if (this.databaseport() != null)
            {
            builder.append(":");
            builder.append(this.databaseport());
            }
        builder.append("/");
        builder.append(this.databasename());
        
        return builder.toString();
        }

    /**
     * Connect our {@link DataSource}.
     * 
     */
    public DataSource source()
        {
        if (null == this.source)
            {
            this.source = new SimpleDriverDataSource(
                this.driver(),
                this.url(),
                this.databaseuser(),
                this.databasepass()
                );            
            }
        return this.source;
        }

    /*
     * http://www.mchange.com/projects/c3p0/#using_datasources_factory
     * Need to have a Map<uuid,ComboPooledDataSource> for this to make sense. 
     ComboPooledDataSource cpds = new ComboPooledDataSource();
     cpds.setDriverClass( "org.postgresql.Driver" );
     cpds.setJdbcUrl( "jdbc:postgresql://localhost/testdb" );
     cpds.setUser("swaldman");
     cpds.setPassword("test-password");
     
     cpds.setMinPoolSize(5);
     cpds.setAcquireIncrement(5);
     cpds.setMaxPoolSize(20);        
     * 
     */

    /**
     * Connect our {@link DataSource}.
     * @throws SQLException 
     * 
     */
    public Connection connect()
    throws SQLException
        {
        return source().getConnection();
        }

    public static class SourceImpl
    implements Source
        {
        public SourceImpl(final ResultSet results)
        throws SQLException
            {
            this.catalog = results.getString(1);
            this.source  = results.getString(2);
            this.htmid   = results.getLong(3);
            this.ra      = results.getDouble(4);
            this.dec     = results.getDouble(5);

            this.position = new double[2];
            this.position[0] = this.ra;
            this.position[2] = this.dec;
            }
        
        protected String catalog;
        @Override
        public String catalog()
            {
            return this.catalog;
            }

        protected String source;
        @Override
        public String source()
            {
            return this.source;
            }

        protected long htmid;
        @Override
        public long htmid()
            {
            return this.htmid;
            }

        protected double ra;
        @Override
        public double ra()
            {
            return this.ra;
            }

        protected double dec;
        @Override
        public double dec()
            {
            return this.dec;
            }

        protected double[] position;
        @Override
        public double[] position()
            {
            return this.position;
            }
        }
    
    /**
     * 
     * 
     */
    public Iterable<Source> match(final long htmid)
    throws SQLException
        {
        log.debug("match [{}]", htmid);

        final Connection connection  = this.connect();        
        final String template =
                " SELECT" +
                "    catalog," +
                "    sourceid," +
                "    htmid," +
                "    ra," +
                "    dec" +
                " FROM" +
                "    {tablename}" +
                " WHERE" +
                "    htmid = ?"
                ;
        
        final PreparedStatement statement = connection.prepareStatement(
            template.replace(
                "{tablename}",
                this.tablename()
                )
            );
        
        statement.setLong(
            1,
            htmid
            );

        final ResultSet results = statement.executeQuery();        
        final List<Source> list = new ArrayList<Source>();
        while (results.next())
            {
            list.add(
                new SourceImpl(
                    results
                    )
                );
            }
        
        return list;
        }
    }
