package study.datajpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Team {
    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String username;


}
