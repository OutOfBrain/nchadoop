package org.nchadoop.fs.scanstrategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.nchadoop.fs.Directory;
import org.nchadoop.fs.HdfsScanner;
import org.nchadoop.fs.SearchRoot;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ScanStrategyConcurrent implements ScanStrategy
{
    private final FileSystem fileSystem;
    private final int numWorkers;

    public ScanStrategyConcurrent(final FileSystem fileSystem, final int numWorkers)
    {
        this.fileSystem = fileSystem;
        this.numWorkers = numWorkers;
    }

    @Override
    public void walkThroughDirectories(final HdfsScanner.StatusCallback callback, final SearchRoot searchRoot, final FileStatus[] fileStatuses) throws IOException
    {
        final ConcurrentLinkedQueue<FileStatus[]> workQueue = new ConcurrentLinkedQueue<>();
        final ExecutorService executorService = Executors.newFixedThreadPool(numWorkers);

        final AtomicInteger working = new AtomicInteger();
        final Object lock = new Object();

        final AtomicInteger debugCounter = new AtomicInteger();

        // add initial directory
        workQueue.add(fileStatuses);

        for (int i = 0; i < numWorkers; i++)
        {
            log.debug("start worker");
            executorService.submit(() ->
                {
                    while (true)
                    {
                        final FileStatus[] workFileStatuses;

                        synchronized (lock)
                        {
                            workFileStatuses = workQueue.poll();

                            if (workFileStatuses != null)
                            {
                                working.incrementAndGet();
                            }
                        }

                        if (workFileStatuses == null)
                        {
                            // nothing to work on

                            if (working.get() > 0)
                            {
                                // someone else has work, new work might come in, lets wait
                                try
                                {
                                    log.debug("waiting for work");
                                    Thread.sleep(100);
                                }
                                catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }

                                continue;
                            }
                            else
                            {
                                // no work and no one going to create work - done
                                break;
                            }
                        }

                        scanFiles(workQueue, callback, searchRoot, workFileStatuses);
                        working.decrementAndGet();
                    }

                    log.debug("worker shutting down");
                }
            );
        }

        executorService.shutdown();
        try
        {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void scanFiles(ConcurrentLinkedQueue<FileStatus[]> workQueue, HdfsScanner.StatusCallback callback, SearchRoot searchRoot, FileStatus[] fileStatuses)
    {
        for (final FileStatus fileStatus : fileStatuses)
        {
            log.debug("scan {}", fileStatus.getPath());
            if (fileStatus.isDirectory())
            {
                try
                {
                    workQueue.add(this.fileSystem.listStatus(fileStatus.getPath()));
                }
                catch (final IOException e)
                {
                    log.warn("Couldn't open directory {}. Exception: {}", fileStatus.getPath(), e.getMessage());
                }
            }
            else
            {
                onVisitFile(callback, fileStatus);
                addFile(searchRoot, fileStatus);
                log.debug("added file {}", fileStatus.getPath());
            }
        }
    }

    synchronized private void onVisitFile(HdfsScanner.StatusCallback callback, FileStatus fileStatus)
    {
        if (callback != null)
        {
            callback.onVisitFile(fileStatus);
        }
    }

    synchronized private void addFile(final SearchRoot searchRoot, final FileStatus file)
    {
        final Path directorPath = file.getPath().getParent();

        // calculate real (including replication) size not just file size
        final Directory directory = searchRoot.addPath(directorPath, file.getReplication() * file.getLen());

        directory.addFile(file);
    }
}
