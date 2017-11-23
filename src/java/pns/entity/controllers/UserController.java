package pns.entity.controllers;

import com.sun.xml.rpc.processor.modeler.j2ee.xml.emptyType;
import pns.entity.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.RollbackException;
//import pns.sessiontime.SessionUtils;

@Named
@SessionScoped
public class UserController extends AbstractEntityController {

    protected User currantSessionUser;
    protected User selectedUser;
    protected User newUser;
    protected List<User> userList = new ArrayList<>();
    List<User> cUsers = new ArrayList<>();

    private long selectedID = -1;
    private String pw = "";
    private String pw1 = "";

    private String displayEditBox = "none";

    protected String testLogin;
    protected String testPasswd;
    private int uType = 0;
    private boolean userAllreadySelected = false;
//    @Inject
//    private SessionUtils utils;

    public UserController() {
    }

    @PostConstruct
    public void init() {
        System.out.println("" + (new Date())
                + ":   ====    UserController START!!   ");
        this.newUser = new User();
        this.selectedUser = new User();
        if (cUsers.size() == 0) {
            cUsers = new ArrayList<>();
        }

        generatePasswords();
        generateUserList();
        System.out.println("  Number Of users " + userList.size());
    }

    public String getDisplayEditBox() {
        return displayEditBox;
    }

    public long getSelectedID() {
        return selectedID;
    }

    public void setSelectedID(long selectedID) {
        this.selectedID = selectedID;
        selectUser();
    }

    public void generatePasswords() {
//        System.out.println("**  PW Generator ");
//        if (newUser == null) {
//            newUser = new User();
//        }
//        System.out.println(" ~~~~~~>>>> (selectedUser == null)  " + (selectedUser == null));
//        if (selectedUser == null) {
//            selectedUser = new User();
//        }
        System.out.println("Generate passwords ");
        int rnd1 = pns.utils.RBytes.rndInt(15, 20);
        int rnd2 = pns.utils.RBytes.rndInt(15, 20);
        int rnd3 = pns.utils.RBytes.rndInt(15, 20);
        int rnd4 = pns.utils.RBytes.rndInt(15, 20);
        int rnd5 = pns.utils.RBytes.rndInt(2, 3);
        int rnd6 = pns.utils.RBytes.rndInt(10, 14);

        String didgitPW = pns.utils.RStrings.rndString(rnd3, '0', '9');
        String didgitPW1 = pns.utils.RStrings.rndString(rnd4, '0', '9');

        String smallPW = pns.utils.RStrings.rndString(rnd1, 'a', 'z');
        String largePW = pns.utils.RStrings.rndString(rnd2, 'A', 'Z');
        String smallPW1 = pns.utils.RStrings.rndString(rnd1, 'a', 'z');
        String largePW1 = pns.utils.RStrings.rndString(rnd2, 'A', 'Z');

        String special = "";
        for (int k = 0; k < rnd5; k++) {
            special += "!$%^-_";
        }

        pw = pw1 = "";
        for (int k1 = 0; k1 < rnd5; k1++) {
            pw += smallPW + largePW + didgitPW + special;
            pw1 += smallPW1 + largePW1 + didgitPW1 + special;
        }

        pw = pns.utils.RStrings.shaffleString(pw);
        pw1 = pns.utils.RStrings.shaffleString(pw1);
        pw = pw.substring(0, rnd6);
        pw1 = pw1.substring(0, rnd6);

        System.out.println("     =========>pw " + pw);
        System.out.println("     =========>pw1 " + pw1);
        System.out.println("   (selectedUser" + selectedUser);
        System.out.println("  generatePasswords :   userAllreadySelected=====> " + userAllreadySelected);
//        pwds[0] = pw;
//        pwds[1] = pw1;
        this.newUser.setPasswd(pw);
        this.selectedUser.setPasswd(pw1);

//
    }

    public List<User> getCUsers() {
        return cUsers;
    }

    public void setCUsers(List<User> cUsers) {
        this.cUsers = cUsers;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getPw1() {
        return pw1;
    }

    public void setPw1(String pw1) {
        this.pw1 = pw1;
    }

    public String delogin() {
        System.out.println(" ------ User " + currantSessionUser + " deloginned ");
        try {
            currantSessionUser = null;
            newUser = new User();
            selectedUser = new User();
        } catch (Exception e) {
        }
        return "index";
    }

    public void dropSelectedUser() {
        System.out.println("----------------------------------->dropSelectedUser ");
        selectedUser = new User();
        generatePasswords();
    }

    public User getCurrantSessionUser() {
        return currantSessionUser;
    }

    public void setCurrantSessionUser(User currantSessionUser) {
        this.currantSessionUser = currantSessionUser;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<User> getUserList() {
        return userList;
    }

    public User getNewUser() {
        return newUser;
    }

    public boolean adminDLGShow() {
        if (userList == null) {
            return true;
        }
        return (userList.size() == 0);
    }

    public boolean loginDLGShow() {
        if (userList == null) {
            return true;
        }
//        currantSessionUser = validateUser();
        return (userList.size() > 0 && currantSessionUser == null);
    }

    public String getTestLogin() {
        return testLogin;
    }

    public void setTestLogin(String testLogin) {
        this.testLogin = testLogin;
    }

    public String getTestPasswd() {
        return testPasswd;
    }

    public void setTestPasswd(String testPasswd) {
        this.testPasswd = testPasswd;
    }

    public boolean isUserAllreadySelected() {
        return userAllreadySelected;
    }

    /**
     * validates a user on correctJoin and returs it, if the user login and
     * password are correct. but if not returns null.
     * <br/>
     * Then tests, eqals to null the currentSession User? or not/
     *
     * @return
     */
    public void validateUser() {
        // currantSessionUser = null;
//        for (int k = 0; k < userList.size(); k++) {
//            if (testLogin.equals(userList.get(k).getEmail()) && testPasswd.equals(userList.get(k).getPasswd())) {
//                System.out.println("Loginned user id " + k + " :  " + userList.get(k));
//                currantSessionUser = userList.get(k);
//            }
//        }

////        try {
        String qlString = " SELECT users FROM User users "
                + "WHERE users.email= '" + testLogin + "' AND "
                + " users.passwd= '" + testPasswd + "' ";
        Query query = em.createQuery(qlString, User.class);
        cUsers = query.getResultList();
        //uType = cUsers.get(0).getUserType();

        boolean test = cUsers.size() == 1;
//        System.out.println("   qlString :  " + qlString + "    " + System.lineSeparator() + cUsers.size() + "   " + test);
        if (test) {
//            System.out.println("Loginned user id: " + cUsers.get(0).toString());
            currantSessionUser = new User();
//            if (currantSessionUser == null) {
//                currantSessionUser = users.get(0);
//            } else {
//                System.out.println("  currantSessionUser " + currantSessionUser);
//            }

            //currantSessionUser = new User();
//            currantSessionUser.setEmail(users.get(0).getEmail());
//            currantSessionUser.setEnabled(users.get(0).isEnabled());
//            currantSessionUser.setMoment(users.get(0).getMoment());
//            currantSessionUser.setId(users.get(0).getId());
//            currantSessionUser.setUserType(users.get(0).getUserType());
        }
        //users.clear();
////        } catch (ClassCastException e) {
////            System.out.println("   USER: " + testLogin);
////        }
        //return null;
    }

    /**
     * Creates current session user while login/
     * <br />
     * If the login data are incorrect? the user does not created
     * <br />
     * Uses on the webpage
     */
    public void currantSessionUserCreator() {
        validateUser();

//        utils.sessionUP();
    }

    /**
     * Generating a new user Instance
     */
    public void createNewAdmin() {
        if (newUser.getEmail() == null || newUser.getPasswd() == null) {
            return;
        }
        newUser.setUserType(2);
        newUser.setMoment(System.currentTimeMillis());
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(newUser);
        tx.commit();
        em.refresh(newUser);
        generateUserList();
        newUser = new User();
    }

    public void removeSelectedUser() {
        if (selectedUser.getId() > 0) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.remove(selectedUser);
            tx.commit();
        }
        selectedUser = new User();
        generateUserList();
        displayEditBox = "none";
    }

    public void createNewUser() {
        try {
            if (newUser != null) {
                System.out.println("NewUser: " + newUser);

                EntityTransaction tx = em.getTransaction();
                tx.begin();

                newUser.setMoment(System.currentTimeMillis());
                em.persist(newUser);
                tx.commit();
                generateUserList();
//                //generatePasswords();
            }
        } catch (RollbackException e) {
        }
    }

    /**
     * Test if selectedUser is null and if not updates it
     */
    public void remakeUser() {
        System.out.println("SelectedUser: TEST METHOD!!");

        System.out.println("SelectedUser: " + selectedUser);
        if (selectedUser.getId() > 0) {
            try {
                EntityTransaction tx = em.getTransaction();
                tx.begin();
                em.persist(selectedUser);
                tx.commit();
                generateUserList();
            } catch (RollbackException e) {
            }
        }
        selectedUser = new User();
        displayEditBox = "none";
    }

    public void updateUser1() {
        System.out.println("SelectedUser: TEST METHOD!!");

        System.out.println("SelectedUser: " + selectedUser);
    }

//    public void updateUser() {
//        System.out.println("SelectedUser: " + selectedUser);
//        try {
//            if (selectedUser != null) {
//
//                EntityTransaction tx = em.getTransaction();
//                tx.begin();
//
////                if (selectedUser.getId() != 0) {
////                    // here suppose? that selectedUser is in DB
////                    selectedUser.setPasswd(pw);
////                } else {
////                    // here suppose? that selectedUser is not in DB -- i.e this is a fresh user
////                    selectedUser.setMoment(System.currentTimeMillis());
////                    selectedUser.setPasswd(pw);
////                }
//                em.persist(selectedUser);
//                tx.commit();
//                generateUserList();
//                //generatePasswords();
//            }
//        } catch (RollbackException e) {
//        }
//        selectedUser = null;
//
//        userAllreadySelected = false;
//    }
    public void selectUser() {
        System.out.println("    UID! " + selectedID);
        //selectedUser = null;
        for (int k = 0; k < userList.size(); k++) {
            if (selectedID == userList.get(k).getId()) {
                selectedUser = userList.get(k);
            }
        }
        displayEditBox = "block ";
        //selectedUser = em.find(User.class, selectedID);
//userAllreadySelected = true;
//        System.out.println("SELECTED USER " + selectedUser);
////        pw = selectedUser.getPasswd();
////        System.out.println("       selectedUser.getPasswd()===> " + selectedUser.getPasswd() + "     selectedUser.getEmail()===> " + selectedUser.getEmail());
    }

    public String generateAdminLink() {
//        selectedUser = null;
        userAllreadySelected = false;
        //uType= cUsers.get(0).getUserType();
        if (uType == 2) {
            return "admins";
        }
        return "admins";
    }

    /**
     * Generate a List of users
     */
    private void generateUserList() {
        String qlString = " SELECT users FROM User users ";
        Query query = em.createQuery(qlString, User.class);
        userList = query.getResultList();
    }

}
