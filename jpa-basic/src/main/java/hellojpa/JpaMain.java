package hellojpa;

import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            Member member = new Member();
            member.setName("hello");
            Team team = new Team();
            team.setTeamName("TeamA");
            member.setTeam(team);
            em.persist(team);
            em.persist(member);
            em.flush();
            em.clear();

            Member test = em.find(Member.class , member.getId());
            System.out.println(test.getTeam().getClass());
            Team team1 = test.getTeam();
            Hibernate.initialize(team1);
            System.out.println("=================================");
            System.out.println(team1.getTeamName());








            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
            tx.rollback();
        }finally {
            em.close();

        }
        emf.close();



    }



}
