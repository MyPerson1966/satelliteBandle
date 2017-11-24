/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.controllers;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.context.*;
import javax.inject.Inject;
import javax.inject.Named;
import pns.fileUtils.*;
import pns.sessiontime.SessionUtils;

/**
 *
 * @author User
 */
@Named
@SessionScoped
public class DirectoryController implements Serializable {

    private String rootDirectory;
    private String currDirectory;
    private String parentDirectory;

    private List<File> subDirList = new ArrayList<>();
    private List<String> subDirNameList = new ArrayList<>();
    private List<File> fileList = new ArrayList<>();
    private DirGoing dg = new DirGoing();

    private FileActor fa = new FileActor(true);
    @Inject
    private SessionUtils utils;

    public DirectoryController() {

    }

    @PostConstruct
    public void init() {
        fa.setFileDir("satdata");
        rootDirectory = currDirectory = fa.getRootPath() + "/" + fa.getFileDir();
        rootDirectory = absoluteFilePath(rootDirectory);

        dirGoingGenerator();
    }

    public String getShortCurrDir() {
        String res = currDirectory.replace('\\', '/');
        String[] resParts = res.split("/");
        res = resParts[resParts.length - 2] + "=>" + resParts[resParts.length - 1];
        if (resParts[resParts.length - 2].trim().equals("satdata".trim())) {
            res = resParts[resParts.length - 1];
        }
        return res;

    }

    public String getCurrDirectory() {
        return currDirectory;
    }

    public String properFileID(File f) {
        String[] parts = f.getName().split("\\.");
        return parts[0];
    }

    /**
     * changing the directory
     *
     * @param currDirectory
     * @return
     */
    public String changeCurrDir(String currDirectory) {
        utils.sessionDown();
        this.currDirectory = absoluteFilePath(currDirectory);
        dirGoingGenerator();

//        System.out.println("   rootDirectory: "
//                + rootDirectory + System.lineSeparator()
//                + " currDirectory: " + this.currDirectory + "  parentDirectory: " + parentDirectory);
        if (!currDirectory.contains(rootDirectory) || currDirectory.equals(rootDirectory)) {
            this.currDirectory = rootDirectory;
//            System.out.println("========>  TOO UP ");System.out.println(" ----->  equals !!!");
            dirGoingGenerator();

            return "index";
        }

        return "fileBlocs";
    }

    public void setCurrDirectory(String currDirectory) {
//        System.out.println(" dg: " + (dg == null));
        //System.out.println(currDirectory + "    void setCurrDirectory(String currDirectory)currDirectory.length(): " + currDirectory.length());
        //this.currDirectory = fa.getRootPath() + fa.getFileDir() + currDirectory;

        this.currDirectory = currDirectory;
        currDirectory = absoluteFilePath(currDirectory);
        parentDirectory = dg.getParentDirectoryName();

        dirGoingGenerator();

    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public String getParentDirectory() {
        return parentDirectory;
    }

    public List<File> getSubDirList() {
        return subDirList;
    }

    public void setSubDirList(List<File> subDirList) {
        this.subDirList = subDirList;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    public List<String> getSubDirNameList() {
        return subDirNameList;
    }

    public void setSubDirNameList(List<String> subDirNameList) {
        this.subDirNameList = subDirNameList;
    }

    public String properFileName(File f) {
        if (f == null) {
            return null;
        }
        return f.getName();
    }

    public String fileLastModified(File f) {
        if (f == null) {
            return null;
        }
        Date d = new Date(f.lastModified());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(d);
    }

    public String fileLastModified(File f, boolean isUTC) {
        if (f == null) {
            return null;
        }
        Date d = new Date(f.lastModified());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss");
        if (isUTC) {
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        return sdf.format(d);
        //return sdf.format(d);
    }

    private void dirGoingGenerator() {

        currDirectory = absoluteFilePath(currDirectory);

        dg.setDirectoryName(this.currDirectory);
        dg.createParentDir();
        parentDirectory = dg.getParentDirectoryName();
        if (parentDirectory == null) {
            parentDirectory = rootDirectory;
        }
        dg.createSubDirList();
        subDirList = dg.getSubDirList();
        fileList = dg.getFileList();

    }

    private String absoluteFilePath(String filePath) {
        File f = new File(filePath);
        return f.getAbsolutePath();
    }
}
