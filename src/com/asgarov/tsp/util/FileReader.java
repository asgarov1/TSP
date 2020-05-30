package com.asgarov.tsp.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileReader {
    /**
     * Utility method that reads the file
     * @param fileName
     * @return list of lines read from the file
     * @throws IOException
     */
    public List<String> readFile(String fileName) throws IOException, URISyntaxException {
        URI uri = Objects.requireNonNull(this.getClass().getClassLoader()
                .getResource(fileName)).toURI();
        return Files.lines(Paths.get(uri)).collect(Collectors.toList());
    }
}
