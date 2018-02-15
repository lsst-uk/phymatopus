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

package ac.uk.roe.wfau.phymatopus.index;

import ac.uk.roe.wfau.phymatopus.index.Indexer;
import edu.jhu.htm.core.HTMException;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 */
@Slf4j
public class IndexerTest extends TestCase
    {

    /**
     * 
     */
    public IndexerTest()
        {
        }

    public void testCone()
    throws HTMException
        {
        final Indexer matcher = new Indexer();
        
        final Iterable<Long> iter = matcher.circle(123.0, 8.5, 0.0025);

        for (Long htmid : iter)
            {
            log.debug(htmid.toString());
            }
        }
    }
