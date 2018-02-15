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
@RequestMapping(ServiceModel.INDEX_PATH)
public class IndexService
implements ServiceModel
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
     * Find the HTM triangles that intersect a circle.
     * 
     * @param ra  The circle position.
     * @param dec The circle position.
     * @param radius The circle radius.
     * @return An Iterable set of HTM triangle IDs.
     * @throws IndexerException
     * 
     */
    @RequestMapping(value=CIRCLE_PATH, params={PARAM_RA, PARAM_DEC, PARAM_RADIUS}, method=RequestMethod.POST, produces=JSON_MIME)
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
