/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.sessiontime;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author User
 */
@Stateful
public class SessionUtils {

    private FacesContext fContext;
    private HttpSession session;

    private static final int timeout = 2 * 3600;

    @PostConstruct
    public void init() {

        fContext = FacesContext.getCurrentInstance();
        session = (HttpSession) fContext.getExternalContext().getSession(true);

    }

    public void sessionUP() {
        session.setMaxInactiveInterval(timeout);
    }

    public void sessionDown() {
        FacesContext fContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fContext.getExternalContext().getSession(true);

        long currTM = System.currentTimeMillis() / 1000;
        long sessTM = currTM - session.getCreationTime() / 1000;
        long accsTM = currTM - session.getLastAccessedTime() / 1000;
        long freeTM = session.getMaxInactiveInterval() - accsTM;
//        double frac = (session.getLastAccessedTime() - session.getCreationTime()) / session.getMaxInactiveInterval();
        System.out.println("   =========>>>session creationTime: " + (session.getCreationTime() / 1000) + "  "
                + " ; sessTM  " + sessTM + ""
                + " accsTM " + accsTM + ""
                + "  freeTM  " + freeTM
        );

        if (freeTM < 10) {
            System.out.println(freeTM + " ---------------  " + (session.getMaxInactiveInterval() - 10));
        }
        if (freeTM <= 5) {
            session.invalidate();
        }

    }
}
