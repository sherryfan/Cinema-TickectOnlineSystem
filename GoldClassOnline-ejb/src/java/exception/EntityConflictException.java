/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception;

/**
 *
 * @author sherry
 */
public class EntityConflictException extends Exception {
    
    public EntityConflictException(String message) {
        super(message);
    }
}