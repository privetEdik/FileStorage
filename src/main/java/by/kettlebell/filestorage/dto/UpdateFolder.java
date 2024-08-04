package by.kettlebell.filestorage.dto;

import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFolder {
    private Long userId;
    private String pathCurrent;
    private String newName;
    private String oldName;
}
