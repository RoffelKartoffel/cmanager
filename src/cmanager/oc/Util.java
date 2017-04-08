package cmanager.oc;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import cmanager.CacheListModel;
import cmanager.geo.Geocache;
import cmanager.geo.GeocacheComparator;
import cmanager.okapi.OKAPI;
import cmanager.okapi.User;

public class Util
{

    static final ArrayList<Geocache> okapiRuntimeCache = new ArrayList<>();

    /**
     *
     * @param stopBackgroundThread Processing is interrupted if this boolean is
     * set true
     * @param clm The model supplying the caches to check
     * @param oi Callback functions
     * @param user OCUser object for OKAPI authentication
     * @param uuid The uuid of the OC user to exclude caches already found by
     * this user
     * @throws Throwable
     */
    public static void findOnOc(final AtomicBoolean stopBackgroundThread, final CacheListModel clm,
                                final OutputInterface oi, final User user, final String uuid,
                                final ShadowList shadowList) throws Throwable
    {
        // Number of found duplicates
        final AtomicInteger count = new AtomicInteger(0);
        // Thread pool which establishes 10 concurrent connection at max
        final ExecutorService service = Executors.newFixedThreadPool(10);
        // Variable to hold an exception throwable if one is thrown by a task
        final AtomicReference<Throwable> throwable = new AtomicReference<Throwable>(null);

        // Create a task for each cache and submit it to the thread pool.
        for (final Geocache gc : clm.getList())
        {
            if (throwable.get() != null)
                break;

            if (stopBackgroundThread.get())
                break;

            Callable<Void> callable = new Callable<Void>() {
                public Void call()
                {
                    if (stopBackgroundThread.get())
                        return null;

                    try
                    {
                        oi.setProgress(count.get(), clm.getList().size());
                        count.getAndIncrement();

                        if (SearchCache.isEmptySearch(gc, uuid))
                            return null;

                        //
                        // Search shadow list for a duplicate
                        //
                        String ocCode = shadowList.getMatchingOCCode(gc.getCode());
                        if (ocCode != null)
                        {
                            Geocache oc = OKAPI.getCacheBuffered(ocCode, okapiRuntimeCache);
                            OKAPI.completeCacheDetails(oc);
                            OKAPI.updateFoundStatus(user, oc);
                            // Found status can not be retrieved without user
                            // so we have a match when there is no user or the
                            // user has not found
                            // the cache
                            if (user == null || !oc.getIsFound())
                            {
                                oi.match(gc, oc);
                                return null;
                            }
                        }

                        //
                        // Search for duplicate using the OKAPI
                        //
                        double searchRadius = gc.hasVolatileStart() ? 1 : 0.05;
                        ArrayList<Geocache> similar =
                            OKAPI.getCachesAround(user, uuid, gc, searchRadius, okapiRuntimeCache);
                        boolean match = false;
                        for (Geocache oc : similar)
                            if (GeocacheComparator.similar(oc, gc))
                            {
                                OKAPI.completeCacheDetails(oc);
                                oi.match(gc, oc);
                                match = true;
                            }

                        if (!match)
                            SearchCache.setEmptySearch(gc, uuid);
                    }
                    catch (Throwable t)
                    {
                        throwable.set(t);
                    }

                    return null;
                }
            };
            service.submit(callable);
        }

        service.shutdown();
        service.awaitTermination(Long.MAX_VALUE,
                                 TimeUnit.DAYS); // incredible high delay but still ugly

        if (throwable.get() != null)
            throw throwable.get();

        oi.setProgress(clm.getList().size(), clm.getList().size());
    }

    public interface OutputInterface {
        void setProgress(Integer count, Integer max);
        void match(Geocache gc, Geocache oc);
    }
}
