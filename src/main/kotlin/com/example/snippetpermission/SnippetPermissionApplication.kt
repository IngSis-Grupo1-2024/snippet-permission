package com.example.snippetpermission

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SnippetPermissionApplication{
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SnippetPermissionApplication::class.java, *args)
        }
    }
}
