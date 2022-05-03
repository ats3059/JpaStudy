package study.querydsl.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// Order 조건을 위한것.
@Getter
@Setter
@ToString
public class OrderCondition{
    private String column;
    private CustomSort sort;
}
