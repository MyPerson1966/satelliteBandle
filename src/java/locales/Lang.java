/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package locales;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author User
 */
@Named
@SessionScoped
public class Lang implements Serializable {

    private String language = "en";

    @PostConstruct
    public void init() {
        FacesContext faceCTX = FacesContext.getCurrentInstance();
        language = faceCTX.getViewRoot().getLocale().getCountry().toLowerCase();
        if (!language.equals("en") && !language.equals("ru")) {
            language = "en";
        }
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String translateMSG(String key) {

        FacesContext faceCTX = FacesContext.getCurrentInstance();

        ResourceBundle bundle = ResourceBundle.getBundle("locales." + language);
        String message = bundle.getString(key);
        System.out.println("----->>~~~~~~~~~~~~~~>>> key: "
                + key + "  bundle==null " + (bundle == null) + ""
                + "  message " + message
        );
        return message;
    }

}
