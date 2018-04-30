/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.ScheduleEntity;
import exception.EntityConflictException;
import exception.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author sherry
 */
@Local
public interface ScheduleSessionBeanLocal {

    public ScheduleEntity createSchedule(ScheduleEntity schedule, Long movieId, Long hallId) throws EntityNotFoundException, EntityConflictException;

    public ScheduleEntity updateSchedule(ScheduleEntity newSchedule) throws EntityNotFoundException, EntityConflictException;

    public ScheduleEntity retrieveSchedule(Long id) throws EntityNotFoundException;

    public List<LocalDate> retrieveScheduleDays(Long hallId) throws EntityNotFoundException;

    public Boolean deleteSchedule(Long id) throws EntityNotFoundException;

    public List<ScheduleEntity> retrieveDailySchedules(Long hallId, LocalDate date) throws EntityNotFoundException;
    
}
