package jpql;

import javax.persistence.*;
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
            Team teamb = new Team();
            teamb.setName("TeamB");
            Team teamc = new Team();
            teamc.setName("TeamC");

            createCascdeTeam(team);
            createCascdeTeam(teamb);
            createCascdeTeam(teamc);

            em.persist(team);
            em.persist(teamb);
            em.persist(teamc);
            em.flush();
            em.clear();

            List<Member> select_m_from_member_m = em.createQuery("select m from Member m where m.id = :userId", Member.class)
                    .setParameter("userId", 2L)
                    .getResultList();
            System.out.println(select_m_from_member_m.size());


//            TypedQuery<Team> query = em.createQuery("select distinct t from Member m join  m.team t", Team.class);
//
//            List<Team> resultList = query.getResultList();
//
//            for (Team mm : resultList) {
//                mm.getMember().forEach(System.out::println);
//            }
//
//            List<Team> result = em.createQuery("select t from Team t"
//                    ,Team.class)
//                    .getResultList();
//
//            for (Team t : result) {
//                List<Member> member = t.getMember();
//                for (Member m : member) {
//                    System.out.println(m + " TeamId =  " + t.getId());
//                }
//
//            }

            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
            tx.rollback();
        }finally {
            emf.close();
        }





    }

    private static void createCascdeTeam(Team team) {
        for(int i = 0; i < 4; i++){
            Member member = new Member();
            member.setUsername("Member"+i);
            member.setAge(i);
            member.setTeam(team);
            team.createCascade(member);
        }
    }

}
