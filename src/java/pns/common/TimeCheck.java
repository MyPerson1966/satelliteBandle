/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.common;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author User
 */
@Named
//@ViewScoped
public class TimeCheck implements HttpSessionListener {

    /**
     * listener="#{timoutCheck.increment}" update="txt_count" Creates a new
     * instance of TimoutCheck
     */
    private int number;

    public int getNumber() {
        return number;
    }

    public void increment() {
        number++;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {

    }

}
