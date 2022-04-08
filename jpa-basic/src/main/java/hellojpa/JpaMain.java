package hellojpa;

import jdk.swing.interop.SwingInterOpUtils;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
//            Member member = new Member();
//            member.setName("hello");
//            Team team = new Team();
//            team.setTeamName("TeamA");
//            team.setTeamNum("abcdefu");
//            member.setTeam(team);
//            em.persist(team);
//            em.persist(member);
//            em.flush();
//            em.clear();
//
//            Member test = em.find(Member.class , member.getId());
//
//
//            Team team1 = test.getTeam();
//            //1차 캐시에 FK값이 존재함 -> (Member 객체 내부에) 그래서 pk값은 영속컨텍스트에 초기화 값을 요청안함.
//            System.out.println(team1.getTeamName());
//
//            System.out.println("=================================");
//            //1차 캐시 내부에 있는 MEMBER 객체 내부의 TEAM 객체에는 TEAMNAME 값이 존재하지 않음 -> 영속성 컨텍스트에 초기화 요청.
//            //쿼리를 조회해옴.
//            System.out.println(team1.getTeamNum());
//            System.out.println("=================================");


            Parent parent = new Parent();
            parent.setAge("40");
            parent.setName("TEST_PARENT");

            Child child = new Child();
            child.setName("TEST_CHILD");
            Child child2 = new Child();
            child2.setName("TEST_CHILD");
            Child child3 = new Child();
            child3.setName("TEST_CHILD");

            parent.cascadeSaveChild(child);
            parent.cascadeSaveChild(child2);
            parent.cascadeSaveChild(child3);
            em.persist(parent);
            em.flush();
            em.clear();

            em.remove(child);







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
