/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import pns.fileUtils.FileActor;
import pns.fileUtils.FileSpecActor;

/**
 *
 * @author User
 */
@Stateless
public class RunBash {

    private FileSpecActor fsa = new FileSpecActor();
    String dest = "satlogs";

    @PostConstruct
    public void init() {
        fsa.setIsAppSubDirPath(true);
        fsa.setFileDir(dest);
        fsa.createDir(fsa.getFileDir());
    }

    public String execute(String[] args) throws IOException {
        String s = "", res = "";
        if (args.length > 0) {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(args);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            int k = 0;
            System.out.println("Script outputs:");
            while ((s = reader.readLine()) != null) {
                res += "            Message #: " + k + ":  " + s + System.lineSeparator();
                //System.out.println();
                k++;
            }
            System.out.println(res);

        }
        return res;
    }

    public void execute(String[] args, Boolean toFile) throws IOException {
        String s = "", out = "";
        System.out.println("ExeCute!" + args.length);

        if (args.length > 0) {
            System.out.println(" 0:  " + args[0]);
//            Runtime rt = Runtime.getRuntime();
//            Process proc = rt.exec(args);
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    proc.getInputStream()));
//            int k = 0;
//            System.out.println("Script outputs:");
//            while ((s = reader.readLine()) != null) {
//                out += "            Message #: " + k + ":  " + s + System.lineSeparator();
//                //System.out.println("            Message #: " + k + ":  " + s);
//                k++;
//            }
//            System.out.println(out);
            //outToFile(out, "/home/abc/logEXE");
        }
    }

    private void outToFile(String out, String logfile) throws IOException {
        File f = new File(logfile);
//        if (!f.exists()) {
//            f.createNewFile();
//        }
        BufferedWriter outWrite = new BufferedWriter(new FileWriter(f));
        outWrite.write(out);  //Replace with the string
        //you are trying to write
        outWrite.close();

        Runtime rt = Runtime.getRuntime();
        rt.exec("chmod 660 " + f.getAbsolutePath());
//        f.setExecutable(false, false);
//        f.setReadable(true, true);
//        f.setWritable(true, false);

    }

    public void generateDir() {

        FileActor fa = new FileActor();
        String rootDir = fsa.getRootPath();
        dest = "/satlog";//+fsa.getFileDir();
        dest = rootDir + dest;
        System.out.println("   First DEST: " + dest);
        fa.createDir(dest);

        System.out.println("     ROOOT:  " + fsa.getRootPath());
        System.out.println("     DEST:  " + dest);

    }

    public void testWrite(String s) throws IOException {
        if (s.trim().length() > 0) {
            generateDir();
            System.out.println("     DIR:  " + fsa.getFileDir());
            fsa.setFullFileName(dest + "/LogFileCreation.txt");
            fsa.fileWrite(s);
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
