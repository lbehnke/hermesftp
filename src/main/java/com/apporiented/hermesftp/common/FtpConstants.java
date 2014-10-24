/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2014 Lars Behnke
 * ------------------------------------------------------------------------------
 * 
 * This file is part of Hermes FTP Server.
 * 
 * Hermes FTP Server is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Hermes FTP Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Hermes FTP Server; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * ------------------------------------------------------------------------------
 */

package com.apporiented.hermesftp.common;

/**
 * General constants of the application.
 * 
 * @author Lars Behnke
 */
public interface FtpConstants {

    /* Server option keys */

    /** The key for customized good bye message. */
    public static final String   OPT_MSG_GOODBYE           = "msg.goodbye";

    /** The key for customized welcome message. */
    public static final String   OPT_MSG_WELCOME           = "msg.welcome";

    /** The key for data buffer size. */
    public static final String   OPT_BUFFER_SIZE           = "buffer.size";

    /** The key for data maximum idle seconds until the session times out. */
    public static final String   OPT_MAX_IDLE_SECONDS      = "max.idle.seconds";

    /** The key for the flag indicating unix/windows emulation. */
    public static final String   OPT_EMULATE_UNIX          = "emulate.unix";

    /** The key for the client connection limit. */
    public static final String   OPT_MAX_CONNECTIONS       = "max.connections";

    /** The key for the default remote directory. */
    public static final String   OPT_REMOTE_DIR            = "ftp.root.dir";

    /** The key for the allowed passive ports. */
    public static final String   OPT_ALLOWED_PASSIVE_PORTS = "allowed.passive.ports";

    /** The key for the black list of ip v4 addresses. */
    public static final String   OPT_IPV4_BLACK_LIST         = "ipv4.black.list";

    /** The key for the black list of ip v4 addresses. */
    public static final String   OPT_IPV6_BLACK_LIST         = "ipv6.black.list";

    /** The key for the global maximum upload rate in KB/s. */
    public static final String   OPT_MAX_UPLOAD_RATE       = "max.upload.rate";

    /** The key for the global maximum download rate in KB/s. */
    public static final String   OPT_MAX_DOWNLOAD_RATE     = "max.download.rate";

    /** The key for the FTP port to be used (21 is default). */
    public static final String   OPT_FTP_PORT              = "ftp.port";

    /** The key for the flag indicating forced SSL. */
    public static final String   OPT_SSL_FORCE             = "ssl.force";

    /** The key for key store file. */
    public static final String   OPT_SSL_KEYSTORE_FILE     = "ssl.keystore.file";

    /** The key for keystore password. */
    public static final String   OPT_SSL_KEYSTORE_PASS     = "ssl.keystore.password";

    /** The key for a flag enabling explicit SSL. */
    public static final String   OPT_SSL_ALLOW_EXPLICIT    = "ssl.allow.explicit";

    /** The key for a flag enabling implicit SSL. */
    public static final String   OPT_SSL_ALLOW_IMPLICIT    = "ssl.allow.implicit";

    /** The key for port to be used is implicit SSL mode (default is 990). */
    public static final String   OPT_SSL_PORT_IMPLICIT     = "ssl.port.implicit";

    /** The key for supported SSL cipher suites. "*" for all supported by system. */
    public static final String   OPT_SSL_CIPHER_SUITES     = "ssl.cipher.suites";

    /** The EBCDIC character set to use. */
    public static final String   OPT_CHARSET_EBCDIC        = "charset.ebcdic";

    /** The ASCII/ANSI character set to use. */
    public static final String   OPT_CHARSET_ASCII         = "charset.ascii";

    /* Session attribute keys */

    /** Client name of a session (optionally transmitted to server). */
    public static final String   ATTR_CLIENT_NAME          = "client_name";

    /** Name of the file currently renamed. */
    public static final String   ATTR_RENAME_FILE          = "rename_file";

    /** Offset pointer of the transmitted file. */
    public static final String   ATTR_FILE_OFFSET          = "file_offset";

    /** Time stamp of the user's authentication. */
    public static final String   ATTR_LOGIN_TIME           = "login_time";

    /** Information about the authenticated user. */
    public static final String   ATTR_USER_DATA            = "user_data";

    /** Information about the groups the authenticated belongs to. */
    public static final String   ATTR_GROUP_DATA           = "group_data";

    /** Name of the file currently renamed. */
    public static final String   ATTR_SSL                  = "ssl";

    /** Data protection flag. */
    public static final String   ATTR_DATA_PROT            = "data_protection";

    /** Boolean flag that enforces that output data is encoded in UTF-8 (as required by IE). */
    public static final String   ATTR_FORCE_UTF8           = "force_utf8";

    /** Container for the restart markers. */
    public static final String   ATTR_RESTART_MARKERS      = "restart_markers";

    /* Various constants */

    /** Konfiguration of the Spring application context. */
    public static final String   DEFAULT_BEAN_RES          = "hermesftp-ctx.xml";

    /** Environment property key that points to the application's home directory. * */
    public static final String   HERMES_HOME               = "HERMES_HOME";

    /** Default key store password. */
    public static final String   DEFAULT_KEYSTORE_PASS     = "secret";

    /** Default key store resource file. */
    public static final String   DEFAULT_KEYSTORE          = "/keystore";

    /** All supported data types. */
    public static final String[] TYPE_NAMES                = new String[] {"ASCII", "EBCDIC", "BINARY"};

    /* Data types */

    /** ASCII data type. */
    public static final int      DT_ASCII                  = 0;

    /** EBCDIX data type. */
    public static final int      DT_EBCDIC                 = 1;

    /** Binary data type. */
    public static final int      DT_BINARY                 = 2;

    /* Transmission modes */

    /** Stream mode. */
    public static final int      MODE_STREAM               = 0;

    /** Block mode. */
    public static final int      MODE_BLOCK                = 1;

    /** Compressed mode. */
    public static final int      MODE_COMPRESS             = 2;

    /** Zlib mode. */
    public static final int      MODE_ZIP                  = 3;

    /* Data structure */

    /** Files based structure. */
    public static final int      STRUCT_FILE               = 0;

    /** Record based structure (Mainframes). */
    public static final int      STRUCT_RECORD             = 1;

    /* Various common constants */

    /** Default text separator. */
    public static final String   SEPARATOR                 = ",";

    /** Space. */
    public static final String   SPACE                     = " ";

    /** Wildcard. */
    public static final String   WILDCARD                  = "*";

    /** Masks a byte. */
    public static final int      BYTE_MASK                 = 0xff;

    /** Byte size. */
    public static final int      BYTE_LENGTH               = 8;

    /** Number of milliseconds in a second. */
    public static final int      MILLI                     = 1000;

    /* Permission information */

    /** No access allowed on a particular path. */
    public static final int      PRIV_NONE                 = 0;

    /** Read access allowed on a particular path. */
    public static final int      PRIV_READ                 = 1;

    /** Write access allowed on a particular path. */
    public static final int      PRIV_WRITE                = 2;

    /** Read/Write access allowed on a particular path. */
    public static final int      PRIV_READ_WRITE           = 3;

    /* Server status */

    /** Server has not been initialized. */
    public static final int      SERVER_STATUS_UNDEF       = 0;

    /** Server is being initialized. */
    public static final int      SERVER_STATUS_INIT        = 1;

    /** Server is ready to accept connections. */
    public static final int      SERVER_STATUS_READY       = 2;

    /** Server was halted. */
    public static final int      SERVER_STATUS_HALTED      = 3;

    /* Statistics */

    /** Uploaded bytes limit. */
    public static final String   STAT_BYTES_UPLOADED       = "Bytes uploaded";

    /** Uploaded file limit. */
    public static final String   STAT_FILES_UPLOADED       = "Files uploaded";

    /** Downloaded bytes limit. */
    public static final String   STAT_BYTES_DOWNLOADED     = "Bytes downloaded";

    /** Downloaded file limit. */
    public static final String   STAT_FILES_DOWNLOADED     = "Files downloaded";

    /** Download rate (KB/s) limit. */
    public static final String   STAT_DOWNLOAD_RATE        = "Download rate";

    /** Upload rate (KB/s) limit. */
    public static final String   STAT_UPLOAD_RATE          = "Upload rate";

    /* Resource identifiers */

    /** FTP response message 150. */
    public static final String   MSG150                    = "msg150";

    /** FTP response message 200. */
    public static final String   MSG200                    = "msg200";

    /** FTP response message 200 (PBSZ). */
    public static final String   MSG200_PBSZ               = "msg200_pbsz";

    /** FTP response message 200 (size). */
    public static final String   MSG200_SIZE               = "msg200_size";

    /** FTP response message 200. */
    public static final String   MSG200_NOTED              = "msg200_noted";

    /** FTP response message 202. */
    public static final String   MSG202                    = "msg202";

    /** FTP response message 200. */
    public static final String   MSG200_TYPE               = "msg200_type";

    /** FTP response message 211 header. */
    public static final String   MSG211_FEAT_HEADER        = "msg211_feat_header";

    /** FTP response message 211 entry. */
    public static final String   MSG211_FEAT_ENTRY         = "msg211_feat_entry";

    /** FTP response message 211 footer. */
    public static final String   MSG211_FEAT_FOOTER        = "msg211_feat_footer";

    /** FTP response message 211. */
    public static final String   MSG211_STAT               = "msg211_stat";

    /** FTP response message 213 (file size). */
    public static final String   MSG213_SIZE               = "msg213_size";

    /** FTP response message 213 (file size). */
    public static final String   MSG213_TIME               = "msg213_time";

    /** FTP response message 214. */
    public static final String   MSG214                    = "msg214";

    /** FTP response message 220 (welcome text). */
    public static final String   MSG220_WEL                = "msg220_wel";

    /** FTP response message 220. */
    public static final String   MSG220                    = "msg220";

    /** FTP response message 226. */
    public static final String   MSG226                    = "msg226";

    /** FTP response message 227. */
    public static final String   MSG227                    = "msg227";

    /** FTP response message 229. */
    public static final String   MSG229                    = "msg229";

    /** FTP response message 230. */
    public static final String   MSG230                    = "msg230";

    /** FTP response message 234. */
    public static final String   MSG234                    = "msg234";

    /** FTP response message 250. */
    public static final String   MSG250                    = "msg250";

    /** FTP response message 257. */
    public static final String   MSG257                    = "msg257";

    /** FTP response message 331. */
    public static final String   MSG331                    = "msg331";

    /** FTP response message 350. */
    public static final String   MSG350                    = "msg350";

    /** FTP response message 350. */
    public static final String   MSG350_REST               = "msg350_rest";

    /** FTP response message 421 (Timeout). */
    public static final String   MSG421                    = "msg421";

    /** FTP response message 425. */
    public static final String   MSG425                    = "msg425";

    /** FTP response message 426. */
    public static final String   MSG426                    = "msg426";

    /** FTP response message 431. */
    public static final String   MSG431                    = "msg431";

    /** FTP response message 450. */
    public static final String   MSG450                    = "msg450";

    /** FTP response message 451. */
    public static final String   MSG451                    = "msg451";

    /** FTP response message 500. */
    public static final String   MSG500                    = "msg500";

    /** FTP response message 500. */
    public static final String   MSG500_CMD                = "msg500_cmd";

    /** FTP response message 501. */
    public static final String   MSG501                    = "msg501";

    /** FTP response message 501. */
    public static final String   MSG501_PATH               = "msg501_path";

    /** FTP response message 501. */
    public static final String   MSG501_SIZE               = "msg501_size";

    /** FTP response message 503. */
    public static final String   MSG503                    = "msg503";

    /** FTP response message 503. */
    public static final String   MSG503_USR                = "msg503_usr";

    /** FTP response message 504. */
    public static final String   MSG504                    = "msg504";

    /** FTP response message 530. */
    public static final String   MSG530                    = "msg530";

    /** FTP response message 522. */
    public static final String   MSG522                    = "msg522";

    /** FTP response message 530. */
    public static final String   MSG530_AUTH               = "msg530_auth";

    /** FTP response message 534. */
    public static final String   MSG534                    = "msg534";

    /** FTP response message 536. */
    public static final String   MSG536                    = "msg536";

    /** FTP response message 550. */
    public static final String   MSG550                    = "msg550";

    /** FTP response message 550 (customizable message). */
    public static final String   MSG550_MSG                = "msg550_msg";

    /** FTP response message 550. */
    public static final String   MSG550_NOTEMPTY           = "msg550_notempty";

    /** FTP response message 550. */
    public static final String   MSG550_EXISTS             = "msg550_exists";

    /** FTP response message 550. */
    public static final String   MSG550_COMM               = "msg550_comm";

    /** FTP response message 550. */
    public static final String   MSG550_PERM               = "msg550_perm";

    /** FTP response message 553. */
    public static final String   MSG553                    = "msg553";

    /** Resource: Print working directory. */
    public static final String   PWD                       = "pwd";

    /** Resource: goodbye message. */
    public static final String   MSG_GOODBYE               = "msg.goodbye.default";

}
