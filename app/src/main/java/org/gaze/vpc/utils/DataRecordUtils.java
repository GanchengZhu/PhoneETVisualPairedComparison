package org.gaze.vpc.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataRecordUtils {
    private String fileName;
    private final BufferedWriter writer;
    ExecutorService executorService;
    public DataRecordUtils(String fileName){
        try {
            writer = new BufferedWriter(new FileWriter(fileName, true));
            executorService = Executors.newSingleThreadExecutor();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeLine(final String line){
        executorService.submit(() -> {
            synchronized (writer) {
                try {
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void close(){
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
