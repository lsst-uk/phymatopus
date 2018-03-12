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

package uk.ac.roe.wfau.phymatopus.htmsql.match;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import edu.jhu.htm.core.HTMException;
import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.htmsql.index.Indexer;
import uk.ac.roe.wfau.phymatopus.htmsql.index.IndexerService.IndexerException;

/**
 * Matcher service that looks up HTM triangle IDs.
 * 
 */
@Slf4j
@Controller
@RequestMapping(MatcherModel.MATCH_PATH)
public class MatcherService
implements MatcherModel
    {


    @SuppressWarnings("serial")
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public static class MatcherException
    extends Exception
        {
        public MatcherException(Exception ouch)
            {
            super(ouch);
            }
        }
    
    /**
     * Public constructor.
     *   
     */
    public MatcherService()
        {
        log.debug("IndexService() - constructor");
        }

    /**
     * Our indexer.
     * 
     */
    @Autowired
    private Indexer indexer ;

    /**
     * Our Matcher.
     * 
     */
    @Autowired
    private Matcher matcher ;

    /**
     * List the sources in a HTM triangle.
     * @param htmid The HTM triangle ID.
     * @return The list of sources.
     * @throws MatcherException
     * 
     */
    @RequestMapping(params={PARAM_HTMID}, method=RequestMethod.POST, produces=JSON_MIME)
    public ResponseEntity<Iterable<SourceBean>> match(
        @RequestParam(value=PARAM_HTMID, required=true)
        final Long htmid
        ) throws MatcherException
        {
        try {
            return new ResponseEntity<Iterable<SourceBean>>(
                matcher.match(
                    htmid
                    ),
                HttpStatus.OK
                );
            }
        catch (SQLException ouch)
            {
            log.debug("HTMException while processing point");
            log.debug("  Message [{}]", ouch.getMessage());
            throw new MatcherException(
                ouch
                );
            }
        }

    /**
     * Find the sources in a circle.
     * @param circle The circle position and radius.
     * @return A list of sources in the circle.
     * @throws IndexerException
     * 
     */
    @RequestMapping(params={PARAM_CIRCLE}, method=RequestMethod.POST, produces=JSON_MIME)
    public ResponseEntity<Iterable<SourceBean>> circle(
        @RequestParam(value=PARAM_CIRCLE, required=true)
        final Double[] circle
        ) throws IndexerException, MatcherException
        {
        return this.circle(
            circle[0],
            circle[1],
            circle[2]
            );
        }
    
    /**
     * Find the sources in a circle.
     * @param point  The circle position.
     * @param radius The circle  radius.
     * @return A list of sources in the circle.
     * @throws IndexerException
     * 
     */
    @RequestMapping(params={PARAM_POINT, PARAM_RADIUS}, method=RequestMethod.POST, produces=JSON_MIME)
    public ResponseEntity<Iterable<SourceBean>> circle(
        @RequestParam(value=PARAM_POINT, required=true)
        final Double[] point,
        @RequestParam(value=PARAM_RADIUS, required=true)
        final Double radius
        ) throws IndexerException, MatcherException
        {
        return this.circle(
            point[0],
            point[1],
            radius
            );
        }
    
    /**
     * Find the sources in a circle.
     * @param ra  The circle position.
     * @param dec The circle position.
     * @param radius The circle radius.
     * @return A list of sources in the circle.
     * @throws IndexerException
     * 
     */
    @RequestMapping(params={PARAM_RA, PARAM_DEC, PARAM_RADIUS}, method=RequestMethod.POST, produces=JSON_MIME)
    public ResponseEntity<Iterable<SourceBean>> circle(
        @RequestParam(value=PARAM_RA, required=true)
        final Double ra,
        @RequestParam(value=PARAM_DEC, required=true)
        final Double dec,
        @RequestParam(value=PARAM_RADIUS, required=true)
        final Double radius
        ) throws IndexerException, MatcherException
        {
        try {
            return new ResponseEntity<Iterable<SourceBean>>(
                matcher.match(
                    indexer.circle(
                        ra,
                        dec,
                        radius
                        )
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
        catch (SQLException ouch)
            {
            log.debug("SQLException while processing point");
            log.debug("  Message [{}]", ouch.getMessage());
            throw new MatcherException(
                ouch
                );
            }
        }
    }
