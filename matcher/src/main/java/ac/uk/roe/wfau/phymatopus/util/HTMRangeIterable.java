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

package ac.uk.roe.wfau.phymatopus.util;

import java.util.Iterator;

import edu.jhu.htm.core.HTMException;
import edu.jhu.htm.core.HTMrange;
import edu.jhu.htm.core.HTMrangeIterator;

/**
 * Implementation of the {@link Iterable} interface to wrap an {@link HTMrange}. 
 *
 */
public class HTMRangeIterable
implements Iterable<Long>
    {
    /**
     * Public constructor.
     * 
     */
    public HTMRangeIterable(final HTMrange range)
        {
        this.range = range;
        }
    
    private final HTMrange range; 
    
    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Long> iterator()
        {
        try {
            return new HTMrangeIterator(
                range,
                false
                );
            }
        catch (HTMException ouch)
            {
            ouch.printStackTrace();
            throw new RuntimeException(ouch);
            }
        }
    }