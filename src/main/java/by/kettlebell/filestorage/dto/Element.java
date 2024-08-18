package by.kettlebell.filestorage.dto;

import by.kettlebell.filestorage.validator.annotation.status.ValidEnum;
import jakarta.validation.constraints.NotNull;

import lombok.*;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Element implements Comparable<Element> {

    private Long userId;
    private String name;
    @ValidEnum(enumClass = Status.class)
    private Status status;
    private String path;

    @Override
    public int compareTo(@NotNull Element o) {
        if ((this.getStatus().ordinal()) == (o.getStatus().ordinal())) {
            return (this.getName()).compareTo(o.getName());
        } else if (this.getStatus().ordinal() < o.getStatus().ordinal()) {
            return -1;
        } else return 1;
    }
}
