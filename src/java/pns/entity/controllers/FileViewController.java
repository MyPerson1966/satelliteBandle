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
import java.util.List;
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
            System.out.println("  fsa.getFullFileName() " + fsa.getFullFileName());
            fsa.fileWrite(fmCurr.getContent());
        }
    }

    public void createArchiveREC() {
        String rroot = fuc.getRooot() + "/satdata";
        System.out.println("  rroot " + rroot);
        fmc.setArchPath(rroot);
        List<FileMeasured> fml = fmc.readArchiveFileDir();
        System.out.println("  fml    fml.size()  " + fml.size());
        for (int k = 0; k < fml.size(); k++) {
            FileMeasured tmpf = fml.get(k);
            System.out.println("  file " + tmpf.getFileName());
            System.out.println(" exists " + fmList.contains(tmpf));
//            if (!fmList.contains(tmpf)) {
//                emA.getTransaction().begin();
//                emA.persist(tmpf);
//                emA.getTransaction().commit();
//            }
        }
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
