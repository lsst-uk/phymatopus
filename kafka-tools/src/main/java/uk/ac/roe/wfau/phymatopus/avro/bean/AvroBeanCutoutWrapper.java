/*
 *  Copyright (C) 2019 Royal Observatory, University of Edinburgh, UK
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
package uk.ac.roe.wfau.phymatopus.avro.bean;

import java.nio.ByteBuffer;

import uk.ac.roe.wfau.phymatopus.alert.AlertCutout;

/**
 * A wrapper class for the ZTF {@link alert} bean generated from the JSON schema.  
 *
 */
public class AvroBeanCutoutWrapper
implements AlertCutout
    {
    /**
     * Our Avro alert bean.
     *
     */
    private ztf.cutout bean ;

    /**
     * Public constructor.
     * 
     */
    public AvroBeanCutoutWrapper(final ztf.cutout bean)
        {
        this.bean = bean;
        }

    @Override
    public CharSequence getFileName()
        {
        return bean.getFileName();
        }

    @Override
    public ByteBuffer getStampData()
        {
        return bean.getStampData();
        }
    }
