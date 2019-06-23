package uk.ac.roe.wfau.phymatopus.kafka.tools;

/**
 * Interface for the loop statistics.
 * 
 */
public interface ReaderStatistics
    {
    /**
     * The number of events that have been processed.
     * 
     */
    public long count();

    /**
     * The time taken (excluding wait) to process the events.
     * 
     */
    public long time();

    /**
     * Statistics bean implementation.
     * 
     */
    public static class Bean
    implements ReaderStatistics
        {
        public Bean(final long count, final long time)
            {
            this.alerts = count;
            this.time   = time;
            }
        private final long alerts;
        @Override
        public long count()
            {
            return this.alerts;
            }
        private final long time ;
        @Override
        public long time()
            {
            return this.time;
            }
        }
    }