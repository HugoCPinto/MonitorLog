package pt.monitor;



import java.io.*;
import java.nio.file.*;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Monitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Monitor.class);

    private static final String pathDirectory = "";
    private static final String fileLog = "";
    private static final String pattern = "WARN|ERROR";

    public Monitor(){

    }

    private void trackLog(){
        int characters = 0;
        int lines = 0;

        LOGGER.info("Tracking log file {}", fileLog);

        try{
            // create watch service
            WatchService ws = FileSystems.getDefault().newWatchService();

            //create the path to the directory and file
            Path pathDir = Paths.get(pathDirectory);
            Path pathFile = Paths.get(fileLog);

            // monitoring updates
            pathDir.register(ws, StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey key;
            while ((key = ws.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;

                    if(event.context().equals(pathFile)){
                        BufferedReader in = new BufferedReader(new FileReader(pathDirectory+"\\"+fileLog));
                        String line;
                        Pattern p = Pattern.compile(pattern);
                        in.skip(characters);
                        while ((line = in.readLine()) != null) {
                            lines++;
                            characters += line.length() + System.lineSeparator().length();
                            if (p.matcher(line).find()) {
                                LOGGER.info("{}", line);
                            }
                        }
                    }
                }
                key.reset();
            }

        }catch(Exception e){
            LOGGER.error("{}", e);
        }
    }

    public static void main (String[] args){
        new Monitor().trackLog();
    }

}
