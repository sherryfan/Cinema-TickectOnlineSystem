/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author sherry
 */
@NamedQueries({
    @NamedQuery(name = "ScheduleEntity.findFutureSchedulesByHall",
            query = "SELECT s FROM ScheduleEntity s "
            + "WHERE s.startTime > :startTime "
            + "AND s.hall.id = :hall ")
})
@Entity
public class ScheduleEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate day;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    

    @ManyToOne(cascade = CascadeType.DETACH)
    private MovieEntity movie;

    @ManyToOne(cascade = CascadeType.DETACH)
    private HallEntity hall;

    
    public ScheduleEntity() {
    }

    public ScheduleEntity(LocalDateTime start, LocalDateTime end) {
        this.startTime = start;
        this.endTime = end;
        this.day = LocalDate.of(
                startTime.getYear(),
                startTime.getMonth(),
                startTime.getDayOfMonth());
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public MovieEntity getMovie() {
        return movie;
    }

    public void setMovie(MovieEntity movie) {
        this.movie = movie;
    }

    public HallEntity getHall() {
        return hall;
    }

    public void setHall(HallEntity hall) {
        this.hall = hall;
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
        if (!(object instanceof ScheduleEntity)) {
            return false;
        }
        ScheduleEntity other = (ScheduleEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ScheduleEntity[ id=" + id + " ]";
    }
    
}
