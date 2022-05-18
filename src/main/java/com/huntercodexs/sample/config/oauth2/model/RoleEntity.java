package com.huntercodexs.sample.config.oauth2.model;

import lombok.*;

import javax.persistence.*;

@Data
@Setter
@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column
    private String description;

    @Column
    private String name;

}
