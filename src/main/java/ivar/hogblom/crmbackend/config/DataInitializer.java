package ivar.hogblom.crmbackend.config;

import ivar.hogblom.crmbackend.entity.*;
import ivar.hogblom.crmbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner run(RoleRepository roleRepository,
                          UserEntityRepository userEntityRepository,
                          CustomerRepository customerRepository,
                          ResellerRepository resellerRepository,
                          SubscriptionRepository subscriptionRepository,
                          ContractRepository contractRepository
    ) {
        return args -> {
            if (
                    roleRepository.count() == 0 &&
                    userEntityRepository.count() == 0 &&
                    customerRepository.count() == 0 &&
                    resellerRepository.count() == 0 &&
                    subscriptionRepository.count() == 0 &&
                    contractRepository.count() == 0
            ) {
                // 👨‍💻 Create Developers

                System.out.println("Creating initial roles...");
                Role userRole = new Role();
                userRole.setName("ROLE_USER");
                roleRepository.save(userRole);

                Role adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleRepository.save(adminRole);

                System.out.println("Creating initial users...");

                UserEntity puh = new UserEntity(
                        "Nalle_Puh",
                        passwordEncoder.encode("password"),
                        "nalle@puh.com");
                Role admin = roleRepository.findByName("ROLE_ADMIN").get();
                Role user = roleRepository.findByName("ROLE_USER").get();
                puh.setRoles(List.of(admin, user));
                UserEntity savedAdmin = userEntityRepository.save(puh);
                System.out.println("Admin user created with username: " + savedAdmin.getUsername());


                UserEntity nasse = new UserEntity(
                        "Nasse",
                        passwordEncoder.encode("password"),
                        "nasse@puh.com");
                nasse.setRoles(List.of(user));
                UserEntity savedUser1 = userEntityRepository.save(nasse);
                System.out.println("User created with username: " + savedUser1.getUsername());

                UserEntity kanin = new UserEntity(
                        "Kanin",
                        passwordEncoder.encode("password"),
                        "kanin@puh.com");
                kanin.setRoles(List.of(user));
                UserEntity savedUser2 = userEntityRepository.save(kanin);
                System.out.println("User created with username: " + savedUser2.getUsername());

                Customer c1 = new Customer();
                c1.setCompanyName("Nordic IT Solutions AB");
                c1.setOrgNo("559102-8891");
                c1.setContactName("Erik Johansson");
                c1.setContactEmail("erik.johansson@nordicitsolutions.se");
                c1.setContactPhone("+46 72 450 33 12");
                c1.setAddress("Sveavägen 45");
                c1.setCity("Stockholm");
                c1.setZipCode("113 59");
                c1.setCountry("Sverige");
                c1.setIndustry("IT");
                c1.setCustomerType("business");
                c1.setCreatedAt(LocalDate.now());
                c1.setNotes("Prioritetskund. Vill få offert varje år.");


                Customer c2 = new Customer();
                c2.setCompanyName("SkogsTeknik i Småland AB");
                c2.setOrgNo("556722-5510");
                c2.setContactName("Maria Svensson");
                c2.setContactEmail("maria.svensson@skogsteknik.se");
                c2.setContactPhone("+46 76 981 12 45");
                c2.setAddress("Industrigatan 12");
                c2.setCity("Växjö");
                c2.setZipCode("352 46");
                c2.setCountry("Sverige");
                c2.setIndustry("Skogsindustri");
                c2.setCustomerType("business");
                c2.setCreatedAt(LocalDate.now());
                c2.setNotes("Behöver utbildning i samband med installationer.");


                Customer c3 = new Customer();
                c3.setCompanyName("Hav & Kust Konsult AB");
                c3.setOrgNo("556811-9022");
                c3.setContactName("Linnéa Holm");
                c3.setContactEmail("linnea.holm@havkustkonsult.se");
                c3.setContactPhone("+46 70 312 88 05");
                c3.setAddress("Kustvägen 8");
                c3.setCity("Göteborg");
                c3.setZipCode("414 51");
                c3.setCountry("Sverige");
                c3.setIndustry("Konsultverksamhet");
                c3.setCustomerType("business");
                c3.setCreatedAt(LocalDate.now());
                c3.setNotes("Föredrar kontakt via email. Priskänsliga.");


                Customer c4 = new Customer();
                c4.setCompanyName("Friskvårdsteamet Norden AB");
                c4.setOrgNo("559210-3341");
                c4.setContactName("Johan Karlsson");
                c4.setContactEmail("johan.karlsson@friskvardsteamet.se");
                c4.setContactPhone("+46 73 110 55 20");
                c4.setAddress("Kungsgatan 3");
                c4.setCity("Uppsala");
                c4.setZipCode("753 20");
                c4.setCountry("Sverige");
                c4.setIndustry("Hälsa & wellness");
                c4.setCustomerType("business");
                c4.setCreatedAt(LocalDate.now());
                c4.setNotes("Handläggningstid något långsam. Vill ha automatiska rapporter.");

                System.out.println("Creating initial customers...");
                customerRepository.saveAll(List.of(c1, c2, c3, c4));
                System.out.println("4 customers created");

                Reseller r1 = new Reseller();
                r1.setName("TechPartner Sverige AB");
                r1.setOrgNo("556900-1234");
                r1.setActive(true);
                r1.setAddress("Teknikgatan 15, 111 53 Stockholm");
                r1.setContactEmail("kontakt@techpartner.se");
                r1.setContactTelephone("+46 70 845 22 10");
                r1.setInvoiceReference("TP-2025-001");
                r1.setCreatedAt(LocalDate.now());


                Reseller r2 = new Reseller();
                r2.setName("Nordic Security Distribution AB");
                r2.setOrgNo("559311-8832");
                r2.setActive(true);
                r2.setAddress("Hamnvägen 4, 411 27 Göteborg");
                r2.setContactEmail("support@nordicsecurity.se");
                r2.setContactTelephone("+46 76 998 44 55");
                r2.setInvoiceReference("NSD-INV-3345");
                r2.setCreatedAt(LocalDate.now());


                Reseller r3 = new Reseller();
                r3.setName("IT-Partner Stockholm AB");
                r3.setOrgNo("556772-6611");
                r3.setActive(true);
                r3.setAddress("Vikingavägen 9, 182 31 Danderyd");
                r3.setContactEmail("sales@itpartnerstockholm.se");
                r3.setContactTelephone("+46 73 442 19 70");
                r3.setInvoiceReference("STHLM-2025-A");
                r3.setCreatedAt(LocalDate.now());


                Reseller r4 = new Reseller();
                r4.setName("Cloud & Co Konsult AB");
                r4.setOrgNo("559128-5509");
                r4.setActive(true);
                r4.setAddress("Molnvägen 22, 223 63 Lund");
                r4.setContactEmail("info@cloudco.se");
                r4.setContactTelephone("+46 72 552 10 88");
                r4.setInvoiceReference("CLOUD-45-REF");
                r4.setCreatedAt(LocalDate.now());

                System.out.println("Creating initial resellers...");
                resellerRepository.saveAll(List.of(r1, r2, r3, r4));
                System.out.println("4 resellers created");

                Subscription s1 = new Subscription();
                s1.setName("Threat Monitoring Basic");
                s1.setCategory("Threat Monitoring");
                s1.setDescription("Grundläggande övervakning av nätverkstrafik och loggar med varningar vid misstänkt aktivitet.");
                s1.setServiceLevel("Silver (12/5 support)");
                s1.setPricePerMonth(2999);
                s1.setContractLength(12);
                s1.setRenewalPeriod(12);
                s1.setActive(true);
                s1.setSupportContact("support@techpartner.se");
                s1.setCreatedAt(LocalDate.now().minusYears(1));


                Subscription s2 = new Subscription();
                s2.setName("Threat Monitoring Pro");
                s2.setCategory("Threat Monitoring");
                s2.setDescription("Avancerad hotanalys, intrångsdetektion och automatiska incidentrapporter.");
                s2.setServiceLevel("Gold (24/7 support)");
                s2.setPricePerMonth(5999);
                s2.setContractLength(12);
                s2.setRenewalPeriod(12);
                s2.setActive(true);
                s2.setSupportContact("security@nordicsecurity.se");
                s2.setCreatedAt(LocalDate.now().minusYears(1));


                Subscription s3 = new Subscription();
                s3.setName("Cloud Backup Premium");
                s3.setCategory("Backup");
                s3.setDescription("Daglig molnbackup med versionshantering, kryptering och återställningsservice.");
                s3.setServiceLevel("Silver (12/5 support)");
                s3.setPricePerMonth(1499);
                s3.setContractLength(6);
                s3.setRenewalPeriod(6);
                s3.setActive(true);
                s3.setSupportContact("backup@cloudco.se");
                s3.setCreatedAt(LocalDate.now().minusYears(1));


                Subscription s4 = new Subscription();
                s4.setName("Endpoint Protection Advanced");
                s4.setCategory("Security");
                s4.setDescription("Skydd mot malware, ransomware, zero-day exploits och enhetshantering.");
                s4.setServiceLevel("Gold (24/7 support)");
                s4.setPricePerMonth(3999);
                s4.setContractLength(12);
                s4.setRenewalPeriod(12);
                s4.setActive(true);
                s4.setSupportContact("ep@itpartnerstockholm.se");
                s4.setCreatedAt(LocalDate.now().minusYears(1));

                System.out.println("Creating initial subscriptions...");
                subscriptionRepository.saveAll(List.of(s1, s2, s3, s4));
                System.out.println("4 subscriptions created");

                // === CONTRACT 1 — due in 1 month → status TRUE ===
                Contract ct1 = new Contract();
                ct1.setCustomer(c1);
                ct1.setResellers(List.of(r1));
                ct1.setSubscriptions(List.of(s1));
                ct1.setStatus(true);
                ct1.setActive(true);
                ct1.setContractDate(LocalDate.now().minusMonths(11));
                ct1.setContractLengthMonths(12);
                ct1.setRenewalDates(List.of(
                        ct1.getContractDate().plusMonths(12)
                ));
                ct1.setDueDate(LocalDate.now().plusMonths(1));
                ct1.setComment("Löper ut om 1 månad.");


// === CONTRACT 2 — due in 2 months → status TRUE ===
                Contract ct2 = new Contract();
                ct2.setCustomer(c2);
                ct2.setResellers(List.of(r2));
                ct2.setSubscriptions(List.of(s2));
                ct2.setStatus(true);
                ct2.setActive(true);
                ct2.setContractDate(LocalDate.now().minusMonths(10));
                ct2.setContractLengthMonths(12);
                ct2.setRenewalDates(List.of(
                        ct2.getContractDate().plusMonths(12)
                ));
                ct2.setDueDate(LocalDate.now().plusMonths(2));
                ct2.setComment("Förnyas om 2 månader.");


// === CONTRACT 3 — due in 3 months → status TRUE ===
                Contract ct3 = new Contract();
                ct3.setCustomer(c3);
                ct3.setResellers(List.of(r3));
                ct3.setSubscriptions(List.of(s3));
                ct3.setStatus(true);
                ct3.setActive(true);
                ct3.setContractDate(LocalDate.now().minusMonths(9));
                ct3.setContractLengthMonths(12);
                ct3.setRenewalDates(List.of(
                        ct3.getContractDate().plusMonths(12)
                ));
                ct3.setDueDate(LocalDate.now().plusMonths(3));
                ct3.setComment("Löper ut om 3 månader.");


// === CONTRACT 4 — due in 4 months → status FALSE ===
                Contract ct4 = new Contract();
                ct4.setCustomer(c4);
                ct4.setResellers(List.of(r4));
                ct4.setSubscriptions(List.of(s4));
                ct4.setStatus(false);
                ct4.setActive(true);
                ct4.setContractDate(LocalDate.now().minusMonths(8));
                ct4.setContractLengthMonths(12);
                ct4.setRenewalDates(List.of(
                        ct4.getContractDate().plusMonths(12)
                ));
                ct4.setDueDate(LocalDate.now().plusMonths(4));
                ct4.setComment("Löper ut om 4 månader.");


// === CONTRACT 5 — due in 36 months → status FALSE ===
// Här gör jag TVÅ förnyelser eftersom kommentaren säger "förlängt flera gånger"
                Contract ct5 = new Contract();
                ct5.setCustomer(c1);
                ct5.setResellers(List.of(r2));
                ct5.setSubscriptions(List.of(s1));
                ct5.setStatus(false);
                ct5.setActive(true);
                ct5.setContractDate(LocalDate.now().minusMonths(4));
                ct5.setContractLengthMonths(12);
                ct5.setRenewalDates(List.of(
                        ct5.getContractDate().plusMonths(12),
                        ct5.getContractDate().plusMonths(24)
                ));
                ct5.setDueDate(ct5.getContractDate().plusMonths(36));
                ct5.setComment("Förlängt flera gånger.");


// === CONTRACT 6 — due in 24 months → status FALSE ===
                Contract ct6 = new Contract();
                ct6.setCustomer(c2);
                ct6.setResellers(List.of(r3));
                ct6.setSubscriptions(List.of(s2));
                ct6.setStatus(false);
                ct6.setActive(false);
                ct6.setContractDate(LocalDate.now().minusMonths(6));
                ct6.setContractLengthMonths(12);
                ct6.setRenewalDates(List.of(
                        ct6.getContractDate().plusMonths(12)
                ));
                ct6.setDueDate(ct6.getContractDate().plusMonths(24));
                ct6.setComment("Avslutat i förtid.");


// === CONTRACT 7 — due in 48 months → status FALSE ===
                Contract ct7 = new Contract();
                ct7.setCustomer(c3);
                ct7.setResellers(List.of(r4));
                ct7.setSubscriptions(List.of(s3, s4));
                ct7.setStatus(false);
                ct7.setActive(true);
                ct7.setContractDate(LocalDate.now().minusMonths(2).minusDays(12));
                ct7.setContractLengthMonths(24);
                ct7.setRenewalDates(List.of(
                        ct7.getContractDate().plusMonths(24)
                ));
                ct7.setDueDate(ct7.getContractDate().plusMonths(48));
                ct7.setComment("Planerar uppgraderingar.");


// === CONTRACT 8 — due in 36 months → status FALSE ===
                Contract ct8 = new Contract();
                ct8.setCustomer(c4);
                ct8.setResellers(List.of(r2, r3));
                ct8.setSubscriptions(List.of(s1));
                ct8.setStatus(false);
                ct8.setActive(false);
                ct8.setContractDate(LocalDate.now().minusMonths(1).minusDays(20));
                ct8.setContractLengthMonths(12);
                ct8.setRenewalDates(List.of(
                        ct8.getContractDate().plusMonths(12)
                ));
                ct8.setDueDate(ct8.getContractDate().plusMonths(36));
                ct8.setComment("Standardavtal.");


// === CONTRACT 9 — due in 60 months → status FALSE ===
// Här lägger jag 3 förnyelser (36 + 48) eftersom det är långt kontrakt
                Contract ct9 = new Contract();
                ct9.setCustomer(c1);
                ct9.setResellers(List.of(r1, r2));
                ct9.setSubscriptions(List.of(s4));
                ct9.setStatus(false);
                ct9.setActive(true);
                ct9.setContractDate(LocalDate.now().minusMonths(3).minusDays(4));
                ct9.setContractLengthMonths(36);
                ct9.setRenewalDates(List.of(
                        ct9.getContractDate().plusMonths(36),
                        ct9.getContractDate().plusMonths(48)
                ));
                ct9.setDueDate(ct9.getContractDate().plusMonths(60));
                ct9.setComment("Kritisk kund, lång bindningstid.");


// === CONTRACT 10 — due in 24 months → status FALSE ===
                Contract ct10 = new Contract();
                ct10.setCustomer(c3);
                ct10.setResellers(List.of(r4));
                ct10.setSubscriptions(List.of(s2, s3));
                ct10.setStatus(false);
                ct10.setActive(true);
                ct10.setContractDate(LocalDate.now().minusMonths(5));
                ct10.setContractLengthMonths(12);
                ct10.setRenewalDates(List.of(
                        ct10.getContractDate().plusMonths(12)
                ));
                ct10.setDueDate(ct10.getContractDate().plusMonths(24));
                ct10.setComment("Tidigare avtal ersatt av nytt.");


                System.out.println("Creating initial contracts...");
                contractRepository.saveAll(List.of(
                        ct1, ct2, ct3, ct4,
                        ct5, ct6, ct7, ct8, ct9, ct10
                ));
                System.out.println("10 subscriptions created");


            }
        };
    }
}