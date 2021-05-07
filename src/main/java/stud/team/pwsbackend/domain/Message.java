package stud.team.pwsbackend.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMessage;

    @Column(unique = true, nullable = false)
    private Long text;

    @ManyToOne
    @JoinColumn(name = "id_user")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;
}