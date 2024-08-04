package by.kettlebell.filestorage.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
//@EnableMethodSecurity
public class SecurityConfig {

    //private final UserDetailsService userDetailsService;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//    @Bean
//    public AuthenticationManager authManager(UserDetailsService detailsService) {
//        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
//        daoProvider.setUserDetailsService(detailsService);
//        daoProvider.setPasswordEncoder(passwordEncoder());
//        return new ProviderManager(daoProvider);
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/registration", "/login", "/css/**","/image/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/breadcrumb", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login"));
        return http.build();
    }
//    public SecurityConfig(UserDetailsService userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }

}
/*

    private final ApplicationContext applicationContext;

    private final UserService userService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers("/","/login","/user/registration" ,"/user","/breadcrumb"*/
/*"api/v1/apps/welcome", "api/v1/apps/new-user","/","/m","/l"*//*
).permitAll()
                        .requestMatchers("/api/v2/greetings","/api/v1/greetings","/api/v3/greetings").authenticated())
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
 */
/*               .formLogin(login-> login
                        .defaultSuccessUrl("/api/v2/greetings")
//                        .successForwardUrl("/api/v2/greetings")
                        .loginPage("/login"))*//*


            */
/*    .logout(logout-> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID"))*//*

                .build();
    }
//-------------- не помню за чем это----------------
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    //------------------------------------------------------------

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
*/



//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new UserService();
//    }

//    @Bean
//    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
//        UserDetails user = User.withUsername("admin")
//                .password(passwordEncoder.encode("password"))
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.httpBasic(Customizer.withDefaults())
//                /*.sessionManagement(httpSecuritySessionManagementConfigurer ->
//                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))*/
//                /*.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers("/")
//                        .hasRole("ADMIN")
//                        .anyRequest()
//                        .authenticated())*//*.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
//                    httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint((request, response, authException) -> {
//                        authException.printStackTrace();
//                        response.sendError(HttpStatus.UNAUTHORIZED.value());
//                    });
//                })*/;
//        return http.build();
//        //       return http.httpBasic(Customizer.withDefaults()).build();
//    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

//    @Bean
//    public SpringResourceTemplateResolver templateResolver(){
//        // SpringResourceTemplateResolver automatically integrates with Spring's own
//        // resource resolution infrastructure, which is highly recommended.
//        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
//        templateResolver.setApplicationContext(this.applicationContext);
//        templateResolver.setPrefix("/WEB-INF/templates/");
//        templateResolver.setSuffix(".html");
//        // HTML is the default value, added here for the sake of clarity.
//        templateResolver.setTemplateMode(TemplateMode.HTML);
//        // Template cache is true by default. Set to false if you want
//        // templates to be automatically updated when modified.
//        templateResolver.setCacheable(true);
//        return templateResolver;
//    }

//    @Bean
//    public SpringTemplateEngine templateEngine(){
//        // SpringTemplateEngine automatically applies SpringStandardDialect and
//        // enables Spring's own MessageSource message resolution mechanisms.
//        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
//        templateEngine.setTemplateResolver(templateResolver());
//        // Enabling the SpringEL compiler with Spring 4.2.4 or newer can
//        // speed up execution in most scenarios, but might be incompatible
//        // with specific cases when expressions in one template are reused
//        // across different data types, so this flag is "false" by default
//        // for safer backwards compatibility.
//        templateEngine.setEnableSpringELCompiler(true);
//        return templateEngine;
//    }

//    public ThymeleafViewResolver viewResolver(){
//        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
//        viewResolver.setTemplateEngine(templateEngine());
//        // NOTE 'order' and 'viewNames' are optional
//        viewResolver.setOrder(1);
//        viewResolver.setViewNames(new String[] {".html", ".xhtml"});
//        return viewResolver;
//    }

