/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.CinemaEntity;
import entity.HallEntity;
import exception.EntityNotFoundException;
import java.time.LocalDateTime;
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
public class HallSessionBean implements HallSessionBeanLocal {

    @PersistenceContext(unitName = "GoldClassOnline-ejbPU")
    private EntityManager em;
    @EJB
    private CinemaSessionBeanLocal cinemaSessionBean;

    // create hall
    @Override
    public HallEntity createHall(HallEntity hall, Long cinemaId)
            throws EntityNotFoundException {
        if (hall == null || hall.getSeatPlan() == null
                || cinemaId == null) {
            return null;
        }

        CinemaEntity cinema = cinemaSessionBean.getCinemaById(cinemaId);
        HallEntity newHall = new HallEntity(cinema.getHalls().size() + 1, hall.getSeatPlan());

        cinema.getHalls().add(newHall);
        newHall.setCinema(cinema);

        em.persist(newHall);
        em.merge(cinema);
        em.flush();

        return newHall;
    }


    @Override
    public HallEntity updateHall(HallEntity hall)
            throws EntityNotFoundException {
        if (hall == null || hall.getId() == null) {
            return null;
        }

        HallEntity updatedHall = this.getHallById(hall.getId());
        updatedHall.setSeatPlan(hall.getSeatPlan());

        em.merge(updatedHall);
        return updatedHall;
    }

    // delete hall
    @Override
    public Boolean deleteHall(Long id)
            throws EntityNotFoundException {
        if (id == null) {
            return false;
        }

        HallEntity hall = this.getHallById(id);

        // check if this hall has future screening schedules left
        Query q = em.createNamedQuery("ScheduleEntity.findFutureSchedulesByHall")
                .setParameter("hall", hall.getId())
                .setParameter("startTime", (LocalDateTime.now()));

        if (q.getResultList().isEmpty()) {
            //no future schedule
            hall.getCinema().getHalls().remove(hall);
            em.remove(hall);
            em.flush();
            return true;
        } else {
            // need to remove remaining schedules first
            return false;
        }
    }

    // view hall details
    @Override
    public HallEntity getHallById(Long id)
            throws EntityNotFoundException {
        if (id == null) {
            return null;
        }

        HallEntity hall = em.find(HallEntity.class, id);
        if (hall == null) { // id not found
            throw new EntityNotFoundException("Hall " + id + " not found!");
        } else {
            return hall;
        }
    }

    // list all cinema halls
    @Override
    public List<HallEntity> getAllHalls(Long cinemaId)
            throws EntityNotFoundException {
        if (cinemaId == null) {
            return null;
        }

        CinemaEntity cinema = cinemaSessionBean.getCinemaById(cinemaId);
        return cinema.getHalls();
    }
}
