package com.mezkay.bot.utility.workshop;

import net.dv8tion.jda.api.entities.Message;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

public class WorkshopDownloader {

    private static String OS = System.getProperty("os.name").toLowerCase();

    private ProcessBuilder processBuilder;
    private static String username = "k4mz";
    private static String password = "qPp1foRTGnglWF4hQXZz";

    private Message message;
    private String workshopID;
    private Message repliedMessage;

    private boolean alreadyExtracted = false;


    private static String webHttp;
    public static String webServerFolder;
    private static String cacheFile;
    private static String gmadFolder;
    private static String workshopFolder;
    private static String steamCMD;


    public WorkshopDownloader(Message msg, String workshopID) {
        LoadPaths();
        processBuilder = new ProcessBuilder();
        this.message = msg;
        this.workshopID = workshopID;


        try {

            this.deleteCache();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.initNewDownload();
        this.downloadItem();
        //this.compressFolder();
    }

    public static void LoadPaths() {
        if(OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
            webHttp = "https://mezkay.xyz/workshop_downloader/";
            webServerFolder = "/var/www/mezkay.xyz/html/workshop_downloader/";

            gmadFolder = "/home/mezkay/utils/gmad";
            cacheFile = "/home/mezkay/Steam/steamapps/workshop/appworkshop_4000.acf";

            workshopFolder = "/home/mezkay/Steam/steamapps/workshop/content/4000/";
            steamCMD = "/home/steam/steamcmd/steamcmd.sh";
        } else {
            webServerFolder = "C:\\Users\\kamel\\Desktop\\steamcmd\\steamapps\\workshop\\content\\server\\";
            webHttp = webServerFolder;
            cacheFile = "C:\\Users\\kamel\\Desktop\\steamcmd\\steamapps\\workshop\\appworkshop_4000.acf";
            gmadFolder = "C:\\Users\\kamel\\Desktop\\steamcmd\\gmad.exe";
            workshopFolder = "C:\\Users\\kamel\\Desktop\\steamcmd\\steamapps\\workshop\\content\\4000\\";
            steamCMD = "C:\\Users\\kamel\\Desktop\\steamcmd\\steamcmd.exe";
        }
    }

    public void deleteCache() throws IOException {
        File file = new File(cacheFile);

        if(file.exists()) {
            file.delete();
        }
    }

    public void moveZipArchive() {

        String zipPath = getWorkshopFileName().getParentFile().getAbsolutePath() + "/" + workshopID + ".zip";
        File file = new File(zipPath);
        if(file.exists()) {
            file.renameTo(new File(webServerFolder  + workshopID + ".zip"));
            repliedMessage.editMessage("```You can download addon here\n``` " + webHttp + workshopID + ".zip" + "\n```This link expires after 2 hours```").complete();
            try {
                DeleteFolder.deleteDirectoryJava8(getWorkshopFileName().getParentFile().getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void compressFolder() {


        File workshopFileName = getWorkshopFileName();

        if(workshopFileName != null) {
            repliedMessage.editMessage("```Compressing folder..```").complete();
            String parentFolder = workshopFileName.getParentFile().getAbsolutePath();
            String fileName = workshopFileName.getName().substring(0, workshopFileName.getName().indexOf("."));
            System.out.println("file name :" + fileName);

            Runtime rt = Runtime.getRuntime();

            String command = "7z a " + parentFolder + "/" + workshopID + ".zip " + parentFolder + "/" + workshopID + "/* -bsp1";

            try {
                Process compressProcess = rt.exec(command);

                BufferedReader input = new BufferedReader(new InputStreamReader(compressProcess.getInputStream()));
                BufferedReader error = new BufferedReader(new InputStreamReader(compressProcess.getErrorStream()));

                String line = null;
                String errorLine = null;

                while (((line = input.readLine()) != null) || (((errorLine = error.readLine()) != null))) {
                    if (line != null) {
                        //System.out.println(line);
                        if(line.indexOf("%") >= 0) {
                            repliedMessage.editMessage("```Compressing folder" + line.substring(line.indexOf(" "), line.indexOf("%") + 1) + "```").complete();
                        }

                        if (line.indexOf("Everything is Ok") >= 0) {
                            this.moveZipArchive();
                        }

                    } else {
                        System.out.println(errorLine);
                    }

                }

            } catch (IOException e) {
                repliedMessage.editMessage("Canno't compress folder").complete();
            }
        }

    }

    public void extractGMA(boolean alreadyUnzip) {
        Runtime rt = Runtime.getRuntime();
        File currentFile = this.getWorkshopFileName().getAbsoluteFile();
        String fileName;

        if(!alreadyUnzip)
            fileName = currentFile.getAbsolutePath().substring(0, currentFile.getAbsolutePath().indexOf(".bin"));
        else
            fileName = currentFile.getAbsolutePath().substring(0, currentFile.getAbsolutePath().indexOf(".gma"));

        repliedMessage.editMessage("```Extracting GMA```").complete();
        try {
            System.out.println(gmadFolder + " extract -file " + fileName + " -out " + fileName);
            Process extractProcess;

            if(!this.alreadyExtracted)
                extractProcess = rt.exec(gmadFolder + " extract -file " + fileName + " -out " + currentFile.getParentFile().getAbsolutePath() + "/" + workshopID );
            else
                extractProcess = rt.exec(gmadFolder + " extract -file " + fileName + ".gma" + " -out " + currentFile.getParentFile().getAbsolutePath() + "/" + workshopID);

                BufferedReader input = new BufferedReader(new InputStreamReader(extractProcess.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(extractProcess.getErrorStream()));
            String line = null;
            String errorLine = null;

            while (((line = input.readLine()) != null) || (((errorLine = error.readLine()) != null))) {
                if (line != null) {

                    System.out.println(line);
                    if(line.indexOf("Done!") >= 0) {
                        this.compressFolder();
                    }

                } else {
                    System.out.println(errorLine);
                }

            }

            this.compressFolder();


        } catch (IOException e) {
            repliedMessage.editMessage("Canno't extract GMA").complete();
        }

    }

    public File getWorkshopFileName() {
        File workshopDirectory = new File(workshopFolder + this.workshopID);
        File[] files = workshopDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File pathname, String name) {
                if(name.endsWith(".bin") || name.endsWith(".gma"))
                    return true;
                return false;
            }
        });

        if (files != null && files.length > 0)
            return files[0];

        return null;
    }


    public void initNewDownload() {
        this.repliedMessage = this.message.reply("```Downloading " + workshopID + "..```").complete();
    }

    public void extractFile() throws IOException {
        Runtime rt = Runtime.getRuntime();


        File downloadedFile = getWorkshopFileName();

        //System.out.println(downloadedFile.getName());
        if(downloadedFile.getName().endsWith(".gma")) {
            this.alreadyExtracted = true;
            this.extractGMA(true);
        } else {
            if (downloadedFile != null) {

                this.repliedMessage.editMessage("```Uncompressing file...```").complete();
                String zipCommand = "7z e " + downloadedFile.getAbsolutePath() + " -o" + downloadedFile.getParentFile().getPath() + " -bsp1 -y ";

                Process unzipPrrocess = rt.exec(zipCommand);
                BufferedReader input = new BufferedReader(new InputStreamReader(unzipPrrocess.getInputStream()));
                BufferedReader error = new BufferedReader(new InputStreamReader(unzipPrrocess.getErrorStream()));
                String line = null;
                String errorLine = null;

                while (((line = input.readLine()) != null) || (((errorLine = error.readLine()) != null))) {
                    if (line != null) {
                        if(line.indexOf("%") >= 0) {
                            repliedMessage.editMessage("```Uncompressing file" + line.substring(line.indexOf(" "), line.indexOf("%") + 1) + "```").complete();
                        }
                    } else {
                        if (errorLine.indexOf("There are some data after the end of the payload data") >= 0) {

                            this.extractGMA(false);
                        }
                    }

                }
            } else {
                System.out.println("File not found");
            }
        }
    }

    public void downloadItem() {
        Runtime rt = Runtime.getRuntime();

        // +login kaz3k4 +workshop_download_item 4000 180507408 +quit
        String args = " +login " + username + " " + password + " +workshop_download_item 4000 " + workshopID + " +quit";

        if (workshopID.length() >= 9 && workshopID.length() <= 10) {
            try {


                Process steamCMDProcess = rt.exec(steamCMD + args);
                BufferedReader input = new BufferedReader(new InputStreamReader(steamCMDProcess.getInputStream()));
                BufferedReader errorInput = new BufferedReader(new InputStreamReader(steamCMDProcess.getErrorStream()));

                String line = null;
                String errorLine = null;

                try {
                    while ((line = input.readLine()) != null || ((errorLine = errorInput.readLine()) != null)) {
                        if (line != null) {
                            System.out.println(line);
                        } else {
                            System.out.println(errorLine);
                        }
                        if (line.indexOf("Success. Downloaded item " + workshopID + " to") >= 0) {
                            this.repliedMessage.editMessage("```Download complete```").complete();
                            this.extractFile();
                        } else if (line.indexOf("Downloading item") >= 0){
                            this.repliedMessage.editMessage("```Downloading..```").complete();

                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();

                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.repliedMessage.editMessage("Not valid item").complete();
        }

    }
}
