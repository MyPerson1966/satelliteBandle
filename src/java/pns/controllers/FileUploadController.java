package pns.controllers;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import locales.Lang;
import org.primefaces.model.UploadedFile;
import pns.fileUtils.DirectoryDeepGo;
import pns.fileUtils.FileActor;
import pns.fileUtils.FileSpecActor;

@Named
@RequestScoped
public class FileUploadController implements Runnable {

    @EJB
    private RunBash runBush;
    private String fContent = "";
    private String fSelectedContent = "";
    private String uploadDir = "";
    private String finUploadTo = "";
    private String destination = "";

    private String sucsessUplMSG = "";//File Has Been Upload ";
    private String errorUplMSG = "";//Error whie Uploading ";

    private FileActor fa = new FileActor(true);
    private FileSpecActor fsa = new FileSpecActor();

    private long progress = 0;

    private UploadedFile uploadedFile;
    private File selectedFile;

    private String rootDir = "";
    private Thread th = new Thread(this);
    private String rooot = "";
    @Inject
    Lang languageBean;

    public FileUploadController() {
    }

    @PostConstruct
    public void init() {
        rooot = fa.getAppRootPath(true);

        fsa.setIsAppSubDirPath(true);
        fsa.setFileDir("satdata");
        generatePath();
    }

    public String getFContent() {
        return fContent;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public String getfContent() {
        return fContent;
    }

    public String getSucsessUplMSG() {
        return sucsessUplMSG;
    }

    public String getErrorUplMSG() {
        return errorUplMSG;
    }

    public String getDestination() {
        return destination;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public void generatePath() {
        destination = fa.getFullFileName();
    }

    public void generatePath(String dirName) {
        fsa.fullFilePath(dirName);
        destination = fa.getFullFileName();
    }

    public void generatePath(String dirName, String resultFName) {
        fa.fullFilePath(dirName, resultFName);
        destination = fa.getFullFileName();
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    public String getRootDir() {
        return rootDir;
    }

    public String getRooot() {
        rooot = rooot.replace('\\', '/');
        return rooot;
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public long getProgress() {
        return progress;
    }

    public String getFinUploadTo() {
        return finUploadTo;
    }

    @Override
    public void run() {
        if (uploadedFile != null) {
            copyFile(fContent);
        }
        try {
            throw new InterruptedException();
        } catch (InterruptedException ex) {
        }
    }

    public String getFSelectedContent() {
        return fSelectedContent;
    }

    public void generateFileContent(File f) {
        if (f != null) {
            String fullName = f.getAbsolutePath();
            if (fa.fileRead(fullName)) {
                //System.out.println("    @@@@@   " + fullName);
                fSelectedContent = fa.getFileContent();
                //System.out.println("  " + fSelectedContent);
            }
        }
    }

    public String outputFileContent(File f) {
        if (f != null) {
            fa = new FileActor();
            String fullName = f.getAbsolutePath();
            System.out.println("   String outputFileContent(File f)  " + fullName + "  " + f.exists());
            if (f.exists()) {
                if (fa.fileRead(fullName)) {
                    System.out.println("  ------------------>>>@@@@@   " + fullName + " LEN " + fa.getFileContent().length());
                    return fa.getFileContent().toString();
                    //System.out.println("  " + fSelectedContent);
                }
            }
        }
        fa = null;
        return "; ";
    }

    public void upload() {
        if (uploadedFile == null) {
            errorUplMSG = languageBean.translateMSG("ErrorWhileUploading") + ": "
                    + languageBean.translateMSG("EmptyFile");
            return;
        }

        boolean isUploaded = realUploadFile();
        if (isUploaded) {
            sucsessUplMSG = languageBean.translateMSG("FileUploaded") + ": " + destination;
//            th.start();

            if (uploadedFile != null) {
                copyFile(fContent);
            }
        } else {
            errorUplMSG += ".  " + languageBean.translateMSG("FileDoesNotUploaded");
            //" Error while Uploading: The file format is not supporting or file size is too lage... " + (uploadedFile.getSize() / 1024) + " KB ";
        }

//        try {
////            runBush.execute(args);
////            Runtime rt = Runtime.getRuntime();
//            //Process proc = rt.exec(args);
//            //runBush.testWrite("  123 456 789 987 654 321 0   " + Math.random());
//        } catch (Exception ex) {
//            Logger.getLogger(FileUploadController.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    private void generateFinalPathUpload(String ext) {

        generateRootUploadPath(); // generate a rootDirName

        String prefaceName
                = pns.utils.dateTimeMechanism.convertLongToDateStr(System.currentTimeMillis(),
                        "yyyyMMddHHmm_", true);
        prefaceName = pns.utils.strings.RStrings.rndString(4, 'A', 'Z') + "_" + prefaceName;
        String instrument = "";   //"i_" + pns.utils.RSrings.rndString(3, 'A', 'S');
        String fileName = uploadedFile.getFileName();
        String fileProperName = fileName.split("\\.")[0];

        String dir = fsa.setTemplateTimePath("YYYY/MM-dd", true);
        destination = "/" + fsa.getFileDir();
        destination += "/" + dir;
        destination = rootDir + destination;
        destination = FileActor.getAbsoluteFileName(destination);
        finUploadTo = instrument = instrumentIDCreator(fContent);
// _suspicious
        System.out.println("  ~~~~~~~~~~~~>>fileName " + fileName + ";    " + uploadedFile.getFileName() + "  ***  " + fileName.contains("_suspicious"));
        if (fileName.contains("_suspicious")) {
            finUploadTo += "_suspicious";
        }

        finUploadTo = prefaceName + "_" + finUploadTo + "." + ext;

        System.out.println("generateFinalPathUpload  **************>>fileName " + finUploadTo + ";    ");

    }

    /**
     * Generating rootDirName
     */
    private void generateRootUploadPath() {
        if (uploadedFile.getSize() > 1024 * 1024 * 2) {
            errorUplMSG = languageBean.translateMSG("ErrorWhileUploading") + ": "
                    + languageBean.translateMSG("TheFileSizeIsTooLage") + " "
                    + (uploadedFile.getSize() / 1024) + " KB ";
            return;
        }
        String prefaceName
                = pns.utils.dateTimeMechanism.convertLongToDateStr(System.currentTimeMillis(),
                        "yyyyMMddHHmm_", true);
        // FileName for writing uploaded file

        // FolderName for writing file
        rootDir = fa.getRootPath() + destination;
        rootDir = FileActor.getAbsoluteFileName(rootDir);

        System.out.println(" ~~~~~  rootDir  " + rootDir);

    }

    public boolean realUploadFile() {

        if (uploadedFile.getSize() > 1024 * 1024 * 2) {
            errorUplMSG = languageBean.translateMSG("ErrorWhileUploading") + ": "
                    + languageBean.translateMSG("TheFileSizeIsTooLage")
                    + "... " + (uploadedFile.getSize() / 1024) + " KB ";
            return false;
        }
        try {
            long startRead = System.currentTimeMillis();
            InputStream is = uploadedFile.getInputstream();
            //int read = 0;
            // byte[] bytes = new byte[2048 * 3];
            String[] uplParts = uploadedFile.getFileName().split("\\.");
            String uplExt = uplParts[uplParts.length - 1];

            Scanner sc = new Scanner(uploadedFile.getInputstream(), "cp1251");
            long LNG = 0;
            String line = "";
            while (sc.hasNext()) {
                String currLine = sc.nextLine() + System.lineSeparator();
                //currLine = new String(currLine.getBytes(), "cp1251");
                //System.out.println("   " + currLine);
                fContent += currLine;
                //progress = calcProgress(currLine);
                LNG++;
            }
            if (!kiam.utils.parsers.GeneralValidation.validateProtocols(fContent)) {

                errorUplMSG = languageBean.translateMSG("ErrorWhileUploading") + ": "
                        + languageBean.translateMSG("FileFormatIsNotSupporting");
//                        " Error while Uploading: The file format is not supporting";
                fContent = "  Error file content: " + fContent;
                return false;
            }
            generateRootUploadPath();

            DirectoryDeepGo ddg = new DirectoryDeepGo();
            ddg.setRootDir(rootDir);
            ddg.setDirToInvestigate(fsa.getFileDir());
            ddg.goDeep();
            generateFinalPathUpload(uplExt);
//
//            System.out.println("   ---------->destination: " + destination);
            System.out.println("   ----> finUploadTo:  " + finUploadTo);

            System.out.println(" ---------------------->  ddg.getRootDir(): " + ddg.getRootDir());
            boolean eqa = false;// ddg.checkEqualDataInFiles(fContent, true);
            //System.out.println("     ~~~~~~~~~    eqa: " + eqa);
            if (eqa) {

                errorUplMSG = " Error while Uploading: The file with such content already exists ";
                errorUplMSG = languageBean.translateMSG("ErrorWhileUploading") + ": "
                        + languageBean.translateMSG("FileExists");

                fContent = "Error file content: " + System.lineSeparator() + fContent;
                return false;
            }

            System.out.println(System.lineSeparator() + "rooot!:   " + rooot + System.lineSeparator() + " =============" + System.lineSeparator());
            String FFN = destination + "/" + finUploadTo;
            fsa.setFileDir(destination);
            fsa.setScriptName(rooot + "/scriptS/simplesend.sh");

            fsa.setAppDir(rootDir);
            fsa.setFullFileName(FFN);

        } catch (IOException ex) {
            Logger.getLogger(FileUploadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    /**
     * stoppp!
     */
    public void copyFile(String contentString) {
        String logpath = fsa.getAppDir() + "/satlog/";
        //File flog = new File(logpath + "/satlog");

        boolean bCopy
                = fsa.fileWrite(contentString, "660", rooot + "/scriptS/uploadLOG");
        //fsa.fileWrite(contentString, "660", flog.getAbsolutePath());
        //fsa.fileWrite(contentString, false, true, true);
        //fsa.fileWrite(contentString);
        if (bCopy) {
            sucsessUplMSG = languageBean.translateMSG("FileUploaded") + "!! ";
        } else {
            errorUplMSG = languageBean.translateMSG("UnableToCopyTo") + "  " + destination;
        }

    }

    /**
     * Took Up the Instrument ID from a File Content
     *
     * @param str
     * @return
     */
    private String instrumentIDCreator(String str) {
        str = str.trim();
        String res = "";
        String[] parts = str.split(System.lineSeparator());
        String[] subParts = parts[0].split("\\s");
        res = subParts.length + "_";
        if (subParts.length == 2) {
            res = subParts[0];
        } else if (subParts.length == 3) {
            res = subParts[1];
        } else if (subParts.length == 9) {
            try {
                subParts = parts[1].split("\\s");
                res = subParts[0];
            } catch (ArrayIndexOutOfBoundsException ex) {
            }

        }
        return res;
    }

    private long calcProgress(String line) {
        long result = 0;
        byte[] bytes = line.getBytes();
        result = bytes.length / uploadedFile.getSize();
        return result;
    }
}
