package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Member {

    protected Member() {

    }

    public Member(String username) {
        this.username = username;
    }

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;



}
