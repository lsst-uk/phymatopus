--
-- <meta:header>
--   <meta:licence>
--     Copyright (c) 2018, ROE (http://www.roe.ac.uk/)
--
--     This information is free software: you can redistribute it and/or modify
--     it under the terms of the GNU General Public License as published by
--     the Free Software Foundation, either version 3 of the License, or
--     (at your option) any later version.
--
--     This information is distributed in the hope that it will be useful,
--     but WITHOUT ANY WARRANTY; without even the implied warranty of
--     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--     GNU General Public License for more details.
--  
--     You should have received a copy of the GNU General Public License
--     along with this program.  If not, see <http://www.gnu.org/licenses/>.
--   </meta:licence>
-- </meta:header>
--
--

CREATE TABLE sources
    (
    matchid  INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY,
    catalog  VARCHAR(64) NOT NULL,
    sourceid VARCHAR(64) NOT NULL,
    htmid    BIGINT  NOT NULL,
    ra       DOUBLE PRECISION NOT NULL,
    decl     DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (htmid)
    );


INSERT INTO sources (catalog, sourceid, htmid, ra, decl) VALUES ('test', '0000', 12345, 0.0,  0.0) ;
INSERT INTO sources (catalog, sourceid, htmid, ra, decl) VALUES ('test', '0001', 12345, 0.0,  0.0) ;
INSERT INTO sources (catalog, sourceid, htmid, ra, decl) VALUES ('test', '0002', 12345, 0.0,  0.0) ;
INSERT INTO sources (catalog, sourceid, htmid, ra, decl) VALUES ('test', '0003', 12345, 0.0,  0.0) ;



