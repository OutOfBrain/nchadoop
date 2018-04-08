package org.nchadoop.fs;

import org.apache.hadoop.fs.GlobFilter;
import org.apache.hadoop.fs.PathFilter;

import java.io.IOException;

public class GlobFilterConverter
{
    public PathFilter toGlobFilter(final String[] globFilter) throws IOException
    {
        // TODO: Remove this one
        PathFilter pathFilter = path -> true;

        for (final String filterPattern : globFilter)
        {
            pathFilter = new GlobFilter(filterPattern, pathFilter);
        }

        return pathFilter;
    }
}
