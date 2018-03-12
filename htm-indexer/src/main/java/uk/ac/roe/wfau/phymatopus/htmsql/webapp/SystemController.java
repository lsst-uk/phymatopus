/*
 *  Copyright (C) 2012 Royal Observatory, University of Edinburgh, UK
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
package uk.ac.roe.wfau.phymatopus.htmsql.webapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.roe.wfau.phymatopus.htmsql.index.IndexerModel;

/**
 *
 *
 */
@Controller
@RequestMapping(SystemController.SERVICE_PATH)
public class SystemController
implements IndexerModel
    {

    public static final String SERVICE_PATH = "/system" ;

    public static final String SYSTEM_INFO_PATH = "info" ;
    public static final String SYSTEM_INFO_VIEW = "system/system-info" ;
    
    /**
     * Public constructor.
     *
     */
    public SystemController()
        {
        }

    /**
     * {@link RequestMethod#GET} request for the system info.
     * <br/>Request path : [{@value #SYSTEM_INFO_PATH}}]
     * <br/>Content type : [{@value #JSON_MIME}]
     * @return The name of the JSP page, [{@value #SYSTEM_INFO_VIEW}].
     * 
     */
    @RequestMapping(value=SYSTEM_INFO_PATH, method=RequestMethod.GET, produces=JSON_MIME)
    public String vosi(
        ){
        return SYSTEM_INFO_VIEW ;
        }
    }
