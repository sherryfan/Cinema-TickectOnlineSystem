package entity;

import entity.ScheduleEntity;
import java.util.List;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-04-30T21:12:27")
@StaticMetamodel(MovieEntity.class)
public class MovieEntity_ { 

    public static volatile SingularAttribute<MovieEntity, Integer> duration;
    public static volatile SingularAttribute<MovieEntity, List> images;
    public static volatile ListAttribute<MovieEntity, ScheduleEntity> schedules;
    public static volatile SingularAttribute<MovieEntity, String> rating;
    public static volatile SingularAttribute<MovieEntity, Long> id;
    public static volatile SingularAttribute<MovieEntity, String> title;

}