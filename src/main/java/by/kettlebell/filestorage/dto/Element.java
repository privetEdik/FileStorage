package by.kettlebell.filestorage.dto;

import jakarta.validation.constraints.NotBlank;
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
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private Status status;
    @NotNull
    @NotBlank
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
