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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author sherry
 */
@Stateless
public class CinemaSessionBean implements CinemaSessionBeanLocal {

    @PersistenceContext(unitName = "GoldClassOnline-ejbPU")
    private EntityManager em;

    @Override
    public CinemaEntity createNewCinema(String name) throws EntityConflictException {
        Query q = em.createNamedQuery("CinemaEntity.findCinemaByName")
                .setParameter("name", name);
        
        if(!q.getResultList().isEmpty()){
            throw new EntityConflictException("Cinema with name " + name + " already exists!");
        }
        
        CinemaEntity newCinema = new CinemaEntity(name);
        em.persist(newCinema);
        
        return newCinema;
    }

    @Override
    public List<CinemaEntity> getAllCinemas() {
        Query q = em.createQuery("SELECT c FROM CinemaEntity c ");
        return q.getResultList();
    }

    @Override
    public CinemaEntity getCinemaById(Long id) throws EntityNotFoundException {
        if (id == null) {
            return null;
        }

        CinemaEntity cinema = em.find(CinemaEntity.class, id);
        if (cinema == null) {
            throw new EntityNotFoundException("Cinema " + id + " not found!");
        }
        
        return cinema;
    }
    

    @Override
    public CinemaEntity updateCinema(String newName, Long id) throws EntityNotFoundException, EntityConflictException {
        if (newName == null || id == null) {
            return null;
        }
        
        CinemaEntity cinema = this.getCinemaById(id);
        Query q = em.createNamedQuery("CinemaEntity.findCinemaByName")
                .setParameter("name", newName);
        
        if(!q.getResultList().isEmpty()){
            throw new EntityConflictException("Cinema with name " + newName + " already exists!");
        }else {
            cinema.setName(newName);
            em.merge(cinema);
            return cinema;
        }
        
    }  
    
    @Override
    public Boolean deleteCinema(Long id) throws EntityNotFoundException {
        if (id == null) 
            return false;

        CinemaEntity cinema = this.getCinemaById(id);

        if (cinema.getHalls().isEmpty()) { 
            em.remove(cinema);
            em.flush();
            return true;
        } else {
            return false; //cinema has remaining hals, must delete all halls first
        }
    }

    
    
    
    
}
