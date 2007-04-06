package net.sf.hermesftp.utils;

public class TransferRateLimiter {

    private double maxRate;

    private long   startTime;

    private long   transferredBytes;

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

    public void init(double maxRate) {
        this.maxRate = maxRate;
        startTime = System.currentTimeMillis();
        transferredBytes = 0;
    }
    
    public double getCurrentTransferRate() {
        double seconds = (System.currentTimeMillis() - startTime) / 1024d;
        if (seconds <= 0) {
            seconds = 1;
        }
        return (transferredBytes / 1024d) / seconds;
        
    }

    public void execute(long byteCount) {
        transferredBytes += byteCount;
        while (maxRate >= 0 && getCurrentTransferRate() > maxRate) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    

}
