package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    //도메인 클래스 컨버터 딱히 권장하지 않음
    //외부에 pk 공개한다..? 별로 좋지않음
    //이렇게 단순한 경우는 잘 없다.
    //영속성 컨텍스트의 개념이 애매해다 (트랜잭션의 범위가 ..????잘모르겠음)
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(Pageable pageable){
        Page<Member> page = memberRepository.findAll(pageable);
        Page<MemberDto> memberDtos = page.map(mem -> new MemberDto(mem.getId(), mem.getUsername()));

        return memberDtos;
    }


    @PostConstruct
    public void init(){
        for(int i = 0; i < 100; i++){
            memberRepository.save(new Member("user"+i,i));
        }
    }

}
