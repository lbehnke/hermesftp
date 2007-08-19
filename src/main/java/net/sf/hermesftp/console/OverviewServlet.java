/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2007 Lars Behnke
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

package net.sf.hermesftp.console;

import java.util.Properties;

import net.sf.hermesftp.common.FtpServerOptions;

/**
 * Provides an overview of system and application properties.
 * 
 * @author Administrator
 */
public class OverviewServlet extends AbstractConsoleServlet {

    private static final long serialVersionUID = -594524060863929206L;

    private FtpServerOptions  options;

    /**
     * {@inheritDoc}
     */
    protected Properties getContentProperties() {
        Properties result = new Properties();
        result.putAll(options.getProperties());
        result.put("memory.max", formatNum(Runtime.getRuntime().maxMemory() / 1024));
        result.put("memory.free", formatNum(Runtime.getRuntime().freeMemory() / 1024));
        result.put("memory.total", formatNum(Runtime.getRuntime().totalMemory() / 1024));
        result.put("memory.total.free", formatNum((Runtime.getRuntime().freeMemory() + (Runtime.getRuntime()
            .maxMemory() - Runtime.getRuntime().totalMemory())) / 1024));
        return result;
    }

    /**
     * Getter methode for property <code>options</code>.
     * 
     * @return Property <code>options</code>.
     */
    public FtpServerOptions getOptions() {
        return options;
    }

    /**
     * Setter methode for property <code>options</code>.
     * 
     * @param options Value for <code>options</code>.
     */
    public void setOptions(FtpServerOptions options) {
        this.options = options;
    }

}
