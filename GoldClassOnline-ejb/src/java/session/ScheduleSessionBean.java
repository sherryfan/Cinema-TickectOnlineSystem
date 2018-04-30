/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.HallEntity;
import entity.MovieEntity;
import entity.ScheduleEntity;
import exception.EntityConflictException;
import exception.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author sherry
 */
@Stateless
public class ScheduleSessionBean implements ScheduleSessionBeanLocal {

    @PersistenceContext(unitName = "GoldClassOnline-ejbPU")
    private EntityManager em;
    @EJB
    private MovieSessionBeanLocal movieSessionBean;
    @EJB
    private HallSessionBeanLocal hallSessionBean;

    // add screening schedule: 15 mins buffer time & no overlap to other schedules
    @Override
    public ScheduleEntity createSchedule(ScheduleEntity schedule, Long movieId, Long hallId) throws EntityNotFoundException, EntityConflictException {
        // does not require an end time (auto-calculated based on movie duration)
        if (schedule == null || schedule.getStartTime() == null || movieId == null || hallId == null) {
            return null;
        }
        HallEntity hall = hallSessionBean.getHallById(hallId);
        MovieEntity movie = movieSessionBean.retrieveMovie(movieId);
        
        schedule.setEndTime(schedule.getStartTime().plusMinutes(movie.getDuration()));
        schedule.setHall(hall);
        
        // check onflict
        if (this.checkScheduleConflict(schedule)) {
            throw new EntityConflictException("Schedule conflict!");
        }
        
        //
        ScheduleEntity newSchedule = new ScheduleEntity(schedule.getStartTime(), schedule.getEndTime());

        newSchedule.setHall(hall);
        hall.getSchedules().add(newSchedule);
        newSchedule.setMovie(movie);
        movie.getSchedules().add(newSchedule);
        
        
        em.persist(newSchedule);
        em.merge(movie);
        em.merge(hall);
        return newSchedule;
    }


    private Boolean checkScheduleConflict(ScheduleEntity schedule) {
        if (schedule == null || schedule.getHall() == null 
                || schedule.getStartTime() == null || 
                schedule.getEndTime() == null) {
            return true;
        }
        
        Query q = em.createQuery("SELECT s from ScheduleEntity s "
                + "WHERE s.hall.id = :hall "
                + "AND ((s.endTime > :startTime "
                + "AND s.endTime < :endTime) "
                + "OR (s.startTime < :endTime "
                + "AND s.startTime > :startTime)) ")
                .setParameter("startTime", schedule.getStartTime())
                .setParameter("endTime", schedule.getEndTime())               
                .setParameter("hall", schedule.getHall().getId());
        return !q.getResultList().isEmpty();
    }

    //update a new startTime

    /**
     *
     * @param newSchedule
     * @return
     * @throws EntityNotFoundException
     * @throws EntityConflictException
     */
    @Override
    public ScheduleEntity updateSchedule(ScheduleEntity newSchedule) throws EntityNotFoundException, EntityConflictException {
        if (newSchedule == null || newSchedule.getId() == null || newSchedule.getStartTime() == null) {
            return null;
        }
        if (newSchedule.getStartTime().isBefore(LocalDateTime.now())) {
            throw new EntityConflictException("Schedule cannot be moved to the past!");
        }
        
        ScheduleEntity schedule = this.retrieveSchedule(newSchedule.getId());
        if (schedule.getStartTime().isBefore(LocalDateTime.now())) {
            throw new EntityConflictException("schedule has already past!");
        }

        LocalDateTime oldStart = schedule.getStartTime();
        LocalDateTime oldEnd = schedule.getEndTime();
        
        schedule.setStartTime(LocalDateTime.of(1999, 1, 1, 0, 0));
        schedule.setEndTime(LocalDateTime.of(1999, 1, 1, 0, 0));
        em.merge(schedule);
        
        newSchedule.setEndTime(newSchedule.getStartTime().plusMinutes(schedule.getMovie().getDuration()));

        //check newSchedule time duration conflict
        if (this.checkScheduleConflict(newSchedule)) {
            // restore
            schedule.setStartTime(oldStart);
            schedule.setEndTime(oldEnd);
            
            throw new EntityConflictException("Schedule conflict!");
        } else {
            //no conflict, update 
            schedule.setStartTime(newSchedule.getStartTime());
            schedule.setEndTime(newSchedule.getEndTime());
            
            em.merge(schedule);
            return schedule;
        }
    }

    /**
     *
     * @param id
     * @return
     * @throws EntityNotFoundException
     */
    @Override
    public ScheduleEntity retrieveSchedule(Long id) throws EntityNotFoundException {
        if (id == null) { return null; }

        ScheduleEntity schedule = em.find(ScheduleEntity.class, id);
        if (schedule == null) { 
            throw new EntityNotFoundException("Schedule " + id + " not found!");
        } else {
            return schedule;
        }
    }

    /**
     *
     * @param hallId
     * @return
     * @throws EntityNotFoundException
     */
    @Override
    public List<LocalDate> retrieveScheduleDays(Long hallId)
            throws EntityNotFoundException {
        if (hallId == null) {
            return null;
        }
        HallEntity hall = hallSessionBean.getHallById(hallId);

        List<LocalDate> days = new ArrayList<>();
        hall.getSchedules().stream().filter((schedule) -> (!days.contains(schedule.getDay()))).forEachOrdered((schedule) -> {
            days.add(schedule.getDay());
        });
        return days;
    }

    //view all schedules in a day
    @Override
    public List<ScheduleEntity> retrieveDailySchedules(Long hallId, LocalDate date)throws EntityNotFoundException {
        if (hallId == null || date == null) {
            return null;
        }

        HallEntity hall = hallSessionBean.getHallById(hallId);

        Query q = em.createQuery("SELECT s "
                + "FROM ScheduleEntity s "
                + "WHERE s.hall.id = :hall "
                + "AND s.day = :day "
                + "ORDER BY s.startTime ASC ")
                .setParameter("hall", hall.getId())
                .setParameter("day", date);
        List<ScheduleEntity> result;
        try{ 
            result = q.getResultList(); 
            if(result.isEmpty()){
                throw new EntityNotFoundException("Error: no result");
            }
            return result;
        }catch(Exception ex){
            return null;//
        }
        
    }

    /**
     *
     * @param id
     * @return
     * @throws EntityNotFoundException
     */
    @Override
    public Boolean deleteSchedule(Long id)throws EntityNotFoundException {
        if (id == null) {
            return null;
        }

        ScheduleEntity schedule = this.retrieveSchedule(id);
        //only delete done schedules
        if (schedule.getStartTime().isBefore(LocalDateTime.now())) {
            schedule.getMovie().getSchedules().remove(schedule);
            schedule.getHall().getSchedules().remove(schedule);
            em.remove(schedule);
            em.flush();
            return true;
        } else {
            return false;
        }
    }
}
