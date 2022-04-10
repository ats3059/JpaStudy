package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try{
            Team team = new Team();
            team.setName("TeamA");
//            em.persist(team);
            for(int i = 0; i < 11; i++){
                Member member = new Member();
                member.setUsername("Member"+i);
                member.setAge(i);
                member.setTeam(team);
//                em.persist(member);
                team.createCascade(member);
            }
            em.persist(team);


            List<Member> result = em.createQuery("select m from Member m join m.team t on m.username = 'Member3'"
                    ,Member.class)
                    .getResultList();
            team.setId(12L);

            System.out.println(result.size());
            for (Member member : result) {
                System.out.println(member.getUsername());
                System.out.println(member.getTeam().getName());
            }

            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
            tx.rollback();
        }finally {
            emf.close();
        }





    }

}
