package entity;

import entity.CinemaEntity;
import entity.ScheduleEntity;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-05-01T02:27:56")
@StaticMetamodel(HallEntity.class)
public class HallEntity_ { 

    public static volatile SingularAttribute<HallEntity, Integer> number;
    public static volatile SingularAttribute<HallEntity, Integer> numOfCols;
    public static volatile SingularAttribute<HallEntity, Integer[][]> seatPlan;
    public static volatile ListAttribute<HallEntity, ScheduleEntity> schedules;
    public static volatile SingularAttribute<HallEntity, Long> id;
    public static volatile SingularAttribute<HallEntity, CinemaEntity> cinema;
    public static volatile SingularAttribute<HallEntity, Integer> numOfRows;

}