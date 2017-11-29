/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.common;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import pns.entity.controllers.FileViewController;

/**
 *
 * @author User
 */
@Stateless
public class CreatorArchiveTimer {

    @Inject
    private FileViewController fvc;

//    @Schedule(dayOfWeek = "*", month = "*", hour = "*", dayOfMonth = "*", year = "*", minute = "*/10", second = "0", persistent = true)
    public void archivator() {
        System.out.println(this.getClass().getCanonicalName() + "   Timer event: " + new Date());
        try {
            fvc.createArchiveREC();
        } catch (Exception ex) {
            System.err.println("  ex  " + ex.getMessage());
            //Logger.getLogger(CreatorArchiveTimer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
