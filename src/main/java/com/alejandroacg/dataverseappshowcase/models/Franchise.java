package com.alejandroacg.dataverseappshowcase.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Franchise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String englishName;

    @Column(nullable = false, unique = true)
    private String originalName;

    @Column(length = 2000)
    private String description;

    @Column(name = "profile_image_path")
    private String profileImagePath;

    @Column(nullable = false)
    private boolean upToDate = false;
}
