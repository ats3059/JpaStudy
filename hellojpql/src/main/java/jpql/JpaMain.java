package jpql;

import javax.persistence.*;
import java.util.List;

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

            // N+1 상황 jpql 사용하여 멤버만 가져왔을 때 해당 멤버 엔티티 내부의 팀은 현재 프록시 ( 레이지로딩 상태 )
//            List<Member> memberList = em.createQuery("select m from Member m", Member.class).getResultList();
//            for (Member member : memberList) {
//                System.out.println("member = " + member.getUsername() + " : " + "TeamName = " + member.getTeam().getName());
//            }
            // N+1 해결방안 fetch 조인 사용 fetch 조인을 사용하게 된다면 해당 연관관계에 있는 데이터들을 전부 퍼올린다.
            //여기까지가 N:1 관계
//            List<Member> memberList = em.createQuery("select m from Member m join fetch m.team", Member.class).getResultList();
//            for (Member member : memberList) {
//                System.out.println("member = " + member.getUsername() + " : " + "TeamName = " + member.getTeam().getName());
//            }


            // 컬렉션 패치조인 시 문제상황
//            List<Team> resultList = em.createQuery("select t from Team t join fetch t.member", Team.class).getResultList();
            // 분명 팀은 3개인데 12개가 나오게된다.
//            System.out.println(resultList.size());

            // 1:N 관계에서는 데이터가 뻥튀기가 되는데 , 이때 distinct 를 사용하면 해결된다
            // 이유는 jpa에서 일단 연관관계에 있는 데이터들( 뻥튀기된 데이터 )을 전부 퍼올려서 애플리케이션에서 같은 pk를 가진
            // 1에 해당하는 엔티티의 컬렉션 내부에 N관계의 데이터들을 전부 넣어주기 때문 ( distinct 사용 시 영속성 컨텍스트에서 pk가 같다면 지워준다 )
            // + sql 자체에 distinct 들어감
            List<Team> distinctList = em.createQuery("select distinct t from Team t join fetch t.member", Team.class).getResultList();
            //제대로 3개 출력
            System.out.println(distinctList.size());

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
