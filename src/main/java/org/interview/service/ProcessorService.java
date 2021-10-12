package org.interview.service;

import java.io.IOException;
import java.io.InputStream;

public interface ProcessorService {
    /**
     * Service method to get tweets
     * @param inputStream
     * @throws IOException
     */
    void getTweets(InputStream inputStream) throws IOException;
}
