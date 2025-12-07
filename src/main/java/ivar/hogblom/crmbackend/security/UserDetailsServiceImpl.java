package ivar.hogblom.crmbackend.security;

import ivar.hogblom.crmbackend.system.entity.userEntityAndRole.Role;
import ivar.hogblom.crmbackend.system.entity.userEntityAndRole.UserEntity;
import ivar.hogblom.crmbackend.system.repository.userEntityAndRole.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserEntityRepository userEntityRepository;

    @Autowired
    public UserDetailsServiceImpl(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    //UserDetails är gränssnittet, en implementation är klassen User. Här returneras klassen User.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        /*
            User-objektet har formen:

            principal = {
                username = "alice",
                password = "$2a$10$abcd...xyz",
                authorities = [
                    SimpleGrantedAuthority("ROLE_USER")
                ],
                accountNonExpired = true,
                accountNonLocked = true,
                credentialsNonExpired = true,
                enabled = true
            }
         */
        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                mapRolesToAuthorities(userEntity.getRoles()
                )
        );

    }
    //Hjälpfunktion till loadUserByUsername ovan.
    private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
