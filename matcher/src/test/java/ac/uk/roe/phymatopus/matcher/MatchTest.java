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

package ac.uk.roe.phymatopus.matcher;

import ac.uk.roe.wfau.phymatopus.matcher.Matcher;
import edu.jhu.htm.core.HTMException;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 */
@Slf4j
public class MatchTest extends TestCase
    {

    /**
     * 
     */
    public MatchTest()
        {
        }

    public void testCone()
    throws HTMException
        {
        final Matcher matcher = new Matcher();
        
        final Iterable<Long> iter = matcher.cone(123.0, 8.5, 0.0025);

        for (Long htmid : iter)
            {
            log.debug(htmid.toString());
            }
        }
    }
