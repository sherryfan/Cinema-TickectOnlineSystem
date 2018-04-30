/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.StaffEntity;
import exception.InvalidLoginCredentialException;
import javax.ejb.Local;

/**
 *
 * @author sherry
 */
@Local
public interface UtilSessionBeanLocal {

    public StaffEntity staffDoLogin(String account, String password) throws InvalidLoginCredentialException;
    
}
