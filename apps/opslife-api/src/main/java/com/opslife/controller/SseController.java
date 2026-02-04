package com.opslife.controller;

import com.opslife.sse.SseHub;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseController {
  private final SseHub hub;

  public SseController(SseHub hub) { this.hub = hub; }

  @GetMapping(value = "/api/v1/stream/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter stream() {
    return hub.connect();
  }
}
