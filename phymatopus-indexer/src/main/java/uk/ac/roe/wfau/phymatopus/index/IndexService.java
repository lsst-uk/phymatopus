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

package uk.ac.roe.wfau.phymatopus.index;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import edu.jhu.htm.core.HTMException;
import lombok.extern.slf4j.Slf4j;


/**
 * Indexing service that looks up HTM triangle IDs.
 * 
 */
@Slf4j
@Controller
@RequestMapping(IndexModel.INDEX_PATH)
public class IndexService
implements IndexModel
    {


    @SuppressWarnings("serial")
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public static class IndexerException
    extends Exception
        {
        public IndexerException(Exception ouch)
            {
            super(ouch);
            }
        }
    
    /**
     * Public constructor.
     *   
     */
    public IndexService()
        {
        log.debug("IndexService() - constructor");
        this.indexer = new Indexer();
        }

    /**
     * Our indexer.
     * 
     */
    final Indexer indexer ;

    /**
     * Find the HTM triangle for a point.
     * @param point  The point position.
     * @return The HTM triangle ID.
     * @throws IndexerException
     * 
     */
    @RequestMapping(params={PARAM_POINT}, method=RequestMethod.POST, produces=JSON_MIME)
    public ResponseEntity<Long> point(
        @RequestParam(value=PARAM_POINT, required=true)
        final Double[] point
        ) throws IndexerException
        {
        return this.point(
            point[0],
            point[1]
            );
        }
    
    /**
     * Find the HTM triangle for a point.
     * @param ra  The point position.
     * @param dec The point position.
     * @return The HTM triangle ID.
     * @throws IndexerException
     * 
     */
    @RequestMapping(params={PARAM_RA, PARAM_DEC}, method=RequestMethod.POST, produces=JSON_MIME)
    public ResponseEntity<Long> point(
        @RequestParam(value=PARAM_RA, required=true)
        final Double ra,
        @RequestParam(value=PARAM_DEC, required=true)
        final Double dec
        ) throws IndexerException
        {
        try {
            return new ResponseEntity<Long>(
                indexer.point(
                    ra,
                    dec
                    ),
                HttpStatus.OK
                );
            }
        catch (HTMException ouch)
            {
            log.debug("HTMException while processing point");
            log.debug("  Message [{}]", ouch.getMessage());
            throw new IndexerException(
                ouch
                );
            }
        }

    /**
     * Find the HTM triangle for a point.
     * @param point  The point position.
     * @return The HTM triangle ID.
     * @throws IndexerException
     * 
     */
    @RequestMapping(params={PARAM_CIRCLE}, method=RequestMethod.POST, produces=JSON_MIME)
    public ResponseEntity<Iterable<Long>> circle(
        @RequestParam(value=PARAM_CIRCLE, required=true)
        final Double[] circle
        ) throws IndexerException
        {
        return this.circle(
            circle[0],
            circle[1],
            circle[2]
            );
        }
    
    /**
     * Find the HTM triangle for a point.
     * @param point  The point position.
     * @return The HTM triangle ID.
     * @throws IndexerException
     * 
     */
    @RequestMapping(params={PARAM_POINT, PARAM_RADIUS}, method=RequestMethod.POST, produces=JSON_MIME)
    public ResponseEntity<Iterable<Long>> circle(
        @RequestParam(value=PARAM_POINT, required=true)
        final Double[] point,
        @RequestParam(value=PARAM_RADIUS, required=true)
        final Double radius
        ) throws IndexerException
        {
        return this.circle(
            point[0],
            point[1],
            radius
            );
        }
    
    /**
     * Find the HTM triangles for a circle.
     * @param ra  The circle position.
     * @param dec The circle position.
     * @param radius The circle radius.
     * @return An Iterable set of HTM triangle IDs.
     * @throws IndexerException
     * 
     */
    @RequestMapping(params={PARAM_RA, PARAM_DEC, PARAM_RADIUS}, method=RequestMethod.POST, produces=JSON_MIME)
    public ResponseEntity<Iterable<Long>> circle(
        @RequestParam(value=PARAM_RA, required=true)
        final Double ra,
        @RequestParam(value=PARAM_DEC, required=true)
        final Double dec,
        @RequestParam(value=PARAM_RADIUS, required=true)
        final Double radius
        ) throws IndexerException
        {
        try {
            return new ResponseEntity<Iterable<Long>>(
                indexer.circle(
                    ra,
                    dec,
                    radius
                    ),
                HttpStatus.OK
                );
            }
        catch (HTMException ouch)
            {
            log.debug("HTMException while processing circle");
            log.debug("  Message [{}]", ouch.getMessage());
            throw new IndexerException(
                ouch
                );
            }
        }

    }
