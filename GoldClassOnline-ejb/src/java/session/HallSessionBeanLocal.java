/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.HallEntity;
import exception.EntityNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author sherry
 */
@Local
public interface HallSessionBeanLocal {

    public HallEntity createHall(HallEntity hall, Long cinemaId) throws EntityNotFoundException;

    public Boolean deleteHall(Long id) throws EntityNotFoundException;

    public HallEntity updateHall(HallEntity hall) throws EntityNotFoundException;

    public HallEntity getHallById(Long id) throws EntityNotFoundException;

    public List<HallEntity> getAllHalls(Long cinemaId) throws EntityNotFoundException;
    
}
