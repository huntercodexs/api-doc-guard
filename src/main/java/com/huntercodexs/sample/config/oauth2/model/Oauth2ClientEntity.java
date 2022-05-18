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
public class Oauth2ClientEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column
    private String client;

    @Column
    private String password;

    @Column
    private int accessTokenValiditySeconds;

    @Column
    private int refreshTokenValiditySeconds;
    
}
