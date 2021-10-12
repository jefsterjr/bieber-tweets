package org.interview.service.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.interview.model.DTO.StatisticDTO;
import org.interview.model.DTO.TweetListDTO;
import org.interview.service.FileExportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Set;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

@Slf4j
@Service
public class FileExportServiceImpl implements FileExportService {

    @Value("${output-path}")
    private String outputPath;

    public void createJsonTweetFile(Set<TweetListDTO> authorVOS, Gson gson) throws IOException {

        log.info("Creating tweet file to export with {} records. Saving in: {}", authorVOS.size(), outputPath);
        final Path path = Paths.get(outputPath + "\\" + new Date().getTime() + ".json");
        Files.writeString(
                path,
                gson.toJson(authorVOS),
                CREATE, APPEND);
    }

    public void createJsonStatisticFile(StatisticDTO statisticDTO, Gson gson) throws IOException {

        log.info("Creating statistics file. Saving in: {}", outputPath);
        final Path path = Paths.get(outputPath + "\\statistic.json");
        Files.writeString(
                path,
                gson.toJson(statisticDTO) + System.lineSeparator(),
                CREATE, APPEND
        );
    }
}
