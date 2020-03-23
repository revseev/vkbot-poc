package com.revseev.vkbotpoc.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "vk_id", unique = true)
    private Integer vkId;

    @Column(name = "age")
    private Integer age;

    @Column(name = "topic")
    private String topic;

}
