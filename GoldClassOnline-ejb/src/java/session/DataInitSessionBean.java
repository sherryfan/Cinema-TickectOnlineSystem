/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session.singleton;

import entity.CinemaEntity;
import entity.StaffEntity;
import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author sherry
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @PersistenceContext(unitName = "GoldClassOnline-ejbPU")
    private EntityManager em;


    @PostConstruct
    public void postConstruct() {

        Query q = em.createQuery("SELECT a FROM StaffEntity a "
                + "WHERE a.account = :username ")
                .setParameter("username", "admin1");
        
        if (q.getResultList().isEmpty()) {//no pre-inserted data in DB

            // create staff
            StaffEntity admin1 = new StaffEntity("admin1", "anyone", "ADMIN");
            StaffEntity admin2 = new StaffEntity("admin2", "anyone", "ADMIN");
            StaffEntity operationStaff1 = new StaffEntity("operation1", "anyone", "OPERATION");
            StaffEntity operationStaff2 = new StaffEntity("operation2", "anyone", "OPERATION");

            em.persist(admin1);
            em.persist(admin2);
            em.persist(operationStaff1);
            em.persist(operationStaff2);
            em.flush();
            
            CinemaEntity cinema1 = new CinemaEntity ("GoldClass VivoCity");
            CinemaEntity cinema2 = new CinemaEntity ("GoldClass Central");
            em.persist(cinema1);
            em.persist(cinema2);
            em.flush();
        }
    }

}
