package org.faust.config;

import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {

    // TODO: find better solution
    @GetMapping
    public void healthcheck() {}
}
