package stud.team.pwsbackend.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "voteAdd")
@Data
public class VoteAdd {

    @Id
    @Column(name = "id_vote")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVote;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_add_video")
    private Video addVideo;

    @Column(name = "number_prev_video")
    private Integer numberPrevVideo;
}
