/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sherry
 */
@Entity
public class HallEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int number;
    private Integer numOfRows = 0;
    private Integer numOfCols = 0;

    //'0' for normal, '1' for handicapped, '-1' for empty 
    private Integer[][] seatPlan;

    @XmlTransient
    @ManyToOne(cascade = CascadeType.DETACH)
    private CinemaEntity cinema;

    @XmlTransient
    @OneToMany(cascade = CascadeType.DETACH, mappedBy = "hall")
    private List<ScheduleEntity> schedules = new ArrayList<>();

    public HallEntity() {
    }

    public HallEntity(int number, Integer [][] seatPlan) {
        this.number = number;
        if (seatPlan != null && seatPlan.length > 0) {
            this.seatPlan = seatPlan;
            this.numOfRows = seatPlan.length;
            this.numOfCols = seatPlan[0].length;
        }
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Integer getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(Integer numOfRows) {
        this.numOfRows = numOfRows;
    }

    public Integer getNumOfCols() {
        return numOfCols;
    }

    public void setNumOfCols(Integer numOfCols) {
        this.numOfCols = numOfCols;
    }

    public Integer[][] getSeatPlan() {
        return seatPlan;
    }

    public void setSeatPlan(Integer[][] seatPlan) {
        this.seatPlan = seatPlan;
    }

    public CinemaEntity getCinema() {
        return cinema;
    }

    public void setCinema(CinemaEntity cinema) {
        this.cinema = cinema;
    }

    public List<ScheduleEntity> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleEntity> schedules) {
        this.schedules = schedules;
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HallEntity)) {
            return false;
        }
        HallEntity other = (HallEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.HallEntity[ id=" + id + " ]";
    }

}
