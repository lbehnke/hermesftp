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

package de.apporiented.hermesftp.utils;

/**
 * Controls the upload/download bandwidth.
 * 
 * @author Behnke
 */
public class TransferRateLimiter {

    private static final int SLEEP_INTERVAL = 100;

    private double           maxRate;

    private long             startTime;

    private long             transferredBytes;

    /**
     * Constructor.
     */
    public TransferRateLimiter() {
        this(-1);
    }

    /**
     * Constructor.
     * 
     * @param maxRate KB per second.
     */
    public TransferRateLimiter(double maxRate) {
        init(maxRate);
    }

    /**
     * Initializes the object.
     * 
     * @param maxRate The maximum transfer rate.
     */
    public void init(double maxRate) {
        this.maxRate = maxRate;
        startTime = System.currentTimeMillis();
        transferredBytes = 0;
    }

    /**
     * Returns the current transfer rate.
     * 
     * @return The transfer rate in KB per seconds.
     */
    public double getCurrentTransferRate() {
        double seconds = (System.currentTimeMillis() - startTime) / 1024d;
        if (seconds <= 0) {
            seconds = 1;
        }
        return (transferredBytes / 1024d) / seconds;

    }

    /**
     * Updates the transfer rate statistics. If the maximum rate has been exceeded, the method
     * pauses.
     * 
     * @param byteCount The number of bytes previously transfered.
     */
    public void execute(long byteCount) {
        transferredBytes += byteCount;
        while (maxRate >= 0 && getCurrentTransferRate() > maxRate) {
            try {
                Thread.sleep(SLEEP_INTERVAL);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}
