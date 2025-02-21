package Seoul_Milk.sm_server.login.dto;

import Seoul_Milk.sm_server.login.entity.MemberEntity;
import java.util.ArrayList;
import java.util.Collection;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final MemberEntity memberEntity;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return memberEntity.getRole().toString();
            }
        });
        return collection;
    }

    public String getPassword() {
        return memberEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return null;
    }

    public String getEmployeeId() {
        return memberEntity.getEmployeeId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
