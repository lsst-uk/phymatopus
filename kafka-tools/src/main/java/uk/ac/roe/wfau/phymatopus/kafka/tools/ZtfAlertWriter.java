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

package uk.ac.roe.wfau.phymatopus.kafka.tools;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.avro.Schema;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.specific.SpecificData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlert;

/**
 * First attempt at an alert writer.
 * 
 */
@Slf4j
public class ZtfAlertWriter
extends BaseClient
    {

    /**
     * Public constructor.
     * 
     */
    public ZtfAlertWriter(final Configuration config)
        {
        super(
            config
            );
        }

    /**
     * Public interface for a writer configuration.
     * 
     */
    public static interface Configuration extends BaseReader.Configuration
        {
        }

    /**
     * Configuration implementation.
     * 
     */
    public static class ConfigurationBean
    implements Configuration
        {
        public ConfigurationBean(final String servers, final String topic, final String group)
            {
            this.servers = servers;
            this.topic   = topic;
            this.group   = group;
            }

        private String servers;
        @Override
        public String getServers()
            {
            return this.servers;
            }

        private String topic;
        @Override
        public String getTopic()
            {
            return this.topic;
            }

        private String group;
        @Override
        public String getGroup()
            {
            return this.group;
            }
        }
    
    /**
     * Our {@link Producer}. 
     * 
     */
    private Producer<Long, byte[]> producer;

    /**
     * Initialise our {@link Producer}. 
     * 
     */
    protected void init()
        {
        Properties properties = new Properties();
        properties.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            this.config.getServers()
            );
        properties.put(
            ProducerConfig.CLIENT_ID_CONFIG,
            "AvroExampleProducer"
            );
        this.producer = new KafkaProducer<Long, byte[]>(
            properties,
            new LongSerializer(),
            new ByteArraySerializer()
            );
        }

    /**
     * Count of the records sent by this writer.
     *
     */
    private long count ;

    
    SpecificData model = new SpecificData();

    Schema schema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"alert\",\"namespace\":\"ztf\",\"doc\":\"avro alert schema for ZTF (www.ztf.caltech.edu)\",\"fields\":[{\"name\":\"schemavsn\",\"type\":\"string\",\"doc\":\"schema version used\"},{\"name\":\"publisher\",\"type\":\"string\",\"doc\":\"origin of alert packet\"},{\"name\":\"objectId\",\"type\":\"string\",\"doc\":\"object identifier or name\"},{\"name\":\"candid\",\"type\":\"long\"},{\"name\":\"candidate\",\"type\":{\"type\":\"record\",\"name\":\"candidate\",\"doc\":\"avro alert schema\",\"fields\":[{\"name\":\"jd\",\"type\":\"double\",\"doc\":\"Observation Julian date at start of exposure [days]\"},{\"name\":\"fid\",\"type\":\"int\",\"doc\":\"Filter ID (1=g; 2=R; 3=i)\"},{\"name\":\"pid\",\"type\":\"long\",\"doc\":\"Processing ID for science image to facilitate archive retrieval\"},{\"name\":\"diffmaglim\",\"type\":[\"float\",\"null\"],\"doc\":\"Expected 5-sigma mag limit in difference image based on global noise estimate [mag]\",\"default\":null},{\"name\":\"pdiffimfilename\",\"type\":[\"string\",\"null\"],\"doc\":\"filename of positive (sci minus ref) difference image\",\"default\":null},{\"name\":\"programpi\",\"type\":[\"string\",\"null\"],\"doc\":\"Principal investigator attached to program ID\",\"default\":null},{\"name\":\"programid\",\"type\":\"int\",\"doc\":\"Program ID: encodes either public, collab, or caltech mode\"},{\"name\":\"candid\",\"type\":\"long\",\"doc\":\"Candidate ID from operations DB\"},{\"name\":\"isdiffpos\",\"type\":\"string\",\"doc\":\"t or 1 => candidate is from positive (sci minus ref) subtraction; f or 0 => candidate is from negative (ref minus sci) subtraction\"},{\"name\":\"tblid\",\"type\":[\"long\",\"null\"],\"doc\":\"Internal pipeline table extraction ID\",\"default\":null},{\"name\":\"nid\",\"type\":[\"int\",\"null\"],\"doc\":\"Night ID\",\"default\":null},{\"name\":\"rcid\",\"type\":[\"int\",\"null\"],\"doc\":\"Readout channel ID [00 .. 63]\",\"default\":null},{\"name\":\"field\",\"type\":[\"int\",\"null\"],\"doc\":\"ZTF field ID\",\"default\":null},{\"name\":\"xpos\",\"type\":[\"float\",\"null\"],\"doc\":\"x-image position of candidate [pixels]\",\"default\":null},{\"name\":\"ypos\",\"type\":[\"float\",\"null\"],\"doc\":\"y-image position of candidate [pixels]\",\"default\":null},{\"name\":\"ra\",\"type\":\"double\",\"doc\":\"Right Ascension of candidate; J2000 [deg]\"},{\"name\":\"dec\",\"type\":\"double\",\"doc\":\"Declination of candidate; J2000 [deg]\"},{\"name\":\"magpsf\",\"type\":\"float\",\"doc\":\"Magnitude from PSF-fit photometry [mag]\"},{\"name\":\"sigmapsf\",\"type\":\"float\",\"doc\":\"1-sigma uncertainty in magpsf [mag]\"},{\"name\":\"chipsf\",\"type\":[\"float\",\"null\"],\"doc\":\"Reduced chi-square for PSF-fit\",\"default\":null},{\"name\":\"magap\",\"type\":[\"float\",\"null\"],\"doc\":\"Aperture mag using 14 pixel diameter aperture [mag]\",\"default\":null},{\"name\":\"sigmagap\",\"type\":[\"float\",\"null\"],\"doc\":\"1-sigma uncertainty in magap [mag]\",\"default\":null},{\"name\":\"distnr\",\"type\":[\"float\",\"null\"],\"doc\":\"distance to nearest source in reference image PSF-catalog [pixels]\",\"default\":null},{\"name\":\"magnr\",\"type\":[\"float\",\"null\"],\"doc\":\"magnitude of nearest source in reference image PSF-catalog [mag]\",\"default\":null},{\"name\":\"sigmagnr\",\"type\":[\"float\",\"null\"],\"doc\":\"1-sigma uncertainty in magnr [mag]\",\"default\":null},{\"name\":\"chinr\",\"type\":[\"float\",\"null\"],\"doc\":\"DAOPhot chi parameter of nearest source in reference image PSF-catalog\",\"default\":null},{\"name\":\"sharpnr\",\"type\":[\"float\",\"null\"],\"doc\":\"DAOPhot sharp parameter of nearest source in reference image PSF-catalog\",\"default\":null},{\"name\":\"sky\",\"type\":[\"float\",\"null\"],\"doc\":\"Local sky background estimate [DN]\",\"default\":null},{\"name\":\"magdiff\",\"type\":[\"float\",\"null\"],\"doc\":\"Difference: magap - magpsf [mag]\",\"default\":null},{\"name\":\"fwhm\",\"type\":[\"float\",\"null\"],\"doc\":\"Full Width Half Max assuming a Gaussian core, from SExtractor [pixels]\",\"default\":null},{\"name\":\"classtar\",\"type\":[\"float\",\"null\"],\"doc\":\"Star/Galaxy classification score from SExtractor\",\"default\":null},{\"name\":\"mindtoedge\",\"type\":[\"float\",\"null\"],\"doc\":\"Distance to nearest edge in image [pixels]\",\"default\":null},{\"name\":\"magfromlim\",\"type\":[\"float\",\"null\"],\"doc\":\"Difference: diffmaglim - magap [mag]\",\"default\":null},{\"name\":\"seeratio\",\"type\":[\"float\",\"null\"],\"doc\":\"Ratio: difffwhm / fwhm\",\"default\":null},{\"name\":\"aimage\",\"type\":[\"float\",\"null\"],\"doc\":\"Windowed profile RMS afloat major axis from SExtractor [pixels]\",\"default\":null},{\"name\":\"bimage\",\"type\":[\"float\",\"null\"],\"doc\":\"Windowed profile RMS afloat minor axis from SExtractor [pixels]\",\"default\":null},{\"name\":\"aimagerat\",\"type\":[\"float\",\"null\"],\"doc\":\"Ratio: aimage / fwhm\",\"default\":null},{\"name\":\"bimagerat\",\"type\":[\"float\",\"null\"],\"doc\":\"Ratio: bimage / fwhm\",\"default\":null},{\"name\":\"elong\",\"type\":[\"float\",\"null\"],\"doc\":\"Ratio: aimage / bimage\",\"default\":null},{\"name\":\"nneg\",\"type\":[\"int\",\"null\"],\"doc\":\"number of negative pixels in a 5 x 5 pixel stamp\",\"default\":null},{\"name\":\"nbad\",\"type\":[\"int\",\"null\"],\"doc\":\"number of prior-tagged bad pixels in a 5 x 5 pixel stamp\",\"default\":null},{\"name\":\"rb\",\"type\":[\"float\",\"null\"],\"doc\":\"RealBogus quality score; range is 0 to 1 where closer to 1 is more reliable\",\"default\":null},{\"name\":\"ssdistnr\",\"type\":[\"float\",\"null\"],\"doc\":\"distance to nearest known solar system object if exists within 30 arcsec [arcsec]\",\"default\":null},{\"name\":\"ssmagnr\",\"type\":[\"float\",\"null\"],\"doc\":\"magnitude of nearest known solar system object if exists within 30 arcsec (usually V-band from MPC archive) [mag]\",\"default\":null},{\"name\":\"ssnamenr\",\"type\":[\"string\",\"null\"],\"doc\":\"name of nearest known solar system object if exists within 30 arcsec (from MPC archive)\",\"default\":null},{\"name\":\"sumrat\",\"type\":[\"float\",\"null\"],\"doc\":\"Ratio: sum(pixels) / sum(|pixels|) in a 5 x 5 pixel stamp where stamp is first median-filtered to mitigate outliers\",\"default\":null},{\"name\":\"magapbig\",\"type\":[\"float\",\"null\"],\"doc\":\"Aperture mag using 18 pixel diameter aperture [mag]\",\"default\":null},{\"name\":\"sigmagapbig\",\"type\":[\"float\",\"null\"],\"doc\":\"1-sigma uncertainty in magapbig [mag]\",\"default\":null},{\"name\":\"ranr\",\"type\":\"double\",\"doc\":\"Right Ascension of nearest source in reference image PSF-catalog; J2000 [deg]\"},{\"name\":\"decnr\",\"type\":\"double\",\"doc\":\"Declination of nearest source in reference image PSF-catalog; J2000 [deg]\"},{\"name\":\"sgmag1\",\"type\":[\"float\",\"null\"],\"doc\":\"g-band PSF-fit magnitude of closest source from PS1 catalog; if exists within 30 arcsec [mag]\",\"default\":null},{\"name\":\"srmag1\",\"type\":[\"float\",\"null\"],\"doc\":\"r-band PSF-fit magnitude of closest source from PS1 catalog; if exists within 30 arcsec [mag]\",\"default\":null},{\"name\":\"simag1\",\"type\":[\"float\",\"null\"],\"doc\":\"i-band PSF-fit magnitude of closest source from PS1 catalog; if exists within 30 arcsec [mag]\",\"default\":null},{\"name\":\"szmag1\",\"type\":[\"float\",\"null\"],\"doc\":\"z-band PSF-fit magnitude of closest source from PS1 catalog; if exists within 30 arcsec [mag]\",\"default\":null},{\"name\":\"sgscore1\",\"type\":[\"float\",\"null\"],\"doc\":\"Star/Galaxy score of closest source from PS1 catalog; if exists within 30 arcsec: 0 <= sgscore <= 1 where closer to 1 implies higher likelihood of being a star\",\"default\":null},{\"name\":\"distpsnr1\",\"type\":[\"float\",\"null\"],\"doc\":\"Distance to closest source from PS1 catalog; if exists within 30 arcsec [arcsec]\",\"default\":null},{\"name\":\"ndethist\",\"type\":\"int\",\"doc\":\"Number of spatially-coincident detections falling within 1.5 arcsec going back to beginning of survey; only detections that fell on the same field and readout-channel ID where the input candidate was observed are counted\"},{\"name\":\"ncovhist\",\"type\":\"int\",\"doc\":\"Number of times input candidate position fell on any field and readout-channel going back to beginning of survey\"},{\"name\":\"jdstarthist\",\"type\":[\"double\",\"null\"],\"doc\":\"Earliest Julian date of epoch corresponding to ndethist [days]\",\"default\":null},{\"name\":\"jdendhist\",\"type\":[\"double\",\"null\"],\"doc\":\"Latest Julian date of epoch corresponding to ndethist [days]\",\"default\":null},{\"name\":\"scorr\",\"type\":[\"double\",\"null\"],\"doc\":\"Peak-pixel signal-to-noise ratio in point source matched-filtered detection image\",\"default\":null},{\"name\":\"tooflag\",\"type\":[\"int\",\"null\"],\"doc\":\"1 => candidate is from a Target-of-Opportunity (ToO) exposure; 0 => candidate is from a non-ToO exposure\",\"default\":0},{\"name\":\"objectidps1\",\"type\":[\"long\",\"null\"],\"doc\":\"Object ID of closest source from PS1 catalog; if exists within 30 arcsec\",\"default\":null},{\"name\":\"objectidps2","\",\"type\":[\"long\",\"null\"],\"doc\":\"Object ID of second closest source from PS1 catalog; if exists within 30 arcsec\",\"default\":null},{\"name\":\"sgmag2\",\"type\":[\"float\",\"null\"],\"doc\":\"g-band PSF-fit magnitude of second closest source from PS1 catalog; if exists within 30 arcsec [mag]\",\"default\":null},{\"name\":\"srmag2\",\"type\":[\"float\",\"null\"],\"doc\":\"r-band PSF-fit magnitude of second closest source from PS1 catalog; if exists within 30 arcsec [mag]\",\"default\":null},{\"name\":\"simag2\",\"type\":[\"float\",\"null\"],\"doc\":\"i-band PSF-fit magnitude of second closest source from PS1 catalog; if exists within 30 arcsec [mag]\",\"default\":null},{\"name\":\"szmag2\",\"type\":[\"float\",\"null\"],\"doc\":\"z-band PSF-fit magnitude of second closest source from PS1 catalog; if exists within 30 arcsec [mag]\",\"default\":null},{\"name\":\"sgscore2\",\"type\":[\"float\",\"null\"],\"doc\":\"Star/Galaxy score of second closest source from PS1 catalog; if exists within 30 arcsec: 0 <= sgscore <= 1 where closer to 1 implies higher likelihood of being a star\",\"default\":null},{\"name\":\"distpsnr2\",\"type\":[\"float\",\"null\"],\"doc\":\"Distance to second closest source from PS1 catalog; if exists within 30 arcsec [arcsec]\",\"default\":null},{\"name\":\"objectidps3\",\"type\":[\"long\",\"null\"],\"doc\":\"Object ID of third closest source from PS1 catalog; if exists within 30 arcsec\",\"default\":null},{\"name\":\"sgmag3\",\"type\":[\"float\",\"null\"],\"doc\":\"g-band PSF-fit magnitude of third closest source from PS1 catalog; if exists within 30 arcsec [mag]\",\"default\":null},{\"name\":\"srmag3\",\"type\":[\"float\",\"null\"],\"doc\":\"r-band PSF-fit magnitude of third closest source from PS1 catalog; if exists within 30 arcsec [mag]\",\"default\":null},{\"name\":\"simag3\",\"type\":[\"float\",\"null\"],\"doc\":\"i-band PSF-fit magnitude of third closest source from PS1 catalog; if exists within 30 arcsec [mag]\",\"default\":null},{\"name\":\"szmag3\",\"type\":[\"float\",\"null\"],\"doc\":\"z-band PSF-fit magnitude of third closest source from PS1 catalog; if exists within 30 arcsec [mag]\",\"default\":null},{\"name\":\"sgscore3\",\"type\":[\"float\",\"null\"],\"doc\":\"Star/Galaxy score of third closest source from PS1 catalog; if exists within 30 arcsec: 0 <= sgscore <= 1 where closer to 1 implies higher likelihood of being a star\",\"default\":null},{\"name\":\"distpsnr3\",\"type\":[\"float\",\"null\"],\"doc\":\"Distance to third closest source from PS1 catalog; if exists within 30 arcsec [arcsec]\",\"default\":null},{\"name\":\"nmtchps\",\"type\":\"int\",\"doc\":\"Number of source matches from PS1 catalog falling within 30 arcsec\"},{\"name\":\"rfid\",\"type\":\"long\",\"doc\":\"Processing ID for reference image to facilitate archive retrieval\"},{\"name\":\"jdstartref\",\"type\":\"double\",\"doc\":\"Observation Julian date of earliest exposure used to generate reference image [days]\"},{\"name\":\"jdendref\",\"type\":\"double\",\"doc\":\"Observation Julian date of latest exposure used to generate reference image [days]\"},{\"name\":\"nframesref\",\"type\":\"int\",\"doc\":\"Number of frames (epochal images) used to generate reference image\"},{\"name\":\"rbversion\",\"type\":\"string\",\"doc\":\"version of RealBogus model/classifier used to assign rb quality score\"},{\"name\":\"dsnrms\",\"type\":[\"float\",\"null\"],\"doc\":\"Ratio: D/stddev(D) on event position where D = difference image\",\"default\":null},{\"name\":\"ssnrms\",\"type\":[\"float\",\"null\"],\"doc\":\"Ratio: S/stddev(S) on event position where S = image of convolution: D (x) PSF(D)\",\"default\":null},{\"name\":\"dsdiff\",\"type\":[\"float\",\"null\"],\"doc\":\"Difference of statistics: dsnrms - ssnrms\",\"default\":null},{\"name\":\"magzpsci\",\"type\":[\"float\",\"null\"],\"doc\":\"Magnitude zero point for photometry estimates [mag]\",\"default\":null},{\"name\":\"magzpsciunc\",\"type\":[\"float\",\"null\"],\"doc\":\"Magnitude zero point uncertainty (in magzpsci) [mag]\",\"default\":null},{\"name\":\"magzpscirms\",\"type\":[\"float\",\"null\"],\"doc\":\"RMS (deviation from average) in all differences between instrumental photometry and matched photometric calibrators from science image processing [mag]\",\"default\":null},{\"name\":\"nmatches\",\"type\":\"int\",\"doc\":\"Number of PS1 photometric calibrators used to calibrate science image from science image processing\"},{\"name\":\"clrcoeff\",\"type\":[\"float\",\"null\"],\"doc\":\"Color coefficient from linear fit from photometric calibration of science image\",\"default\":null},{\"name\":\"clrcounc\",\"type\":[\"float\",\"null\"],\"doc\":\"Color coefficient uncertainty from linear fit (corresponding to clrcoeff)\",\"default\":null},{\"name\":\"zpclrcov\",\"type\":[\"float\",\"null\"],\"doc\":\"Covariance in magzpsci and clrcoeff from science image processing [mag^2]\",\"default\":null},{\"name\":\"zpmed\",\"type\":[\"float\",\"null\"],\"doc\":\"Magnitude zero point from median of all differences between instrumental photometry and matched photometric calibrators from science image processing [mag]\",\"default\":null},{\"name\":\"clrmed\",\"type\":[\"float\",\"null\"],\"doc\":\"Median color of all PS1 photometric calibrators used from science image processing [mag]: for filter (fid) = 1, 2, 3, PS1 color used = g-r, g-r, r-i respectively\",\"default\":null},{\"name\":\"clrrms\",\"type\":[\"float\",\"null\"],\"doc\":\"RMS color (deviation from average) of all PS1 photometric calibrators used from science image processing [mag]\",\"default\":null},{\"name\":\"neargaia\",\"type\":[\"float\",\"null\"],\"doc\":\"Distance to closest source from Gaia DR1 catalog irrespective of magnitude; if exists within 90 arcsec [arcsec]\",\"default\":null},{\"name\":\"neargaiabright\",\"type\":[\"float\",\"null\"],\"doc\":\"Distance to closest source from Gaia DR1 catalog brighter than magnitude 14; if exists within 90 arcsec [arcsec]\",\"default\":null},{\"name\":\"maggaia\",\"type\":[\"float\",\"null\"],\"doc\":\"Gaia (G-band) magnitude of closest source from Gaia DR1 catalog irrespective of magnitude; if exists within 90 arcsec [mag]\",\"default\":null},{\"name\":\"maggaiabright\",\"type\":[\"float\",\"null\"],\"doc\":\"Gaia (G-band) magnitude of closest source from Gaia DR1 catalog brighter than magnitude 14; if exists within 90 arcsec [mag]\",\"default\":null}],\"version\":\"3.0\"}},{\"name\":\"prv_candidates\",\"type\":[{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"prv_candidate\",\"doc\":\"avro alert schema\",\"fields\":[{\"name\":\"jd\",\"type\":\"double\",\"doc\":\"Observation Julian date at start of exposure [days]\"},{\"name\":\"fid\",\"type\":\"int\",\"doc\":\"Filter ID (1=g; 2=R; 3=i)\"},{\"name\":\"pid\",\"type\":\"long\",\"doc\":\"Processing ID for image\"},{\"name\":\"diffmaglim\",\"type\":[\"float\",\"null\"],\"doc\":\"Expected 5-sigma mag limit in difference image based on global noise estimate [mag]\",\"default\":null},{\"name\":\"pdiffimfilename\",\"type\":[\"string\",\"null\"],\"doc\":\"filename of positive (sci minus ref) difference image\",\"default\":null},{\"name\":\"programpi\",\"type\":[\"string\",\"null\"],\"doc\":\"Principal investigator attached to program ID\",\"default\":null},{\"name\":\"programid\",\"type\":\"int\",\"doc\":\"Program ID: encodes either public, collab, or caltech mode\"},{\"name\":\"candid\",\"type\":[\"long\",\"null\"],\"doc\":\"Candidate ID from operations DB\"},{\"name\":\"isdiffpos\",\"type\":[\"string\",\"null\"],\"doc\":\"t or 1 => candidate is from positive (sci minus ref) subtraction; f or 0 => candidate is from negative (ref minus sci) subtraction\"},{\"name\":\"tblid\",\"type\":[\"long\",\"null\"],\"doc\":\"Internal pipeline table extraction ID\",\"default\":null},{\"name\":\"nid\",\"type\":[\"int\",\"null\"],\"doc\":\"Night ID\",\"default\":null},{\"name\":\"rcid\",\"type\":[\"int\",\"null\"],\"doc\":\"Readout channel ID [00 .. 63]\",\"default\":null},{\"name\":\"field\",\"type\":[\"int\",\"null\"],\"doc\":\"ZTF field ID\",\"default\":null},{\"name\":\"xpos\",\"type\":[\"float\",\"null\"],\"doc\":\"x-image position of candidate [pixels]\",\"default\":null},{\"name\":\"ypos\",\"type\":[\"float\",\"null\"],\"doc\":\"y-image position of candidate [pixels]\",\"default\":null},{\"name\":\"ra\",\"type\":[\"double\",\"null\"],\"doc\":\"Right Ascension of candidate; J2000 [deg]\"},{\"name\":\"dec\",\"type\":[\"double\",\"null\"],\"doc\":\"Declination of candidate; J2000 [deg]\"},{\"name\":\"magpsf\",\"type\":[\"float\",\"null\"],\"doc\":\"Magnitude from PSF-fit photometry [mag]\"},{\"name\":\"sigmapsf\",\"type\":[\"float\",\"null\"],\"doc\":\"1-sigma uncertainty in magpsf [mag]\"},{\"name\":\"chipsf\",\"type\":[\"float\",\"null\"],\"doc\":\"Reduced chi-square for PSF-fit\",\"default\":null},{\"name\":\"magap\",\"type\":[\"float\",\"null\"],\"doc\":\"Aperture mag using 14 pixel diameter aperture [mag]\",\"default\":null},{\"name\":\"sigmagap\",\"type\":[\"float\",\"null\"],\"doc\":\"1-sigma uncertainty in magap [mag]\",\"defaul","t\":null},{\"name\":\"distnr\",\"type\":[\"float\",\"null\"],\"doc\":\"distance to nearest source in reference image PSF-catalog [pixels]\",\"default\":null},{\"name\":\"magnr\",\"type\":[\"float\",\"null\"],\"doc\":\"magnitude of nearest source in reference image PSF-catalog [mag]\",\"default\":null},{\"name\":\"sigmagnr\",\"type\":[\"float\",\"null\"],\"doc\":\"1-sigma uncertainty in magnr [mag]\",\"default\":null},{\"name\":\"chinr\",\"type\":[\"float\",\"null\"],\"doc\":\"DAOPhot chi parameter of nearest source in reference image PSF-catalog\",\"default\":null},{\"name\":\"sharpnr\",\"type\":[\"float\",\"null\"],\"doc\":\"DAOPhot sharp parameter of nearest source in reference image PSF-catalog\",\"default\":null},{\"name\":\"sky\",\"type\":[\"float\",\"null\"],\"doc\":\"Local sky background estimate [DN]\",\"default\":null},{\"name\":\"magdiff\",\"type\":[\"float\",\"null\"],\"doc\":\"Difference: magap - magpsf [mag]\",\"default\":null},{\"name\":\"fwhm\",\"type\":[\"float\",\"null\"],\"doc\":\"Full Width Half Max assuming a Gaussian core, from SExtractor [pixels]\",\"default\":null},{\"name\":\"classtar\",\"type\":[\"float\",\"null\"],\"doc\":\"Star/Galaxy classification score from SExtractor\",\"default\":null},{\"name\":\"mindtoedge\",\"type\":[\"float\",\"null\"],\"doc\":\"Distance to nearest edge in image [pixels]\",\"default\":null},{\"name\":\"magfromlim\",\"type\":[\"float\",\"null\"],\"doc\":\"Difference: diffmaglim - magap [mag]\",\"default\":null},{\"name\":\"seeratio\",\"type\":[\"float\",\"null\"],\"doc\":\"Ratio: difffwhm / fwhm\",\"default\":null},{\"name\":\"aimage\",\"type\":[\"float\",\"null\"],\"doc\":\"Windowed profile RMS afloat major axis from SExtractor [pixels]\",\"default\":null},{\"name\":\"bimage\",\"type\":[\"float\",\"null\"],\"doc\":\"Windowed profile RMS afloat minor axis from SExtractor [pixels]\",\"default\":null},{\"name\":\"aimagerat\",\"type\":[\"float\",\"null\"],\"doc\":\"Ratio: aimage / fwhm\",\"default\":null},{\"name\":\"bimagerat\",\"type\":[\"float\",\"null\"],\"doc\":\"Ratio: bimage / fwhm\",\"default\":null},{\"name\":\"elong\",\"type\":[\"float\",\"null\"],\"doc\":\"Ratio: aimage / bimage\",\"default\":null},{\"name\":\"nneg\",\"type\":[\"int\",\"null\"],\"doc\":\"number of negative pixels in a 5 x 5 pixel stamp\",\"default\":null},{\"name\":\"nbad\",\"type\":[\"int\",\"null\"],\"doc\":\"number of prior-tagged bad pixels in a 5 x 5 pixel stamp\",\"default\":null},{\"name\":\"rb\",\"type\":[\"float\",\"null\"],\"doc\":\"RealBogus quality score; range is 0 to 1 where closer to 1 is more reliable\",\"default\":null},{\"name\":\"ssdistnr\",\"type\":[\"float\",\"null\"],\"doc\":\"distance to nearest known solar system object if exists within 30 arcsec [arcsec]\",\"default\":null},{\"name\":\"ssmagnr\",\"type\":[\"float\",\"null\"],\"doc\":\"magnitude of nearest known solar system object if exists within 30 arcsec (usually V-band from MPC archive) [mag]\",\"default\":null},{\"name\":\"ssnamenr\",\"type\":[\"string\",\"null\"],\"doc\":\"name of nearest known solar system object if exists within 30 arcsec (from MPC archive)\",\"default\":null},{\"name\":\"sumrat\",\"type\":[\"float\",\"null\"],\"doc\":\"Ratio: sum(pixels) / sum(|pixels|) in a 5 x 5 pixel stamp where stamp is first median-filtered to mitigate outliers\",\"default\":null},{\"name\":\"magapbig\",\"type\":[\"float\",\"null\"],\"doc\":\"Aperture mag using 18 pixel diameter aperture [mag]\",\"default\":null},{\"name\":\"sigmagapbig\",\"type\":[\"float\",\"null\"],\"doc\":\"1-sigma uncertainty in magapbig [mag]\",\"default\":null},{\"name\":\"ranr\",\"type\":[\"double\",\"null\"],\"doc\":\"Right Ascension of nearest source in reference image PSF-catalog; J2000 [deg]\"},{\"name\":\"decnr\",\"type\":[\"double\",\"null\"],\"doc\":\"Declination of nearest source in reference image PSF-catalog; J2000 [deg]\"},{\"name\":\"scorr\",\"type\":[\"double\",\"null\"],\"doc\":\"Peak-pixel signal-to-noise ratio in point source matched-filtered detection image\",\"default\":null},{\"name\":\"rbversion\",\"type\":\"string\",\"doc\":\"version of RealBogus model/classifier used to assign rb quality score\"}],\"version\":\"3.0\"}},\"null\"],\"default\":null},{\"name\":\"cutoutScience\",\"type\":[{\"type\":\"record\",\"name\":\"cutout\",\"doc\":\"avro alert schema\",\"fields\":[{\"name\":\"fileName\",\"type\":\"string\"},{\"name\":\"stampData\",\"type\":\"bytes\",\"doc\":\"fits.gz\"}],\"version\":\"3.0\"},\"null\"],\"default\":null},{\"name\":\"cutoutTemplate\",\"type\":[\"cutout\",\"null\"],\"default\":null},{\"name\":\"cutoutDifference\",\"type\":[\"cutout\",\"null\"],\"default\":null}],\"version\":\"3.0\"}");

    BinaryMessageEncoder<ZtfAlert> encoder = new BinaryMessageEncoder<ZtfAlert>(model, schema);

    /**
     * Write a an alert to the stream. 
     * 
     */
    public void write(final ZtfAlert alert)
        {
        log.debug("Starting write alert");

        
        ByteBuffer buffer = null;
        try {
            buffer = encoder.encode(alert);
            }
        catch (IOException ouch)
            {
            log.error("IOException encoding alert [{}]", ouch.getMessage());
            log.error("IOException", ouch);
            }

        log.debug("Getting the buffer byte array");
        byte[] bytes = buffer.array();
        
        DebugFormatter debugger = new DebugFormatter();
        debugger.asciiBytes(bytes);
        
        log.debug("Sending the bytes");
        write(bytes);
        }
    
    /**
     * Write a byte array to the stream. 
     * 
     */
    public void write(final byte[] bytes)
        {
        log.debug("Starting write byte[]");
        DebugFormatter debug = new DebugFormatter();
        debug.asciiBytes(bytes);
        
        final ProducerRecord<Long, byte[] > record = new ProducerRecord<Long, byte[] >(
            this.config.getTopic(),
            count,
            bytes
            );
        try {
            RecordMetadata metadata = producer.send(
                record
                ).get();
            log.debug("Response [{}][{}]", metadata.partition(), metadata.offset());
            }
        catch (InterruptedException ouch)
            {
            log.error("InterruptedException while writing to stream [{}]", ouch);
            throw new RuntimeException(
                ouch
                );
            }
        catch (ExecutionException ouch)
            {
            log.error("ExecutionException while writing to stream [{}]", ouch);
            throw new RuntimeException(
                ouch
                );
            }
        }

    /**
     * Flush our stream.
     * 
     */
    public void flush()
        {
        if (null != producer)
            {
            producer.flush();
            }
        }

    /**
     * Flush the stream and close our connection
     * 
     */
    public void close()
        {
        if (null != producer)
            {
            producer.flush();
            }
        if (null != producer)
            {
            producer.close();
            }
        }
    }
