package com.usecase.common

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["com.usecase"], lazyInit = true)
class UseCaseConfig