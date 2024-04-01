package com.code.example.common.model;


import java.io.Serializable;

/**
 * 用户实体类
 *
 * @author Liang
 * @create 2024/3/14
 */
public class User implements Serializable {
    private String name;

    public User(String name) {
        this.name = name;
    }

    public User() {
        this.name = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
