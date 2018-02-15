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

/**
 * 
 * 
 */
public class Matcher
    {

    /**
     * 
     */
    public Matcher()
        {
        }

    
    /**
     * Cone search ..
     * @throws HTMException 
     * 
     */
    public void cone(double ra, double dec, double radius)
    throws HTMException
        {
        final int depth = 20 ;
        final HTMindex index = new HTMindexImp(depth);        
        final HTMrange range = new HTMrange();

        final Circle circle = new Circle(
            ra,
            dec,
            radius
            ); 
        final Domain domain = circle.getDomain();

        domain.setOlevel(depth);
        
        domain.intersect(
            (HTMindexImp) index,
            range,
            false
            );        

        @SuppressWarnings("unchecked")
        final Iterator<Long> iter = new HTMrangeIterator(
            range,
            false
            );

        while (iter.hasNext())
            {
            System.out.println(iter.next());        
            }
        }
    }
