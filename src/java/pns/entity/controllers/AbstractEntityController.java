package pns.entity.controllers;

import java.io.*;
import java.util.*;
import javax.ejb.*;
import javax.enterprise.context.SessionScoped;
import javax.persistence.*;

public class AbstractEntityController implements Serializable {

//    protected EntityManagerFactory emf = Persistence.createEntityManagerFactory("satelliteBandlePU");
    protected EntityManagerFactory emf = Persistence.createEntityManagerFactory("sat");
    //@PersistenceContext
    protected EntityManager em = emf.createEntityManager();

    public AbstractEntityController() {
    }

}
