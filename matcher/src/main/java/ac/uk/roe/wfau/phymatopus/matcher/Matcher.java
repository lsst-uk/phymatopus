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

package ac.uk.roe.wfau.phymatopus.matcher;

import java.util.Iterator;

import edu.jhu.htm.core.Domain;
import edu.jhu.htm.core.HTMException;
import edu.jhu.htm.core.HTMindex;
import edu.jhu.htm.core.HTMindexImp;
import edu.jhu.htm.core.HTMrange;
import edu.jhu.htm.core.HTMrangeIterator;
import edu.jhu.htm.geometry.Circle;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 */
@Slf4j
public class Matcher
    {

    /**
     * 
     */
    public Matcher()
        {
        log.debug("Matcher - start");
        this.index = new HTMindexImp(depth);
        log.debug("Matcher - done");
        }

    final int depth = 20 ;
    final HTMindex index ;         
    
    /**
     * Cone search ..
     * @throws HTMException 
     * 
     */
    public HTMRangeIterable cone(double ra, double dec, double radius)
    throws HTMException
        {
        log.debug("cone - start");
        final HTMrange range = new HTMrange();
        final Circle circle = new Circle(
            ra,
            dec,
            radius
            ); 
        final Domain domain = circle.getDomain();
        domain.setOlevel(depth);
        
        log.debug("intersect - start");
        domain.intersect(
            (HTMindexImp) index,
            range,
            false
            );        
        log.debug("intersect - done");
        log.debug("cone - done");
        return new HTMRangeIterable(
            range
            );
        }

    public static class HTMRangeIterable
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
    }
