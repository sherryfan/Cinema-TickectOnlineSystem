package entity;

import entity.HallEntity;
import entity.MovieEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-05-01T02:27:56")
@StaticMetamodel(ScheduleEntity.class)
public class ScheduleEntity_ { 

    public static volatile SingularAttribute<ScheduleEntity, MovieEntity> movie;
    public static volatile SingularAttribute<ScheduleEntity, HallEntity> hall;
    public static volatile SingularAttribute<ScheduleEntity, LocalDateTime> startTime;
    public static volatile SingularAttribute<ScheduleEntity, Long> id;
    public static volatile SingularAttribute<ScheduleEntity, LocalDateTime> endTime;
    public static volatile SingularAttribute<ScheduleEntity, LocalDate> day;

}