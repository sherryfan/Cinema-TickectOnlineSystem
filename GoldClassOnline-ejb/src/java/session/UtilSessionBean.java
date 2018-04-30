/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.StaffEntity;
import exception.InvalidLoginCredentialException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author sherry
 */
@Stateless
public class UtilSessionBean implements UtilSessionBeanLocal {

    @PersistenceContext(unitName = "GoldClassOnline-ejbPU")
    private EntityManager em;
    
    private static final Logger LOGGER = Logger.getLogger("UtilSessionBeanLog");
    private static ConsoleHandler handler = null;

    public UtilSessionBean() {
        handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(handler);
    }

    
    @Override
    public StaffEntity staffDoLogin(String account, String password) throws InvalidLoginCredentialException {
        if (account == null || password == null || account.isEmpty() || password.isEmpty()) {
            return null;
        }

        Query q = em.createQuery("Select s From StaffEntity s Where s.account=:account");
        q.setParameter("account", account);

        try {
            StaffEntity staff = (StaffEntity) q.getSingleResult();

            if (password.equals(staff.getPassword())) {
                return staff;
            } else {
                LOGGER.log(Level.SEVERE, "incorrect password");

                throw new InvalidLoginCredentialException("Incorrect password.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "no user");
            throw new InvalidLoginCredentialException("Invalid password or account name.");
        }
    }
}
