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

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import ac.uk.roe.wfau.phymatopus.matcher.Matcher;
import edu.jhu.htm.core.HTMException;
import lombok.extern.slf4j.Slf4j;


/**
 * 
 * 
 */
@Slf4j
@Controller
@RequestMapping("/matcher")
public class MatcherService
    {

    /**
     * HTTP content type for JSON.
     * 
     */
    public static final String JSON_MIME = MediaType.APPLICATION_JSON_VALUE ;

    /**
     * HTTP content type for URL encoded form fields.
     * 
     */
    public static final String FORM_MIME = MediaType.APPLICATION_FORM_URLENCODED_VALUE;

    /**
     * MVC property for the matcher 'ra' parameter.
     *
     */
    public static final String MATCH_PARAM_RA = "phymatopus.matcher.ra" ;

    /**
     * MVC property for the matcher 'dec' parameter.
     *
     */
    public static final String MATCH_PARAM_DEC = "phymatopus.matcher.dec" ;

    /**
     * MVC property for the matcher 'radius' parameter.
     *
     */
    public static final String MATCH_PARAM_RADIUS = "phymatopus.matcher.radius" ;

    @SuppressWarnings("serial")
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class HtmMatcherException
    extends Exception
        {
        public HtmMatcherException(Exception ouch)
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
        log.debug("MatcherService() - constructor");
        this.matcher = new Matcher();
        }

    /**
     * Our matcher.
     * 
     */
    final Matcher matcher ;
    
    @RequestMapping(method=RequestMethod.POST, produces=JSON_MIME)
    public ResponseEntity<Iterable<Long>> match(

        @RequestParam(value=MATCH_PARAM_RA, required=true)
        final Double ra,
        @RequestParam(value=MATCH_PARAM_DEC, required=true)
        final Double dec,
        @RequestParam(value=MATCH_PARAM_RADIUS, required=true)
        final Double radius
        
        ) throws HtmMatcherException
        {
        try {
            Iterable<Long> iter;
            iter = matcher.cone(ra, dec, radius);
            return new ResponseEntity<Iterable<Long>>(
                iter,
                HttpStatus.OK
                );
            }
        catch (HTMException ouch)
            {
            log.debug("HTMException while processing match");
            log.debug("  Message [{}]", ouch.getMessage());
            throw new HtmMatcherException(
                ouch
                );
            }
        }
    }
