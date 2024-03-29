package study.querydsl.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
public class MemberSearchCondition {

    //회원명 , 팀명 , 나이(ageGoe, ageLoe)

    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
    List<OrderCondition> order = new ArrayList<>();


}
