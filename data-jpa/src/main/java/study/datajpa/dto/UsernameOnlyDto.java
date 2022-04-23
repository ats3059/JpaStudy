package study.datajpa.dto;

import lombok.Data;


public class UsernameOnlyDto {
    private String username;
    private int age;

    public UsernameOnlyDto(String username,int age) {
        this.username = username;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

}
