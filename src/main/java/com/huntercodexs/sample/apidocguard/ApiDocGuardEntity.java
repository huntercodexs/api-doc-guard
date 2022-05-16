package com.huntercodexs.sample.apidocguard;

import lombok.*;

import javax.persistence.*;

@Data
@Setter
@Getter
@Builder
@Entity(name = "api_doc_guard")
@AllArgsConstructor
@NoArgsConstructor
public class ApiDocGuardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String username;

    @Column
    String password;

    @Column
    String name;

    @Column
    String email;

    @Column
    String level;

    @Column
    String active;

    @Column
    String createdAt;

    @Column
    String updatedAt;

    @Column
    String deletedAt;

}
