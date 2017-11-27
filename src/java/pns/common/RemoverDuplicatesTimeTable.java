/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.common;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import pns.entity.controllers.FileViewController;
import pns.fileUtils.DirectoryDeepGo;
import pns.fileUtils.FileActor;
import pns.fileUtils.FileSpecActor;

/**
 *
 * @author User
 */
@Stateless
public class RemoverDuplicatesTimeTable {

    private FileSpecActor fsa = new FileSpecActor();
    private FileActor fa = new FileActor();
    private DirectoryDeepGo ddg = new DirectoryDeepGo();
    private String rootDir = "";
    private List<File> lastFL = new ArrayList<>();
    private long maxFileAge = 1000 * 3600 * 24;
    private List<File> dupl = new ArrayList<>();

    ;

    @Inject
    private FileViewController fvc;

//    @Schedule(dayOfWeek = "*", month = "*", hour = "*", dayOfMonth = "*", year = "*", minute = "*", second = "*/14", persistent = true)
//    public void myTimer() {
//        System.out.println(this.getClass().getCanonicalName() + "   Timer event: " + new Date());
//        //fvc.createArchiveREC();
//    }
    @Schedule(dayOfWeek = "*", month = "*", hour = "*", dayOfMonth = "*", year = "*", minute = "*", second = "0", persistent = true)
    /**
     * every 20 minutes we are investigate here the existence of possible
     * doublicate files and remove them. The time deep is 2 days
     */
    public void removeDupleFiles() {
        lastFL.clear();
        dupl = new ArrayList<>();
        long d = System.currentTimeMillis();
        long d1 = d - maxFileAge;
        System.out.println(" ----->> Now: " + new Date() + "   " + maxFileAge + " milisec ago " + new Date(d1));

        System.out.println();
        List<File> fl = ddg.getFileList();
//        System.out.println("  fl.size " + fl.size());
        for (int k = 0; k < fl.size(); k++) {
            if (fl.get(k).lastModified() > d1) {
                lastFL.add(fl.get(k));
                //System.out.println(k + "  " + fl.get(k).getAbsolutePath() + "  " + fl.get(k).lastModified());
            }
        }
        // prepaering and creating the list of files,
        // which are candidates to removing
        prepareRemoveFiles(2);
        // search duplicate content in filesand then remove that dubles
        dupl = getDuple(lastFL);
        Calendar calendar = GregorianCalendar.getInstance();
//        if (calendar.get(Calendar.MINUTE) % 2 == 0) {
//            System.out.println("ARCHIVE!");
//            fvc.createArchiveREC();
//        }
    }

    /**
     * every 15 days we are investigate here the existence of possible
     * doublicate files and remove them. The time deep is 33 days
     */
    public void removeSuperOldfiles() {

        // prepaering and creating the list of files,
        // which are candidates to removing
        prepareRemoveFiles(33);
        // search duplicate content in filesand then remove that dubles
        dupl = getDuple(lastFL);
    }

    /**
     * prepares an age, from which we need to collect filelist and then generate
     * correspondent list of files
     *
     * @param numberOfDays - number of days before current moment
     */
    private void prepareRemoveFiles(int numberOfDays) {
        if (numberOfDays < 1) {
            numberOfDays = 2;
        }
        long d = System.currentTimeMillis();
        Date dd = new Date(d);

        DateFormat df = new SimpleDateFormat("dd");
        String ds = df.format(d);
        if (ds.equals("01") || ds.equals("15")) {
            numberOfDays = 33;
        }

        long d1 = d - maxFileAge * numberOfDays;
        lastFL.clear();
        dupl = new ArrayList<>();
        rootDir = fa.getAppRootPath(true);
        //ddg.setRootDir(rootDir + "/satdata");
        ddg.goDeep(rootDir + "/satdata", true);
        System.out.println(" Prepare to remove duple content files"
                + " in " + rootDir + "/satdata, "
                + " created  after " + new Date(d1) + "... ");
// refresh files, that older then dl
        getFilesAfter(d1);

    }

    /**
     * generate list of files, that older then a given age
     *
     * @param age
     * @return
     */
    private void getFilesAfter(long age) {
        lastFL.clear();
        List<File> fl = ddg.getFileList();
//        System.out.println("  fl.size " + fl.size());
        for (int k = 0; k < fl.size(); k++) {
            if (fl.get(k).lastModified() > age) {
                lastFL.add(fl.get(k));
                //System.out.println(k + "  " + fl.get(k).getAbsolutePath() + "  " + fl.get(k).lastModified());
            }
        }
    }

    private List<File> getDuple(List<File> fl) {

        List<File> res = new ArrayList<>();
        for (int k = 0; k < fl.size(); k++) {
            File f = fl.get(k);
            if (f.exists()) {
                if (f.isFile()) {
                    boolean exists = false;
                    exists = hasContent(f, fl);
//                System.out.println(exists + "   " + f.getAbsolutePath());
                    if (exists) {
                        //System.out.println("   " + f.getAbsolutePath());
                        res.add(f);
                    }
                }
            }
        }

        System.out.println(" Found   " + res.size() + " of duples in " + fl.size() + " files ");

        return res;
    }

    /**
     * Deletes the files from the hard, which are in a list parameter The second
     * parameter shows from which element of the list it`s need to remove the
     * file
     *
     * @param fl
     * @param startFrom
     */
    private void deleteFilesFromHard(List<File> fl, int startFrom) {
        int s = 0;
        for (int k = startFrom; k < fl.size(); k++) {
            File tmpf = fl.get(k);
            if (tmpf.exists()) {
                if (tmpf.delete()) {
                    dupl.remove(tmpf);
                    s++;
                    //System.out.println(tmpf.getName()+"  deleted");
                }
            }
        }
        System.out.println(s + " files have been deleted");
    }

    private boolean hasContent(File f, List<File> fl) {
//        System.out.println("");
        if (f.isFile()) {

            if (f.length() < 2) {
                f.delete();
                return false;
            }
//            System.out.println(" ----> f: " + f.getAbsolutePath() + "    " + fl.size());
            FileActor testFA = new FileActor();
            String testContent = "";
            testFA.fileRead(f.getAbsolutePath());
            String tss = " ";
            testContent = testFA.getFileContent();
            tss += " TEST CONTENT " + testContent;
////            System.out.println(tss);
            tss = "";
            testContent = pns.utils.RStrings.removeSpaces(testContent);
            for (int k = 0; k < fl.size(); k++) {
                File tmpf = fl.get(k);
                boolean sameFile = f.getAbsolutePath().trim().equals(tmpf.getAbsolutePath().trim());
//                tss += " --> f: " + f.getAbsolutePath();
//                tss += System.lineSeparator() + "  same: "
//                        + "       " + sameFile + " ; " + tmpf.getAbsolutePath();
//                tss += System.lineSeparator() + "  TEST content " + System.lineSeparator()
//                        + testContent
//                        + System.lineSeparator();

                //System.out.println(tss);
                if (!sameFile) {
                    FileActor tmpFA = new FileActor();
                    tmpFA.fileRead(tmpf.getAbsolutePath());
                    String tmpContent = tmpFA.getFileContent();
//                    String ttt = "asd vfr  ttr jhihui jjghju  jgu  jhuihu" + System.lineSeparator() + "L  OO 987  77 gEEE ";
//                    System.out.println(" ttt Str " + ttt);
//                    ttt = pns.utils.RStrings.removeSpaces(ttt);
//                    System.out.println("    ttt REMOVE SPACES  " + ttt);
                    tss += "     TMP content" + System.lineSeparator() + tmpContent + System.lineSeparator();

                    tmpContent = pns.utils.RStrings.removeSpaces(tmpContent);

                    String[] tmpParts = tmpContent.split(testContent);
                    String[] testParts = testContent.split(tmpContent);
                    int has = testParts.length + tmpParts.length;
                    boolean res = testContent.equals(tmpContent);

//                    System.out.println(k + System.lineSeparator() + tss + System.lineSeparator() + " result " + res + "  has: " + has);
                    if (res) {
                        if (tmpf.exists()) {
                            String tmpfParentName = tmpf.getParentFile().getAbsolutePath();
                            if (tmpf.delete()) {
                                dupl.remove(tmpf);
                                //System.out.println(tmpf.getName()+"  deleted");
                            }

                        }
                        return true;
                    }
                }

            }
//            System.out.println("");
//            System.out.println("");
//            System.out.println("**************************");
        } else {
        }
        return false;
    }

    /**
     * Search the equal content of given file f in the file list fl
     *
     * @param f
     * @param fl
     * @return
     */
    private boolean hasContent0(File f, List<File> fl) {
        //    System.out.println("  f: " + f.getAbsolutePath());
        if (f.isFile()) {

            FileActor testFA = new FileActor();
            String testContent = "";
            testFA.fileRead(f.getAbsolutePath());
            testContent = testFA.getFileContent();
//            System.out.println(" **********>>>testFA.getFileContent().length() " + testFA.getFileContent().length() + "    fl.size() " + fl.size());
//            System.out.println(f.getAbsoluteFile() + System.lineSeparator() + "   TEST CONTENT " + testContent);
            for (int k = 0; k < fl.size(); k++) {
                System.out.println("     %%%---->>>>>! >> " + k);
                String tmpContent = "";
                File tmpf = fl.get(k);
                if (!f.getAbsoluteFile().equals(tmpf.getAbsoluteFile())) {
//                    System.out.println(k + "$$$ f.getAbsoluteFile()  " + f.getAbsoluteFile());
//                    System.out.println(k + "### tmpf.getAbsoluteFile()  " + tmpf.getAbsoluteFile());
                    FileActor tmpFA = new FileActor();
                    tmpFA.fileRead(tmpf.getAbsolutePath());
                    tmpContent = tmpFA.getFileContent();
//                    tmpContent = "asd";
//                    testContent = "asd asd gfr dew asd";

                    String[] tmpParts = tmpContent.split(testContent);
                    String[] testParts = testContent.split(tmpContent);
                    int has = testParts.length + tmpParts.length;
                    boolean ravno = tmpContent.equals(testContent);
//                    if (k > 2) {
//
//                        System.out.println("");
//                        System.out.println("");
//                        System.out.println(
//                                k + "=====>>f " + f.getAbsolutePath() + System.lineSeparator()
//                                + "==>>tmpf " + tmpf.getAbsolutePath() + System.lineSeparator()
//                                + " testContent " + testContent
//                                + System.lineSeparator() + " ******************** "
//                                + System.lineSeparator()
//                                + " tmpContent  " + tmpContent
//                                + System.lineSeparator() + "******************* " + System.lineSeparator()
//                                + ravno + "   tmpParts.length " + tmpParts.length + "  testParts.length " + testParts.length);
//
//                        System.out.println("");
//                        System.out.println("");
//                    }
                    return ravno;
                }

            }

        }
        return false;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
