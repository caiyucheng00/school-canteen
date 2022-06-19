package com.cyc.schoolcanteen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//@ServletComponentScan("com.cyc.schoolcanteen.filter")
@EnableTransactionManagement
@EnableCaching
public class SchoolCanteenApplication{

    public static void main(String[] args) {
        SpringApplication.run(SchoolCanteenApplication.class, args);
    }

}
