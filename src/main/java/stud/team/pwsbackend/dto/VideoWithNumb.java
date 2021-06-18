package stud.team.pwsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoWithNumb {

    private Long idVideo;

    private String title;

    private String videoUrl;

    private String previewUrl;
    @NotEmpty
    private Long idUser;

    private Integer numberVideo;
}
