package com.mlyngvo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application {

//    @Configuration
//    class CustomUserService : UserDetailsService {
//
//        override fun loadUserByUsername(username: String?): UserDetails {
//            TODO("Not yet implemented")
//        }
//    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}