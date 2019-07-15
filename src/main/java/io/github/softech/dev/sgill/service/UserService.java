package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.config.Constants;
import io.github.softech.dev.sgill.domain.*;
import io.github.softech.dev.sgill.domain.enumeration.TYPES;
import io.github.softech.dev.sgill.repository.*;
import io.github.softech.dev.sgill.repository.search.CompanySearchRepository;
import io.github.softech.dev.sgill.repository.search.CustomerSearchRepository;
import io.github.softech.dev.sgill.repository.search.ServicelistSearchRepository;
import io.github.softech.dev.sgill.repository.search.UserSearchRepository;
import io.github.softech.dev.sgill.security.AuthoritiesConstants;
import io.github.softech.dev.sgill.security.SecurityUtils;
import io.github.softech.dev.sgill.service.dto.UserDTO;
import io.github.softech.dev.sgill.service.util.RandomUtil;
import io.github.softech.dev.sgill.web.rest.errors.InvalidPasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserSearchRepository userSearchRepository;

    private final CustomerRepository customerRepository;

    private final CustomerSearchRepository customerSearchRepository;

    private final CompanyRepository companyRepository;

    private final CompanySearchRepository companySearchRepository;

    private final ServicelistRepository servicelistRepository;

    private final ServicelistSearchRepository servicelistSearchRepository;

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserSearchRepository userSearchRepository, AuthorityRepository authorityRepository, CacheManager cacheManager,
                       CustomerRepository customerRepository, CustomerSearchRepository customerSearchRepository, CompanyRepository companyRepository, CompanySearchRepository companySearchRepository,
                           ServicelistRepository servicelistRepository,ServicelistSearchRepository servicelistSearchRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSearchRepository = userSearchRepository;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
        this.customerRepository = customerRepository;
        this.customerSearchRepository = customerSearchRepository;
        this.companyRepository = companyRepository;
        this.companySearchRepository = companySearchRepository;
        this.servicelistRepository = servicelistRepository;
        this.servicelistSearchRepository = servicelistSearchRepository;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                Set<Authority> authorities = new HashSet<>();
                authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
                user.setAuthorities(authorities);
                userSearchRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Activated user: {}", user);
                log.debug("Activated user role: {}", authorities);
                return user;
            });
    }

    public void clearUserCache(User user){
        this.clearUserCaches(user);
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
       log.debug("Reset user password for reset key {}", key);

       return userRepository.findOneByResetKey(key)
           .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
           .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                this.clearUserCaches(user);
                return user;
           });
    }

    public Optional<User> requestPasswordReset(String mail, String login) {
        //return userRepository.findOneByEmailIgnoreCase(mail)
        return userRepository.findOneByLogin(login)
            .filter(User::getActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                this.clearUserCaches(user);
                return user;
            });
    }

    public User registerUser(UserDTO userDTO, String password, String phone, String streetaddr, String postal, String city,
                             String state, String country, int month, String birth, String license
                            , TYPES specialities, String trades, String areaserviced, Company company, Boolean show) {

        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setEmail(userDTO.getEmail());
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        userSearchRepository.save(newUser);
        this.clearUserCaches(newUser);
        log.debug("Created Information for User: {}", newUser);

        Customer customer = new Customer();
        customer.setUser(newUser);
        customer.setPhone(phone);
        customer.setNormalized(userDTO.getFirstName() + " "+ userDTO.getLastName());
        customer.setStreetaddress(streetaddr);
        customer.setPostalcode(postal);
        customer.setCity(city);
        customer.setStateProvince(state);
        customer.setCountry(country);
        customer.setLicenceCycle(month);
        customer.setPoints(0);
        customer.setRegistered(Instant.now());
        customer.setLastactive(Instant.now());
        customer.setMonthYear(birth);
        customer.setLicenceNumber(license);
        customer.setSpecialities(specialities);
        customer.setTrades(trades);
        customer.setHidden(customer.getNormalized() + "|" + customer.getLicenceNumber() + "|" + customer.getPhone()
            + "|" + customer.getLicenceCycle() + "|" +customer.getMonthYear());
        customer.setAreaserviced(areaserviced);
        //Company reqdCompany = companyRepository.findById(id).get();
        customer.setCompany(company);
        customer.setShow(show);
        customerRepository.save(customer);
        customerSearchRepository.save(customer);

        if(show){
            Servicelist servicelist = new Servicelist();
            servicelist.setName(userDTO.getFirstName() + " "+ userDTO.getLastName());
            servicelist.setSpeciality(specialities.toString());
            servicelist.setTrades(trades);
            servicelist.setAreas(areaserviced);
            servicelist.setCustomer(customer);
            servicelist.setPhone(phone);
            servicelist.setEmail(newUser.getEmail());
            servicelist.setCompany(company.getName());
            servicelist.setUrl(company.getUrl());
            servicelistRepository.save(servicelist);
            servicelistSearchRepository.save(servicelist);
        }

        return newUser;
    }

    public User getUserbyEmail(String email){
        return userRepository.findUserByEmail(email);
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO.getAuthorities().stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        userRepository.save(user);
        userSearchRepository.save(user);
        this.clearUserCaches(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user
     * @param lastName last name of user
     * @param email email id of user
     * @param langKey language key
     * @param imageUrl image URL of user
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                userSearchRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
            });
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update
     * @return updated user
     */
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository
            .findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                this.clearUserCaches(user);
                user.setLogin(userDTO.getLogin());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setEmail(userDTO.getEmail());
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO.getAuthorities().stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);
                userSearchRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(UserDTO::new);
    }

    public void deleteUser(String login) {
        User user = userRepository.findOneByLogin(login).get();
        Customer customer = customerRepository.findCustomersByUser(user);

        Boolean isShow = customer.isShow();
        if(customer.getId() == null) {
            userRepository.delete(user);
            userSearchRepository.delete(user);
            this.clearUserCaches(user);
            log.debug("Deleted User: if no customer {}", user);
        } else {
            Servicelist listing = servicelistRepository.findServicelistByCustomer(customer);
            customerRepository.delete(customer);
            customerSearchRepository.delete(customer);
            if(isShow && listing != null) {
                servicelistRepository.delete(listing);
                servicelistSearchRepository.delete(listing);
            }
            userRepository.delete(user);
            userSearchRepository.delete(user);
            //this.clearUserCaches(user);
            log.debug("Deleted User: if customer {}", user);
        }
    }

    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                this.clearUserCaches(user);
                log.debug("Changed password for User: {}", user);
            });
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(Long id) {
        return userRepository.findOneWithAuthoritiesById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
            userSearchRepository.delete(user);
            this.clearUserCaches(user);
        }
    }

    /**
     * @return a list of all the authorities
     */
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    public void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
    }
}
