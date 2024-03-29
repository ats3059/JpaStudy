package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    TestMemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testMember() throws Exception{
        //given
        Member member = new Member();
//        member.setUsername("memberA");

        //when
        Long save = memberRepository.save(member);
        Member findMember = memberRepository.find(save);

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
//        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }




}