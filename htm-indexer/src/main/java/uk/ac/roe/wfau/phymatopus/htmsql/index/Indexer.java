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

package uk.ac.roe.wfau.phymatopus.htmsql.index;

import org.springframework.stereotype.Component;

import edu.jhu.htm.core.Domain;
import edu.jhu.htm.core.HTMException;
import edu.jhu.htm.core.HTMindex;
import edu.jhu.htm.core.HTMindexImp;
import edu.jhu.htm.core.HTMrange;
import edu.jhu.htm.geometry.Circle;
import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.htmsql.util.HTMRangeIterable;

/**
 * 
 * 
 */
@Slf4j
@Component
public class Indexer
    {
    /**
     * Default depth.
     * 
     */
    public static final int DEFAULT_DEPTH = 20 ;

    /**
     * Public constructor.
     * 
     */
    public Indexer()
        {
        this(
            DEFAULT_DEPTH
            );
        }
    /**
     * Public constructor.
     * 
     */
    public Indexer(final int depth)
        {
        log.debug("Matcher - start");
        this.depth = depth;
        this.index = new HTMindexImp(depth);
        log.debug("Matcher - done");
        }

    final int depth ;
    final HTMindex index ;         

    /**
     * Get a  HTM triangle that contains a point. 
     * @param ra  The point position.
     * @param dec The point position.
     * @throws HTMException 
     * 
     */
    public Long point(double ra, double dec)
    throws HTMException
        {
        log.debug("point [{}][{}]", ra, dec);
        return index.lookupId(
            ra,
            dec
            );
        }
    
    /**
     * Get a list of HTM triangles that intersect a circle. 
     * @param ra  The circle position.
     * @param dec The circle position.
     * @param radius The circle radius.
     * @throws HTMException 
     * 
     */
    public HTMRangeIterable circle(double ra, double dec, double radius)
    throws HTMException
        {
        log.debug("circle [{}][{}][{}]", ra, dec, radius);
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
        return new HTMRangeIterable(
            range
            );
        }
    }
