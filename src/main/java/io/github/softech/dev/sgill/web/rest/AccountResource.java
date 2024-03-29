package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;

import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.User;
import io.github.softech.dev.sgill.repository.CustomerRepository;
import io.github.softech.dev.sgill.repository.UserRepository;
import io.github.softech.dev.sgill.security.SecurityUtils;
import io.github.softech.dev.sgill.service.MailService;
import io.github.softech.dev.sgill.service.UserService;
import io.github.softech.dev.sgill.service.dto.UserDTO;
import io.github.softech.dev.sgill.web.rest.errors.*;
import io.github.softech.dev.sgill.web.rest.vm.KeyAndPasswordVM;
import io.github.softech.dev.sgill.web.rest.vm.ManagedUserVM;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import io.github.softech.dev.sgill.service.dto.PasswordChangeDTO;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    private CustomerRepository customerRepository;

    public AccountResource(UserRepository userRepository, UserService userService, MailService mailService, CustomerRepository customerRepository) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.customerRepository = customerRepository;
    }

    /**
     * POST  /register : register the user.
     *
     * @param managedUserVM the managed user View Model
     * @throws InvalidPasswordException 400 (Bad Request) if the password is incorrect
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already used
     * @throws LoginAlreadyUsedException 400 (Bad Request) if the login is already used
     */

    @PostMapping("/register")
    @Timed
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        log.debug(managedUserVM.toString());
        userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase()).ifPresent(u -> {throw new LoginAlreadyUsedException();});
        // userRepository.findOneByEmailIgnoreCase(managedUserVM.getEmail()).ifPresent(u -> {throw new EmailAlreadyUsedException();});
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword(), managedUserVM.getPhone(), managedUserVM.getStreetaddress(),
            managedUserVM.getPostalcode(), managedUserVM.getCity(), managedUserVM.getStateProvince(),managedUserVM.getCountry(), managedUserVM.getLicenceCycle(),
            managedUserVM.getMonthYear(), managedUserVM.getLicenseNumber(), managedUserVM.getSpecialities(),managedUserVM.getTrades(),managedUserVM.getAreaserviced(),
            managedUserVM.getCompany(),managedUserVM.isShow());
        mailService.sendActivationEmail(user);
    }

    /*
    @PostMapping("/register")
    @Timed
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase()).ifPresent(u -> {throw new LoginAlreadyUsedException();});
        userRepository.findOneByEmailIgnoreCase(managedUserVM.getEmail()).ifPresent(u -> {throw new EmailAlreadyUsedException();});
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword(), managedUserVM.getPhone(), managedUserVM.getStreetaddress(), managedUserVM.getPostalcode(),
            managedUserVM.getCity(), managedUserVM.getStateProvince(), managedUserVM.getCountry(), managedUserVM.getProfilePic(), managedUserVM.getProfilePicContentType(),
            managedUserVM.getCycledate(), managedUserVM.getAreaserviced(), managedUserVM.getSpecialities(), managedUserVM.getTrades(), managedUserVM.getMonthYear(),
            managedUserVM.getLicenseNumber(), managedUserVM.getCompany());
        mailService.sendActivationEmail(user);
    }*/

    /**
     * GET  /activate : activate the registered user.
     *param
     * @param key the activation key
     * @throws RuntimeException 500 (Internal Server Error) if the user couldn't be activated
     */
    @GetMapping("/activate")
    @Timed
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new InternalServerErrorException("No user was found for this activation key");
        }
    }

    /**
     * GET  /authenticate : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request
     * @return the login if the user is authenticated
     */
    @GetMapping("/authenticate")
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /account : get the current user.
     *
     * @return the current user
     * @throws RuntimeException 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/account")
    @Timed
    public UserDTO getAccount() {
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        User tempUser = userRepository.findOneWithAuthoritiesByLogin(login).orElse(null);
        if(login == null || tempUser.getId() == null) {
            return new UserDTO();
        } else {
            return new UserDTO(userRepository.findOneWithAuthoritiesByLogin(login).get());
        }
        /*return userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new InternalServerErrorException("User could not be found"));*/
    }

    /**
     * POST  /account : update the current user information.
     *
     * @param userDTO the current user information
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already used
     * @throws RuntimeException 500 (Internal Server Error) if the user login wasn't found
     */
    @PostMapping("/account")
    @Timed
    public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
        final String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new InternalServerErrorException("User could not be found");
        }
        userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
            userDTO.getLangKey(), userDTO.getImageUrl());
   }

    /**
     * POST  /account/change-password : changes the current user's password
     *
     * @param passwordChangeDto current and new password
     * @throws InvalidPasswordException 400 (Bad Request) if the new password is incorrect
     */
    @PostMapping(path = "/account/change-password")
    @Timed
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
   }

    /**
     * POST   /account/reset-password/init : Send an email to reset the password of the user
     *
     * @param mail the mail of the user
     * @throws EmailNotFoundException 400 (Bad Request) if the email address is not registered
     */
    @PostMapping(path = "/account/reset-password/{login}/init/{mail}")
    @Timed
    public void requestPasswordReset(@PathVariable String mail, @PathVariable String login) {
       mailService.sendPasswordResetMail(
           userService.requestPasswordReset(mail, login).orElseThrow(RuntimeException::new)
       );
    }

    /**
     * POST   /account/reset-password/finish : Finish to reset the password of the user
     *
     * @param keyAndPassword the generated key and the new password
     * @throws InvalidPasswordException 400 (Bad Request) if the password is incorrect
     * @throws RuntimeException 500 (Internal Server Error) if the password could not be reset
     */
    @PostMapping(path = "/account/reset-password/finish")
    @Timed
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user =
            userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new InternalServerErrorException("No user was found for this reset key");
        }
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }

    @GetMapping("/email/contactform/{name}")
    @Timed
    public boolean sendEmail(@PathVariable String name, @RequestParam String email, @RequestParam String msg) {
        String subject = "Contact Form [AceAol-CA] new message from " + name + " reply at " + email;
        mailService.sendEmail("aceaol.helpdesk@gmail.com", subject, msg, false, true);
        return true;
    }
}
