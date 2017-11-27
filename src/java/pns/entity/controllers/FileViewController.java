/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.entity.controllers;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import pns.controllers.FileDownloadController;
import pns.controllers.FileUploadController;
import pns.entity.User;
import pns.fileUtils.FileActor;
import pns.fileUtils.FileSpecActor;
import pns.kiam.entities.satellites.FileMeasured;
import pns.kiam.entities.telescopes.Telescope;
import pns.kiam.filecontrol.FileMeasuredController;

/**
 *
 * @author User
 */
@Named
@RequestScoped
public class FileViewController {

    private List<FileMeasured> fmList = new ArrayList<>();
    private FileMeasured fmCurr;
    protected CriteriaBuilder cb;
    private CriteriaQuery<FileMeasured> cq;

    private String tmpName = "";
    private String filterValue = "";

    protected EntityManagerFactory emfA = Persistence.createEntityManagerFactory("satelliteBandlePU");
    //@PersistenceContext
    protected EntityManager emA = emfA.createEntityManager();

    @Inject
    private FileDownloadController fdc;

    @Inject
    private FileUploadController fuc;

    @Inject
    private FileMeasuredController fmc;

    @PostConstruct
    public void init() {
        loadFileList();
    }

    private List loadFileList() {

        String qlString = " SELECT fm FROM FileMeasured fm ";
        Query query = emA.createQuery(qlString, FileMeasured.class);
        fmList = query.getResultList();
        System.out.println("     ");
        System.out.println("         fmList.size()   " + fmList.size());
//        try {
//            Root<FileMeasured> res = cq.from(FileMeasured.class);
//            cq.select(res);
//            cq.orderBy(cb.asc(res.get("id")));
//            TypedQuery<FileMeasured> Q = emA.createQuery(cq);
//            System.out.println("       NOTNULL");
//            return Q.getResultList();
//        } catch (NullPointerException e) {
//        }
//        System.out.println("NULL !!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return null;//Q.getResultList();
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<FileMeasured> q = cb.createQuery(FileMeasured.class);
//        Root<FileMeasured> c = q.from(FileMeasured.class);
//        q.select(c);
//        TypedQuery<FileMeasured> query = em.createQuery(q);
//        fmList = query.getResultList();
    }

    public List<FileMeasured> getFmList() {
        return fmList;
    }

    public void setFmList(List<FileMeasured> fmList) {
        this.fmList = fmList;
    }

    public FileMeasured getFmCurr() {
        return fmCurr;
    }

    public void setFmCurr(FileMeasured fmCurr) {
        this.fmCurr = fmCurr;
    }

    public void selectFile(FileMeasured fm) {
        this.fmCurr = fm;
        genFileFromRec();
        System.out.println("    fm  " + fmCurr.getFileName() + "    tmpName  " + tmpName);
    }

    public void deSelect() {
        this.fmCurr = null;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
        System.out.println("   4444   555  555 55 " + filterValue);
    }

    public void recDownload(FileMeasured fm) {
        selectFile(fm);
        File f = new File(tmpName);
        System.out.println("Download: " + f.getAbsolutePath());
        try {
            fdc.downloadFile(f);
        } catch (IOException ex) {
            Logger.getLogger(FileViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        f.delete();
    }

    private void genFileFromRec() {
        if (fmCurr != null) {
            String tmpdir = "tmp/";
            FileSpecActor fsa = new FileSpecActor();
            fsa.createDir(tmpdir);
            tmpName = tmpdir + pns.utils.RStrings.rndString(3, 'a', 'z') + "_" + fmCurr.getFileName();
            fsa.setFullFileName(tmpName);
            //System.out.println("  fsa.getFullFileName() " + fsa.getFullFileName());
            fsa.fileWrite(fmCurr.getContent());
        }
    }

    /*



Info:   Size 54 kB
Info:   del:   false  Name  C:/glassfish4/glassfish/domains/domain1/satdata/2017/6-6/NMIN_201706060753__10116.txt
Info:   del:   false
Info:   file C:/glassfish4/glassfish/domains/domain1/satdata/2017/9-4/NDHJ_201709041223__25_ — копия.txt
Info:   exists false
Info:   Size 103 kB
Info:   del:   false  Name  C:/glassfish4/glassfish/domains/domain1/satdata/2017/9-4/NDHJ_201709041223__25_ — копия.txt
Info:   del:   false
Info:   file C:/glassfish4/glassfish/domains/domain1/satdata/2017/9-4/NDHJ_2017090412230__25_.txt
Info:   exists false
Info:   Size 127 kB
Info:   del:   false  Name  C:/glassfish4/glassfish/domains/domain1/satdata/2017/9-4/NDHJ_2017090412230__25_.txt
Info:   del:   false
Info:   file C:/glassfish4/glassfish/domains/domain1/satdata/2017/9-4/NDHJ_201709041223__25_ — копия (2).txt
Info:   exists false
Info:   Size 95 kB
Info:   del:   false  Name  C:/glassfish4/glassfish/domains/domain1/satdata/2017/9-4/NDHJ_201709041223__25_ — копия (2).txt
Info:   del:   false
Info:   file C:/glassfish4/glassfish/domains/domain1/satdata/2017/9-4/ICDM_201709041159__10116.txt
Info:   exists false
Info:   Size 119 kB
Info:   del:   false  Name  C:/glassfish4/glassfish/domains/domain1/satdata/2017/9-4/ICDM_201709041159__10116.txt
Info:   del:   false
Info:   file C:/glassfish4/glassfish/domains/domain1/satdata/2017/7-17/JMKB_201707171016__10116.txt
Info:   exists false
Info:   Size 64 kB
Info:   del:   false  Name  C:/glassfish4/glassfish/domains/domain1/satdata/2017/7-17/JMKB_201707171016__10116.txt
Info:   del:   false
Info:   file C:/glassfish4/glassfish/domains/domain1/satdata/2017/7-17/GREM_201707171009__10116.txt
Info:   exists false
Info:   Size 112 kB
Info:   del:   false  Name  C:/glassfish4/glassfish/domains/domain1/satdata/2017/7-17/GREM_201707171009__10116.txt
Info:   del:   false
Info:   fml    fml.size()  18
     */
    public void createArchiveREC() throws Exception {
        String rroot = fuc.getRooot() + "/satdata";
        //System.out.println("  rroot " + rroot);
        fmc.setArchPath(rroot);
        Set<FileMeasured> fml = fmc.readArchiveFileDir();
        System.out.println("  fml    fml.size()  " + fml.size());

        for (Iterator<FileMeasured> it = fml.iterator(); it.hasNext();) {
            String tmpFName = rroot + "/";
            FileMeasured tmpf = it.next();
            if (!fmList.contains(tmpf)) {
                String fileMonth = tmpf.getMonth() + "";
                if (tmpf.getMonth() < 10) {
                    fileMonth = "0" + tmpf.getMonth();
                }
                String fileDate = tmpf.getDate() + "";
                if (tmpf.getDate() < 10) {
                    fileDate = "0" + tmpf.getDate();
                }
                tmpFName += tmpf.getYear() + "/" + fileMonth + "-" + fileDate + "/" + tmpf.getFileName();
                System.out.println("  file " + tmpFName);
                System.out.println(" exists " + fmList.contains(tmpf));
                System.out.println(" Size " + (1 + tmpf.getContent().length() / 1024) + " kB ");
                emA.getTransaction().begin();
                emA.persist(tmpf);
                emA.getTransaction().commit();
                File f = new File(tmpFName);

                boolean ex = f.exists();
                System.out.println("del:   " + ex + "  Name  " + tmpFName);
                boolean del = f.delete();
                System.out.println("del:   " + del);
            }
        }
        System.out.println("  fml    fml.size()  " + fml.size());
        fml = null;
    }

    public String uploadMomentUTC(FileMeasured fm) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getDefault().getTimeZone("UTC"));
        Date d = new Date(fm.getUploadedMoment());
        return formatter.format(d);
    }

    public void filterOutput() {
        System.out.println("    public void filterOutput(): filterValue " + filterValue);
        if (filterValue.length() == 0 || filterValue == null) {
            return;
        }

        List<FileMeasured> result = new ArrayList<>();
        for (FileMeasured line : fmList) {
            if (line.getFileName().trim().contains(filterValue.trim() + "")) {
                result.add(line);
            }
            if ((line.getDate() + "").trim().contains(filterValue.trim() + "")) {
                result.add(line);
            }
            if ((line.getYear() + "").trim().contains(filterValue.trim() + "")) {
                result.add(line);
            }
            if ((line.getContent() + "").trim().contains(filterValue.trim() + "")) {
                result.add(line);
            }
        }
        fmList = result;
    }
}
