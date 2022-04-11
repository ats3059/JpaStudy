package jpql;

import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team",cascade = CascadeType.ALL)
    @BatchSize(size = 100)
    private List<Member> member = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMember() {
        return member;
    }

    public void createCascade(Member member){
        this.member.add(member);
        member.setTeam(this);
    }
}
