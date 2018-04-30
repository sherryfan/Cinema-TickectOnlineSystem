/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.CinemaEntity;
import entity.HallEntity;
import exception.EntityConflictException;
import exception.EntityNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import org.primefaces.context.RequestContext;
import session.CinemaSessionBeanLocal;

/**
 *
 * @author sherry
 */
@Named(value = "cinemaManagedBean")
@ViewScoped
public class CinemaManagedBean implements Serializable {

    private static final Logger LOGGER
            = Logger.getLogger(CinemaManagedBean.class.getName());
    private static ConsoleHandler handler = null;
    
    @EJB
    private CinemaSessionBeanLocal cinemaSessionBean;

    private List<CinemaEntity> cinemas; // all current cinemas
    private String name;
    private CinemaEntity thisCinema;
    private HallEntity thisHall;

    private int numOfHalls;

    //input
    private String newName;

    /**
     * Creates a new instance of CinemaManagedBean
     */
    public CinemaManagedBean() {
        cinemas = new ArrayList<>();
        handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(handler);
    }

    @PostConstruct
    public void init() {
        cinemas = cinemaSessionBean.getAllCinemas();
    }

    public void createNewCinema() throws IOException {
        try {
            CinemaEntity newCinema = cinemaSessionBean.createNewCinema(name);
            cinemas.add(newCinema);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New cinema: " + name + " created successfully.", null));
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('createCinemaDialog').hide();");
        } catch (EntityConflictException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new cinema: " + ex.getMessage(), null));
        }
    }

    public Integer getNumberOfHalls(List<HallEntity> halls) throws IOException {
        if (halls == null) {
            return 0;
        } else {
            return halls.size();
        }
    }

    public void updateCinema() throws IOException {
        try {
            CinemaEntity updatedCinema = cinemaSessionBean.updateCinema(newName, thisCinema.getId());
            if (updatedCinema != null) { // success
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('create-movie-dialog').hide();");
            } else {
                //LOGGER.log(Level.SEVERE, "Cinema update failed!");
            }
        } catch (EntityConflictException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating the new cinema: " + ex.getMessage(), null));
        } catch (EntityNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating the new cinema: " + ex.getMessage(), null));
        }
    }

    public void deleteCinema() throws IOException {
        try {
            if (cinemaSessionBean.deleteCinema(thisCinema.getId())) {
                 LOGGER.log(Level.FINE, "Cinema delete succeed!");
                cinemas.remove(thisCinema);
                
                if (cinemas.isEmpty()) {
                    // no cinemas left
                    thisCinema = null;
                } else {
                    thisCinema = cinemas.get(0);    
                 LOGGER.log(Level.FINE, "current cinema is {0}", thisCinema);
                }
            } else {//have remaining hall
                
                 LOGGER.log(Level.SEVERE, "Cinema delete failed - have remaining halls");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting the new cinema: ", null));
            }
        } catch (EntityNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting the new cinema: " + ex.getMessage(), null));
        }
    }
    
    public void viewCinemaDetails() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getFlash().put("cinemaToView", thisCinema);
        ec.redirect(ec.getRequestContextPath() + "/cinemaDetails.xhtml?faces-redirect=true");
    }
    
   

    public List<CinemaEntity> getCinemas() {
        return cinemas;
    }

    public void setCinemas(List<CinemaEntity> cinemas) {
        this.cinemas = cinemas;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CinemaEntity getThisCinema() {
        return thisCinema;
    }

    public void setThisCinema(CinemaEntity thisCinema) {
        this.thisCinema = thisCinema;
    }

    public HallEntity getThisHall() {
        return thisHall;
    }

    public void setThisHall(HallEntity thisHall) {
        this.thisHall = thisHall;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

}
