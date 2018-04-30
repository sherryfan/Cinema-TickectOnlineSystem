/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.HallEntity;
import entity.MovieEntity;
import entity.ScheduleEntity;
import static entity.ScheduleEntity_.hall;
import exception.EntityConflictException;
import exception.EntityNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.context.RequestContext;
import session.MovieSessionBeanLocal;
import session.ScheduleSessionBeanLocal;

/**
 *
 * @author sherry
 */
@Named(value = "scheduleManagedBean")
@ViewScoped
public class ScheduleManagedBean implements Serializable{

    private static final Logger LOGGER
            = Logger.getLogger(ScheduleManagedBean.class.getName());
    private static ConsoleHandler handler = null;
    private HallEntity thisHall;
    private ScheduleEntity thisSchedule;
    private List<LocalDate> days = new ArrayList<>();
    private List<List<ScheduleEntity>> schedules = new ArrayList<>();

    private List<MovieEntity> movies = new ArrayList<>();
    private MovieEntity newScheduleMovie;
    private Date newScheduleDay; // date of new schedule
    private Date newScheduleStart; // start time

    @EJB
    private ScheduleSessionBeanLocal scheduleSessionBean;
    @EJB
    private MovieSessionBeanLocal movieSessionBean;

    /**
     * Creates a new instance of ScheduleManagedBean
     */
    public ScheduleManagedBean() {
        handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(handler);

    }

    @PostConstruct
    public void init() {// load all movies and schedules
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        thisHall = (HallEntity) ec.getFlash().get("thisHall");
        if (thisHall == null) {
            return;
        }
        //get all movies
        movies = movieSessionBean.getAllMovies();
        //map to objects
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("MovieConverter.movies", movies);

        try {
            //get all days with schedules 
            days = scheduleSessionBean.retrieveScheduleDays(thisHall.getId());
            schedules.clear();
            for (LocalDate day : days) {
                //for all days
                List<ScheduleEntity> dailySchedule = scheduleSessionBean.retrieveDailySchedules(thisHall.getId(), day);
                //add daily schedule to schedules
                schedules.add(dailySchedule);
            }
        } catch (EntityNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Hall not found.", null));
        }
    }

    public void viewHallSchedules() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getFlash().put("thisHall", thisHall);
        ec.redirect(ec.getRequestContextPath() + "/schedule.xhtml?faces-redirect=true");
    }

    public void viewSchedule(ScheduleEntity schedule) {
        thisSchedule = schedule;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('view-schedule-dialog').show();");
    }

    public void addSchedule() {
        LocalDate day = newScheduleDay.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDateTime start = LocalDateTime.of(day, LocalTime.of(newScheduleStart.getHours(), newScheduleStart.getMinutes()));
        ScheduleEntity newSchedule = new ScheduleEntity(start, LocalDateTime.now());
        try {
            newSchedule = scheduleSessionBean.createSchedule(newSchedule, newScheduleMovie.getId(), thisHall.getId());
            if (newSchedule != null) { 
                // retrieve days and schedules again
                try {
                    //get all days with schedules 
                    days = scheduleSessionBean.retrieveScheduleDays(thisHall.getId());
                    schedules.clear();
                    for (LocalDate date : days) {
                        //for all days
                        List<ScheduleEntity> dailySchedule = scheduleSessionBean.retrieveDailySchedules(thisHall.getId(), date);
                        //add daily schedule to schedules
                        schedules.add(dailySchedule);
                    }
                } catch (EntityNotFoundException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Hall not found.", null));
                }
                
                
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('create-schedule-dialog').hide();");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Schedule creation failed.", null));
            }
        } catch (EntityConflictException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "New schedule is in conflict with existing schedule. ", null));
        } catch (EntityNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Movie or hall not found.", null));
        }
    }

    public void updateSchedule() {
        // change schedule start time's format from Date to LocalDateTime
        LocalDate day = newScheduleDay.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDateTime start = LocalDateTime.of(
                day,
                LocalTime.of(newScheduleStart.getHours(), newScheduleStart.getMinutes()));
        ScheduleEntity updateSchedule
                = new ScheduleEntity(start, LocalDateTime.now());
        updateSchedule.setId(thisSchedule.getId());
        updateSchedule.setHall(thisSchedule.getHall());
        try {
            thisSchedule = scheduleSessionBean.updateSchedule(updateSchedule);
            if (thisSchedule != null) { 
                
                 // retrieve days and schedules again
                try {
                    //get all days with schedules 
                    days = scheduleSessionBean.retrieveScheduleDays(thisHall.getId());
                    schedules.clear();
                    for (LocalDate date : days) {
                        //for all days
                        List<ScheduleEntity> dailySchedule = scheduleSessionBean.retrieveDailySchedules(thisHall.getId(), date);
                        //add daily schedule to schedules
                        schedules.add(dailySchedule);
                    }
                } catch (EntityNotFoundException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Hall not found.", null));
                }
                
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('view-schedule-dialog').hide();");
            } else {
               FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Schedule update failed.", null));
            }
        } catch (EntityConflictException e) { 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    e.getMessage(), null));
        } catch (EntityNotFoundException e) {FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Movie or hall not found.", null));
        }
    }

    public void deleteSchedule() {
        if (thisSchedule == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Schedule not found.", null));
            return;
        }
        try {
            if (scheduleSessionBean.deleteSchedule(thisSchedule.getId())) {
                // retrieve days and schedules again
                try {
                    //get all days with schedules 
                    days = scheduleSessionBean.retrieveScheduleDays(thisHall.getId());
                    schedules.clear();
                    for (LocalDate date : days) {
                        //for all days
                        List<ScheduleEntity> dailySchedule = scheduleSessionBean.retrieveDailySchedules(thisHall.getId(), date);
                        //add daily schedule to schedules
                        schedules.add(dailySchedule);
                    }
                } catch (EntityNotFoundException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Hall not found.", null));
                }
                
                // close the dialog
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('view-schedule-dialog').hide();");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Schedule delete failed. Cannot delete unscreened schedule", null));
                  }
        } catch (EntityNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Schedule not found.", null));
        }
        
        
        
    }

    public HallEntity getThisHall() {
        return thisHall;
    }

    public void setThisHall(HallEntity thisHall) {
        this.thisHall = thisHall;
    }

    public ScheduleEntity getThisSchedule() {
        return thisSchedule;
    }

    public void setThisSchedule(ScheduleEntity thisSchedule) {
        this.thisSchedule = thisSchedule;
    }

    public List<LocalDate> getDays() {
        return days;
    }

    public void setDays(List<LocalDate> days) {
        this.days = days;
    }

    public List<List<ScheduleEntity>> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<List<ScheduleEntity>> schedules) {
        this.schedules = schedules;
    }

    public List<MovieEntity> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieEntity> movies) {
        this.movies = movies;
    }

    public MovieEntity getNewScheduleMovie() {
        return newScheduleMovie;
    }

    public void setNewScheduleMovie(MovieEntity newScheduleMovie) {
        this.newScheduleMovie = newScheduleMovie;
    }

    public Date getNewScheduleDay() {
        return newScheduleDay;
    }

    public void setNewScheduleDay(Date newScheduleDay) {
        this.newScheduleDay = newScheduleDay;
    }

    public Date getNewScheduleStart() {
        return newScheduleStart;
    }

    public void setNewScheduleStart(Date newScheduleStart) {
        this.newScheduleStart = newScheduleStart;
    }

    public ScheduleSessionBeanLocal getScheduleSessionBean() {
        return scheduleSessionBean;
    }

    public void setScheduleSessionBean(ScheduleSessionBeanLocal scheduleSessionBean) {
        this.scheduleSessionBean = scheduleSessionBean;
    }

}
