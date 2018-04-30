/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.CinemaEntity;
import exception.EntityConflictException;
import exception.EntityNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author sherry
 */
@Local
public interface CinemaSessionBeanLocal {

    CinemaEntity createNewCinema(String name) throws EntityConflictException;

    List<CinemaEntity> getAllCinemas();

    CinemaEntity getCinemaById(Long id) throws EntityNotFoundException;

    public Boolean deleteCinema(Long id) throws EntityNotFoundException;

    CinemaEntity updateCinema(String newName, Long id)throws EntityNotFoundException, EntityConflictException ;
    
}
