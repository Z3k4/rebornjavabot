package com.mezkay.bot.utility.workshop;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.sql.Date;
import java.sql.Time;
import java.time.Instant;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

public class DeleteFolder {
    public static void deleteDirectoryJava8(String dir) throws IOException {

        Path path = Paths.get(dir);

        // read java doc, Files.walk need close the resources.
        // try-with-resources to ensure that the stream's open directories are closed
        try (Stream<Path> walk = Files.walk(path)) {
            walk
                    .sorted(Comparator.reverseOrder())
                    .forEach(DeleteFolder::deleteDirectoryJava8Extract);
        }

    }

    // extract method to handle exception in lambda
    public static void deleteDirectoryJava8Extract(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            System.err.printf("Unable to delete this path : %s%n%s", path, e);
        }
    }

    public static void startScanning() {
        int minutes = 5 ;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startDeletingOldWorkshopArchive();
            }
        }, 0, 1000 * (60 * minutes));
    }

    private static void startDeletingOldWorkshopArchive() {
        //Millis -> Secondes -> Minutes -> Hours
        long expirationTime = 1000 * 60 * 60 * 2;
        File workshopDirectory = new File(WorkshopDownloader.webServerFolder);
        File[] files = workshopDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File pathname, String name) {
                try {
                    File file = new File(pathname + "/" + name);
                    FileTime creationTime = (FileTime) Files.getAttribute(file.toPath(), "creationTime");

                    Date now = new Date(System.currentTimeMillis());
                    Date creationDate = new Date(creationTime.toMillis());

                    System.out.println(now.getTime() - creationDate.getTime());
                    if((now.getTime() - creationDate.getTime()) >= expirationTime) {

                        file.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            };

        });
    }

}
