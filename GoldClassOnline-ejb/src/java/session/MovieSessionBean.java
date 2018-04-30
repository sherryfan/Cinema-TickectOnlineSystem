/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.MovieEntity;
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
public class MovieSessionBean implements MovieSessionBeanLocal {

    @PersistenceContext(unitName = "GoldClassOnline-ejbPU")
    private EntityManager em;
    
    @Override
    public MovieEntity createMovie(MovieEntity movie) throws EntityConflictException {
        if (movie == null || movie.getTitle() == null || movie.getTitle().isEmpty()
                || movie.getRating() == null || movie.getRating().isEmpty()
                || movie.getDuration() == 0 || movie.getImages() == null || movie.getImages().size() < 1) {
            return null;
        }

        Query q = em.createNamedQuery("MovieEntity.findMovieByName")
                .setParameter("title", movie.getTitle());
        if(q.getResultList().isEmpty()) { // name satisfies unique constraint
            MovieEntity newMovie
                    = new MovieEntity(movie.getTitle(), movie.getDuration(), movie.getRating());
            newMovie.setImages(movie.getImages());
            em.persist(newMovie);
            return newMovie;
        }
        // else, name already exist, failed unique constraint
        throw new EntityConflictException("Movie named " + movie.getTitle() + " already exists!");
    }



    @Override
    public MovieEntity updateMovie(MovieEntity movie) throws EntityNotFoundException {
        if (movie == null
                || movie.getRating() == null || movie.getRating().isEmpty()
                || movie.getDuration() == 0) {
            return null;
        }
        
        MovieEntity updatedMovie = this.retrieveMovie(movie.getId());
        updatedMovie.setRating(movie.getRating());
        updatedMovie.setDuration(movie.getDuration());

        em.merge(updatedMovie);
        return updatedMovie;
    }

    // view all movies
    @Override
    public List<MovieEntity> getAllMovies() {
        Query q = em.createQuery("SELECT a FROM MovieEntity a ");
        
        return q.getResultList();
    }

    // view movie details
    @Override
    public MovieEntity retrieveMovie(Long id) throws EntityNotFoundException {
        if (id == null) {
            return null;
        }

        MovieEntity movie = em.find(MovieEntity.class, id);
        if (movie == null) { // id not found
            throw new EntityNotFoundException("Movie " + id + " not found!");
        } else {
            return movie;
        }
    }

    // delete movie
    @Override
    public Boolean deleteMovie(Long id) throws EntityNotFoundException {
        if (id == null) {
            return false;
        }

        MovieEntity movie = this.retrieveMovie(id); // throws exception if id not found in db

        // check if the movie has future schedules
        if (movie.getSchedules().isEmpty()) { 
            em.remove(movie);
            em.flush();
            return true;
        } else {
            return false; // must remove all schedules first
        }
    }
}
