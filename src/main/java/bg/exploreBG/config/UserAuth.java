package bg.exploreBG.config;

import bg.exploreBG.querybuilder.UserQueryBuilder;
import bg.exploreBG.service.ExploreBgUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserAuth {
    private final UserQueryBuilder userQueryBuilder;

    public UserAuth(UserQueryBuilder userQueryBuilder) {
        this.userQueryBuilder = userQueryBuilder;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailService() {
        return new ExploreBgUserDetailService(userQueryBuilder);
    }
}
