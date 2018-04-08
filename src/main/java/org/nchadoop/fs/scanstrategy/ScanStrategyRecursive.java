package org.nchadoop.fs.scanstrategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.nchadoop.fs.Directory;
import org.nchadoop.fs.HdfsScanner;
import org.nchadoop.fs.SearchRoot;

import java.io.IOException;

@Slf4j
public class ScanStrategyRecursive implements ScanStrategy
{
	private FileSystem fileSystem;

	public ScanStrategyRecursive(final FileSystem fileSystem)
	{
		this.fileSystem = fileSystem;
	}

	public void walkThroughDirectories(final HdfsScanner.StatusCallback callback, final SearchRoot searchRoot, final FileStatus[] listLocatedStatus) throws IOException
	{
		for (final FileStatus fileStatus : listLocatedStatus)
		{
			if (fileStatus.isDirectory())
			{
				try
				{
					walkThroughDirectories(callback, searchRoot, this.fileSystem.listStatus(fileStatus.getPath()));
				}
				catch (final IOException e)
				{
					log.warn("Couldn't open directory {}. Exception: {}", fileStatus.getPath(), e.getMessage());
				}
			}
			else
			{
				if (callback != null)
				{
					callback.onVisitFile(fileStatus);
				}
				addFile(searchRoot, fileStatus);
			}
		}
	}

	private void addFile(final SearchRoot searchRoot, final FileStatus file)
	{
		final Path directorPath = file.getPath().getParent();

		final Directory directory = searchRoot.addPath(directorPath, file.getLen());

		directory.addFile(file);
	}
}
