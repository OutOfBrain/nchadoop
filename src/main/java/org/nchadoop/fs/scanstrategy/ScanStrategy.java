package org.nchadoop.fs.scanstrategy;

import org.apache.hadoop.fs.FileStatus;
import org.nchadoop.fs.HdfsScanner;
import org.nchadoop.fs.SearchRoot;

import java.io.IOException;

public interface ScanStrategy {
	void walkThroughDirectories(HdfsScanner.StatusCallback callback, SearchRoot searchRoot, FileStatus[] fileStatuses) throws IOException;
}
