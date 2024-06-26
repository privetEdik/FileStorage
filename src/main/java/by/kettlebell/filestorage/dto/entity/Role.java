package by.kettlebell.filestorage.dto.entity;

/*import org.springframework.security.core.GrantedAuthority;*/

public enum Role /*implements GrantedAuthority*/ {
    USER, ADMIN;

//    @Override
//    public String toString() {
//        return super.toString();
//    }

    //@Override
    public String getAuthority() {
        return name();
    }
}
