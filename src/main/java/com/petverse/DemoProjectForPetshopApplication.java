package com.petverse;

import com.petverse.entity.enums.Gender;
import com.petverse.payload.request.user.UserRequest;
import com.petverse.service.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class DemoProjectForPetshopApplication {

    private final UserService userService;

    public DemoProjectForPetshopApplication(UserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
		SpringApplication.run(DemoProjectForPetshopApplication.class, args);
	}

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {

            if (userService.countAllAdmin() == 0) {

                UserRequest admin = UserRequest.builder()
                        .firstName("Admin")
                        .lastName("System")
                        .email("admin@petverse.com")
                        .address("İstanbul")
                        .gender(Gender.MALE)
                        .phoneNumber("+4915123456789")
                        .password("Admin123!")
                        .isActive(true)
                        .isDeleted(false)
                        .build();

                userService.saveAdmin(admin, "Admin");
            }
        };
    }
}
