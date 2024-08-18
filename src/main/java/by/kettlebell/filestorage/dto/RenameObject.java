package by.kettlebell.filestorage.dto;

import by.kettlebell.filestorage.validator.annotation.status.ValidEnum;
import lombok.*;

@Setter
@Getter
@ToString
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RenameObject {
    private Long userId;
    private String pathCurrent;
    private String newName;
    private String oldName;
    @ValidEnum(enumClass = Status.class)
    private Status status;
}
