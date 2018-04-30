/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.CinemaEntity;
import entity.HallEntity;
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
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import session.HallSessionBeanLocal;

/**
 *
 * @author sherry
 */
@Named(value = "hallManagedBean")
@ViewScoped
public class HallManagedBean implements Serializable {

    private static final Logger LOGGER
            = Logger.getLogger(HallManagedBean.class.getName());
    private static ConsoleHandler handler = null;

    @EJB
    private HallSessionBeanLocal hallSessionBean;

    private CinemaEntity cinemaToView;
    private List<HallEntity> halls;

    private HallEntity newHall;
    private HallEntity thisHall;
    private boolean layoutCreated;

    private int numOfRows = 1;
    private int numOfCols = 1;
    private List<String> handicapSeats = new ArrayList<>(); // e.g. '0_0' corresponds to A1
    private int handicapRow = 1;
    private int handicapCol = 1;
    private List<String> emptySeats = new ArrayList<>(); // e.g. '0_0' corresponds to A1
    private int emptyRow = 1;
    private int emptyCol = 1;

    //for view table
    private List<ColumnModel> columns;
    private List<List<String>> seats;

    /**
     * Creates a new instance of HallsManagedBean
     */
    public HallManagedBean() {
        halls = new ArrayList<>();
        newHall = new HallEntity();
        handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(handler);

    }

    @PostConstruct
    public void init() {
        if (cinemaToView == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            cinemaToView = (CinemaEntity) ec.getFlash().get("cinemaToView");

            LOGGER.log(Level.FINE, "cinmea {0}", cinemaToView);
        }
        try {
            halls = hallSessionBean.getAllHalls(cinemaToView.getId());
        } catch (EntityNotFoundException ex) {
            System.out.println("Unexpected error when retrieve halls: " + ex.getMessage());
        }
    }

    public void viewSchedules(HallEntity hall) throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getFlash().put("hall", hall);
        ec.redirect(ec.getRequestContextPath() + "/schedules.xhtml?faces-redirect=true");
    }

    public void viewHallDetails() {
        resetSeatLayout();
        numOfRows = thisHall.getSeatPlan().length;
        numOfCols = thisHall.getSeatPlan()[0].length;
        createSeatTable(thisHall);
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.execute("PF('hallDetailsDialog').show();");
    }

    public void resetSeatLayout() {
        // clean attributes
        newHall = new HallEntity();
        numOfRows = 0;
        numOfCols = 0;
        columns = new ArrayList<ColumnModel>();
        seats = new ArrayList<List<String>>();
        layoutCreated = false;
    }

    private void createSeatTable(HallEntity hall) {
        // convert seats 2d array to list
        for (int i = 0; i < numOfRows; i++) {
            List<String> row = new ArrayList<String>();
            for (int j = 0; j < numOfCols; j++) {
                if (hall.getSeatPlan()[i][j] == 0) {//NORMAL
                    row.add("S");
                } else if (hall.getSeatPlan()[i][j] == 1) {//HANDICAP
                    row.add("H");
                } else if (hall.getSeatPlan()[i][j] == -1) {//EMPTY
                    row.add("X");
                }
            }
            seats.add(row);
        }

        // populate columns
        for (int k = 0; k < numOfCols; k++) {
            columns.add(new ColumnModel(Integer.toString(k + 1), Integer.toString(k)));
        }
    }

    public void createHall() {
        if (numOfRows < 1 || numOfCols < 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Number of rows and columns must exceed 1. ", null));
        }
        HallEntity newHall = new HallEntity();
        Integer[][] seatPlan = new Integer[numOfRows][numOfCols];
        for (int row = 1; row <= seatPlan.length; row++) {
            for (int col = 1; col <= seatPlan[row - 1].length; col++) {
                seatPlan[row - 1][col - 1] = 0; //ALL NORMAL
            }
        }

        newHall.setSeatPlan(seatPlan);
        LOGGER.log(Level.FINE, "seatPlan {0}", newHall.getSeatPlan());

        try {
            newHall = hallSessionBean.createHall(newHall, cinemaToView.getId());
            if (newHall == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Hall creation failed ", null));
            }
            halls.add(newHall);

            thisHall = newHall;

            LOGGER.log(Level.FINE, "this hall seatPlan {0}", thisHall.getSeatPlan());
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('createHallDialog').hide();");

        } catch (EntityNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Hall creation failed " + ex.getMessage(), null));
        }
    }

    public void deleteHall() {
        if (thisHall == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "thisHall is null ", null));
        }
        try {
            if (hallSessionBean.deleteHall(thisHall.getId())) {
                halls.remove(thisHall);
                // close the dialog
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('view-hall-dialog').hide();");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Please delete the remaining future schedule first ", null));
            }
        } catch (EntityNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Hall deletion failed " + ex.getMessage(), null));
        }

    }

    public void updateHall() {

        Integer[][] newSeatPlan = thisHall.getSeatPlan();

        for (int i = 0; i < newSeatPlan.length; i++) {
            for (int j = 0; j < newSeatPlan[0].length; j++) {
                switch (seats.get(i).get(j)) {
                    case "S":
                        //NORMAL
                        newSeatPlan[i][j] = 0;
                        LOGGER.log(Level.FINE, "S");
                        break;
                    case "H":
                        //HANDICAP
                        newSeatPlan[i][j] = 1;
                        LOGGER.log(Level.FINE, "H");
                        break;
                    case "X":
                        //EMPTY
                        newSeatPlan[i][j] = -1;
                        LOGGER.log(Level.FINE, "X");
                        break;
                    default:
                        break;
                }
            }
        }

        thisHall.setSeatPlan(newSeatPlan);
        try {
            thisHall = hallSessionBean.updateHall(thisHall);
            Integer x = thisHall.getSeatPlan()[0][0];
            LOGGER.log(Level.FINE, "Updated First: {0}", x);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Hall details updated successfully", null));
        } catch (EntityNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Hall update failed " + ex.getMessage(), null));
        }
    }

    public HallSessionBeanLocal getHallSessionBean() {
        return hallSessionBean;
    }

    public void setHallSessionBean(HallSessionBeanLocal hallSessionBean) {
        this.hallSessionBean = hallSessionBean;
    }

    public CinemaEntity getCinemaToView() {
        return cinemaToView;
    }

    public void setCinemaToView(CinemaEntity cinemaToView) {
        this.cinemaToView = cinemaToView;
    }

    public List<HallEntity> getHalls() {
        return halls;
    }

    public void setHalls(List<HallEntity> halls) {
        this.halls = halls;
    }

    public HallEntity getNewHall() {
        return newHall;
    }

    public void setNewHall(HallEntity newHall) {
        this.newHall = newHall;
    }

    public HallEntity getThisHall() {
        return thisHall;
    }

    public void setThisHall(HallEntity thisHall) {
        this.thisHall = thisHall;
    }

    public boolean isLayoutCreated() {
        return layoutCreated;
    }

    public void setLayoutCreated(boolean layoutCreated) {
        this.layoutCreated = layoutCreated;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(int numOfRows) {
        this.numOfRows = numOfRows;
    }

    public int getNumOfCols() {
        return numOfCols;
    }

    public void setNumOfCols(int numOfCols) {
        this.numOfCols = numOfCols;
    }

    public List<String> getHandicapSeats() {
        return handicapSeats;
    }

    public void setHandicapSeats(List<String> handicapSeats) {
        this.handicapSeats = handicapSeats;
    }

    public int getHandicapRow() {
        return handicapRow;
    }

    public void setHandicapRow(int handicapRow) {
        this.handicapRow = handicapRow;
    }

    public int getHandicapCol() {
        return handicapCol;
    }

    public void setHandicapCol(int handicapCol) {
        this.handicapCol = handicapCol;
    }

    public List<String> getEmptySeats() {
        return emptySeats;
    }

    public void setEmptySeats(List<String> emptySeats) {
        this.emptySeats = emptySeats;
    }

    public int getEmptyRow() {
        return emptyRow;
    }

    public void setEmptyRow(int emptyRow) {
        this.emptyRow = emptyRow;
    }

    public int getEmptyCol() {
        return emptyCol;
    }

    public void setEmptyCol(int emptyCol) {
        this.emptyCol = emptyCol;
    }

    static public class ColumnModel implements Serializable {

        private String header;
        private String property;

        public ColumnModel(String header, String property) {
            this.header = header;
            this.property = property;
        }

        public String getHeader() {
            return header;
        }

        public String getProperty() {
            return property;
        }
    }

    public List<ColumnModel> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnModel> columns) {
        this.columns = columns;
    }

    public List<List<String>> getSeats() {
        return seats;
    }

    public void setSeats(List<List<String>> seats) {
        this.seats = seats;
    }

}
