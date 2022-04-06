package hellojpa;

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

            Member member2 = em.find(Member.class,1L);
            System.out.println(member2.getName());
            List<Member> list = member2.getTeam().getList();
            for (Member member1 : list) {
                System.out.println(member1.getName());
            }




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
