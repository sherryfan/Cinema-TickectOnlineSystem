/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.MovieEntity;
import exception.EntityConflictException;
import exception.EntityNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import session.MovieSessionBeanLocal;

/**
 *
 * @author sherry
 */
@Named(value = "movieManagedBean")
@ViewScoped
public class MovieManagedBean implements Serializable {

    private static final Logger LOGGER
            = Logger.getLogger(MovieManagedBean.class.getName());
    private static ConsoleHandler handler = null;

    private List<MovieEntity> movies = new ArrayList<>();
    private MovieEntity thisMovie;

    //inputs
    private MovieEntity newMovie;
    private UploadedFile movieImage;
    @EJB
    private MovieSessionBeanLocal movieSessionBean;

    /**
     * Creates a new instance of MoviesManagedBean
     */
    public MovieManagedBean() {
        handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(handler);
    }

    @PostConstruct
    public void init() {
        movies = movieSessionBean.getAllMovies();
        newMovie = new MovieEntity();
    }

    public void addMovie() {
        if (newMovie.getImages().size() < 1) {
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please upload movie images", null));
        } else {
            try {
                newMovie = movieSessionBean.createMovie(newMovie);
                movies.add(newMovie);
                newMovie = new MovieEntity();
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('create-movie-dialog').hide();");
            } catch (EntityConflictException e) {
                
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Movie with name already exists.", null));
            }
        }
    }

    public void openViewMovieDialog(int index) {
        thisMovie = movies.get(index);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('view-movie-dialog').show();");
//        LOGGER.log(Level.SEVERE, " open view movie dialog: {0}", selectedMovie.toString());
    }

    public void updateMovie() throws IOException {
        LOGGER.log(Level.FINE, "thisMovie {0}", thisMovie);
        try {
            MovieEntity updatedMovie = movieSessionBean.updateMovie(thisMovie);
            thisMovie = updatedMovie;
            
            if (thisMovie != null) { // success
//                RequestContext context = RequestContext.getCurrentInstance();
//                context.execute("PF('updateMovieDialog').hide();");
                 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Movie updated successfully!", null));
            } else {
                LOGGER.log(Level.SEVERE, "Movie update failed!");
            }
        } catch (EntityNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Movie not found.", null));
        }
    }

    public void deleteMovie() throws IOException {
        LOGGER.log(Level.FINE, "thisMovie {0}", thisMovie);
        try {
            if (movieSessionBean.deleteMovie(thisMovie.getId())) {
                // delete success, update cinemas list
                movies.remove(thisMovie);
                thisMovie = null;
            } else {
                 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Movie deleted!", null));}
        } catch (EntityNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Movie not found.", null));
        }
    }

    public void uploadImage(FileUploadEvent event) throws IOException {
        movieImage = event.getFile();
        String imageName = UUID.randomUUID().toString() + ".png";

        if (movieImage != null) {
            String newFilePath = System.getProperty("user.dir").replace("config", "docroot") + System.getProperty("file.separator");
            OutputStream output = new FileOutputStream(new File(newFilePath, imageName));

            int a;
            int BUFFER_SIZE = 8192;
            byte[] buffer = new byte[BUFFER_SIZE];
            InputStream inputStream = movieImage.getInputstream();
            while (true) {
                a = inputStream.read(buffer);
                if (a < 0) {
                    break;
                }
                output.write(buffer, 0, a);
                output.flush();
            }
            output.close();
            inputStream.close();
            newMovie.getImages().add(imageName);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Image uploaded ", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please upload movie images", null));
        }
    }

    public List<MovieEntity> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieEntity> movies) {
        this.movies = movies;
    }

    public MovieEntity getThisMovie() {
        return thisMovie;
    }

    public void setThisMovie(MovieEntity thisMovie) {
        this.thisMovie = thisMovie;
    }

    public MovieEntity getNewMovie() {
        return newMovie;
    }

    public void setNewMovie(MovieEntity newMovie) {
        this.newMovie = newMovie;
    }

    public UploadedFile getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(UploadedFile movieImage) {
        this.movieImage = movieImage;
    }
    
    

}
