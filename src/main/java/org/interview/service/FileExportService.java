package org.interview.service;

import com.google.gson.Gson;
import org.interview.model.DTO.StatisticDTO;
import org.interview.model.DTO.TweetListDTO;

import java.io.IOException;
import java.util.Set;

public interface FileExportService {
    /**
     * Create json file to export tweet information
     * @param authorVOS
     * @param gson
     * @throws IOException
     */
    void createJsonTweetFile(Set<TweetListDTO> authorVOS, Gson gson) throws IOException;

    /**
     * Create json file to export statistics information
     * @param statisticDTO
     * @param gson
     * @throws IOException
     */
    void createJsonStatisticFile(StatisticDTO statisticDTO, Gson gson) throws IOException;

}
