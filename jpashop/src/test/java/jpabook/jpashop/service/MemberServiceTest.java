package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception{
        //given 이런게 주어짐
        Member member = new Member();
        member.setName("kim");

        //when 이렇게 하면
        Long saveId = memberService.join(member);

        //then 이렇게 된다.
        assertEquals(member,memberRepository.findOne(saveId));

    }

    @Test
    public void 중복_회원_예외(){
        //given
        Member member1 = new Member();
        member1.setName("kim1");
        Member member2 = new Member();
        member2.setName("kim1");

        //when
        memberService.join(member1);
        try{
            memberService.join(member2); // 예외가 발생해야 한다 !!
        }catch (IllegalStateException e){
            return;
        }

        //then
        fail("예외가 발생해야 한다.");

    }

}