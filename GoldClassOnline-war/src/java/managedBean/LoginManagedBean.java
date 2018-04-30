/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.StaffEntity;
import exception.InvalidLoginCredentialException;
import java.io.IOException;
import javax.inject.Named;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import session.UtilSessionBeanLocal;

/**
 *
 * @author sherry
 */
@Named(value = "loginManagedBean")
@RequestScoped
public class LoginManagedBean {

    @EJB
    private UtilSessionBeanLocal utilSessionBean;

    private String account;
    private String password;
    private StaffEntity staff;

    private static final Logger LOGGER
            = Logger.getLogger(LoginManagedBean.class.getName());
    private static ConsoleHandler handler = null;

    /**
     * Creates a new instance of LoginManagedBean
     */
    public LoginManagedBean() {
        handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(handler);
    }

    public void staffLogin(ActionEvent event) throws IOException {
        try {
            staff = utilSessionBean.staffDoLogin(account, password);

            if (staff != null) {
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.getSessionMap().put("staff", staff);
                
                if (("ADMIN").equals(staff.getRole())) {
                    ec.redirect(ec.getRequestContextPath() + "/cinemas.xhtml?faces-redirect=true");
                } else if (("OPERATION").equals(staff.getRole())) {
                    ec.redirect(ec.getRequestContextPath() + "/schedules.xhtml?faces-redirect=true");
                } else{
                    ec.redirect(ec.getRequestContextPath() + "/staffLogin.xhtml?faces-redirect=true");
                }
                
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Input", " "));
            }
        } catch (InvalidLoginCredentialException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), " "));
        }
    }

    public void staffLogout(ActionEvent event) throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getSessionMap().remove("staff");
        ec.redirect(ec.getRequestContextPath() + "/staffLogin.xhtml?faces-redirect=true");
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public StaffEntity getStaff() {
        return staff;
    }

    public void setStaff(StaffEntity staff) {
        this.staff = staff;
    }

}
