package com.opslife.sse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseHub {
  private final Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();

  public SseEmitter connect() {
    SseEmitter emitter = new SseEmitter(0L);
    emitters.add(emitter);
    emitter.onCompletion(() -> emitters.remove(emitter));
    emitter.onTimeout(() -> emitters.remove(emitter));
    return emitter;
  }

  public void broadcast(Object event) {
    for (SseEmitter e : emitters) {
      try {
        e.send(SseEmitter.event()
          .name("activity")
          .data(event, MediaType.APPLICATION_JSON));
      } catch (Exception ex) {
        // IOException / client disconnected / broken pipe etc.
        emitters.remove(e);
      }
    }
  }
}
