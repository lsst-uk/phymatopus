/**
 * 
 */
package uk.ac.roe.wfau.phymatopus.avro.lsst;

import java.util.Iterator;

import org.apache.avro.generic.GenericData;

import uk.ac.roe.wfau.phymatopus.alert.PrevCandidate;

/**
 * 
 *
 */
public class LsstPrevCandidateWrapper
extends LsstBaseCandidateWrapper
implements PrevCandidate
    {
    public LsstPrevCandidateWrapper(final GenericData.Record record, final CharSequence objectid)
        {
        super(
            record,
            objectid
            );
        }

    /**
     * An Iterable implementation.
     * 
     */
    public static class IterableWrapper
    implements Iterable<PrevCandidate>
        {
        private CharSequence objectid ;
        public IterableWrapper(final Iterable<GenericData.Record> inner, final CharSequence objectid)
            {
            this.inner = inner ;
            this.objectid = objectid;
            }
        private Iterable<GenericData.Record> inner;
        @Override
        public Iterator<PrevCandidate> iterator()
            {
            return new IteratorWrapper(
                ((this.inner != null) ? this.inner.iterator() : null),
                this.objectid
                );
            }
        }

    /**
     * An Iterator implementation.
     * 
     */
    public static class IteratorWrapper
    implements Iterator<PrevCandidate>
        {
        private CharSequence objectid ;
        public IteratorWrapper(final Iterator<GenericData.Record> inner, final CharSequence objectid)
            {
            this.objectid = objectid;
            this.inner = inner ;
            }
        private Iterator<GenericData.Record> inner;
        @Override
        public boolean hasNext()
            {
            if (null != inner)
                {
                return inner.hasNext();
                }
            else {
                return false ;
                }
            }
        @Override
        public PrevCandidate next()
            {
            if (null != inner)
                {
                return new LsstPrevCandidateWrapper(
                    this.inner.next(),
                    this.objectid
                    );
                }
            else {
                throw new RuntimeException(
                    "Call to next() on an empty (null) Iterator."
                    );
                }
            }
        }
    }
