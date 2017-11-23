/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.controllers;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * statelite polutochka
 *
 * @author User
 */
@Named
@RequestScoped
public class TestController {

    private int testVar = 10;

    private String name;

    public String getName() {
        name = "NAME";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTestVar() {
        return testVar;
    }

    public void setTestVar(int testVar) {
        this.testVar = testVar;
    }

}
