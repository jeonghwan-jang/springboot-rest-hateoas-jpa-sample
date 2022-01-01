package com.assignment.farmskin.common.annotation;

import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@RestController
@RequestMapping(value = "/api/v1", produces = MediaTypes.HAL_JSON_VALUE)
public @interface ApiV1Controller {
}
