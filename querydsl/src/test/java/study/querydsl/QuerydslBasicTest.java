package study.querydsl;

import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.querydsl.jpa.JPAExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;

import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }


    @Test
    public void startJPQL() {
        //member1을 찾아라
        Member findMember = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {


        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("memeber1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        Member findMember = queryFactory.selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(10)))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam() {
        Member findMember = queryFactory.selectFrom(member)
                .where(
                        member.username.eq("member1")
                        , member.age.eq(10)
                )
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void resultFetch() {
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        Member fetchOne = queryFactory
                .selectFrom(QMember.member)
                .fetchOne();

        Member member = queryFactory
                .selectFrom(QMember.member)
                .fetchFirst();


    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력 nulls last
     */

    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    public void paging1() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();
    }

    @Test
    public void paging2() {
        PageRequest of = PageRequest.of(1, 2);
        List<Member> content = queryFactory.selectFrom(member)
                .orderBy(member.username.desc())
                .offset(of.getOffset())
                .limit(of.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member);

        Page<Member> page = PageableExecutionUtils.getPage(content, of, countQuery::fetchOne);

        page.getContent().stream().forEach(System.out::println);

    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구해라.
     *
     * @throws Exception
     */
    @Test
    public void group() throws Exception {
        //given

        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);

    }

    /**
     * 팀 A에 소속된 모든 회원
     */
    @Test
    public void join() throws Exception {

        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    /**
     * cross 조인
     */
    @Test
    public void theta_join() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> result = queryFactory.select(member)
                .from(member, team)
                .where(team.name.eq(member.username))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");

    }

    /**
     * 예 ) 회원과 팀을 조인하면서 , 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL
     * select m , t from Member m
     * left join m.team t
     * on t.name = "teamA"
     */
    @Test
    public void join_on_filtering() throws Exception {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();


        for (Tuple tuple : result) {
            System.out.println(tuple);
        }

    }

    /**
     * 연관관계 없는 엔티티 외부 조인
     * 회원의 이름이 팀 이름과 같은 대상 외부 조인
     */
    @Test
    public void join_on_no_relation() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> result = queryFactory.select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println(tuple);
        }

    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void fetchJoinNo() throws Exception {
        em.flush();
        em.clear();
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .fetch();


        emf.getPersistenceUnitUtil().isLoaded(fetch);

    }


    @Test
    public void fetchJoinYes() throws Exception {
        em.flush();
        em.clear();

        List<Member> fetch = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .fetch();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(fetch);
        System.out.println("loaded = " + loaded);

        em.clear();

    }

    /**
     * 나이가 가장 평균이상인 회원
     */
    @Test
    public void subQueryGoe() throws Exception {
        em.flush();
        em.clear();

        QMember maxAge = new QMember("tem");

        List<Member> fetch = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        select(maxAge.age.avg()).from(maxAge)
                )).fetch();

        assertThat(fetch).extracting("age").containsExactly(30, 40);
    }


    /**
     * 나이가 가장 평균이상인 회원
     */
    @Test
    public void subQueryIn() throws Exception {
        em.flush();
        em.clear();

        QMember maxAge = new QMember("tem");

        List<Member> fetch = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        select(maxAge.age.avg()).from(maxAge)
                )).fetch();

        assertThat(fetch).extracting("age").containsExactly(30, 40);
    }

    @Test
    public void selectSubQuery() {
        QMember memberSub = new QMember("memberSub");

        List<Tuple> result = queryFactory
                .select(member.username,
                        select(memberSub.age.avg())
                                .from(memberSub)
                )
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println(tuple);
        }

    }

    @Test
    public void simpleCaseQuery() throws Exception {
        List<String> result = queryFactory
                .select(
                        member.age
                                .when(10).then("10살")
                                .when(20).then("20살")
                                .otherwise("기타")
                ).from(member).fetch();
        for (String s : result) {
            System.out.println(s);
        }
    }

    @Test
    public void complexCaseQuery() throws Exception {
        List<String> result = queryFactory
                .select(
                       new CaseBuilder()
                           .when(member.age.between(10,20)).then("10~20")
                           .when(member.age.between(21,30)).then("21~30")
                            .otherwise("기타")
                ).from(member).fetch();

        for (String s : result) {
            System.out.println(s);
        }

    }

    @Test
    public void findDtoByJPQL() throws Exception{
        List<MemberDto> result = em.createQuery("select new study.querydsl.dto.MemberDto(m.username,m.age) from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }


    @Test
    public void findDtoBySetter() throws Exception{

        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class, member.username, member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println(memberDto);
        }

    }


    @Test
    public void findDtoByField() throws Exception{
        QMember subMember = new QMember("subMember");

        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class, member.username.as("username"),
                        ExpressionUtils.as(
                                JPAExpressions
                                    .select(subMember.age.max())
                                    .from(subMember)
                                ,"age")
                ))
                .from(member)
                .fetch();
        for (UserDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }

    }

    @Test
    public void findDtoByConstructor(){
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class, member.username, member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }

    }

    @Test
    public void findDtoByQueryProjection(){
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();
    }

    @Test
    public void dynamicQuery_BooleanBuilder(){
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam , ageParam);
        assertThat(result.size()).isEqualTo(1);

    }

    private List<Member> searchMember1(String usernameCond, Integer ageCond) {

        BooleanBuilder builder = new BooleanBuilder();

        if(usernameCond != null){
            builder.and(member.username.eq(usernameCond));
        }
        if(ageCond != null){
            builder.and(member.age.eq(ageCond));
        }

        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    @Test
    public void dynamicQuery_WhereParam() throws Exception{
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember2(usernameParam , ageParam);
        assertThat(result.size()).isEqualTo(1);


    }

    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return queryFactory
                .selectFrom(member)
                .where(usernameEq(usernameCond), ageCondEq(ageCond))
                .fetch();
    }

    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond == null ? null : member.username.eq(usernameCond);
    }

    private BooleanExpression ageCondEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null ;
    }


    @Test
    public void bulkUpdate() throws Exception{
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();


    }

    @Test
    public void bulkAdd(){
        long count = queryFactory
                .update(member)
                .set(member.age, member.age.add(1))
                .where(member.age.lt(28))
                .execute();
    }

    @Test
    public void bulkDelete(){
        queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();
    }


    @Test
    public void sqlFunction() throws Exception{

        List<String> result = queryFactory
                .select(Expressions.stringTemplate(
                        "function('replace',{0},{1},{2})"
                        , member.username, "member", "M")
                ).from(member)
                .fetch();

    }

    @Test
    public void sqlFunction2(){
        List<String> result = queryFactory
                .select(Expressions.stringTemplate(
                        "function('lower',{0})"
                        , member.username)
                ).from(member)
                .fetch();
    }


}
