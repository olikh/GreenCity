package greencity.entities;

import greencity.entities.enums.ROLE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @OneToOne(mappedBy = "user")
    private Photo photo;

    @Enumerated(value = EnumType.ORDINAL)
    @Column(nullable = false)
    private ROLE role;

    private Boolean isBanned = false;

    @Column(nullable = false)
    private LocalDateTime lastVisit;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<FavoritePlace> favoritePlaces = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private UserSecurity userSecurity;

    @OneToMany(mappedBy = "user")
    private List<Rate> rates = new ArrayList<>();

}
