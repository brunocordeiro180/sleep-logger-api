package com.noom.interview.fullstack.sleep

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@SpringBootApplication
class SleepApplication {
	companion object {
		const val UNIT_TEST_PROFILE = "unittest"
	}
}

fun main(args: Array<String>) {
	runApplication<SleepApplication>(*args)
}

@Bean
fun messageSource(): MessageSource {
	val messageSource = ReloadableResourceBundleMessageSource()

	messageSource.setBasename("classpath:messages")
	messageSource.setDefaultEncoding("UTF-8")
	return messageSource
}

@Bean
fun getValidator(): LocalValidatorFactoryBean {
	val bean = LocalValidatorFactoryBean()
	bean.setValidationMessageSource(messageSource())
	return bean
}
