package com.opslife.controller;

import com.opslife.service.OpsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class WebhookController {
  private final OpsService ops;

  @Value("${opslife.webhooks.token}")
  private String token;

  public WebhookController(OpsService ops) { this.ops = ops; }

  private void requireToken(String got) {
    if (got == null || !got.equals(token)) throw new Unauthorized();
  }

  @PostMapping("/webhooks/jenkins")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public Map<String,Object> jenkins(@RequestHeader(value="X-Webhook-Token", required=false) String t,
                                    @RequestBody String rawJson) {
    requireToken(t);
    ops.recordWebhook("jenkins", "deployment.recorded", rawJson);
    return Map.of("status","accepted");
  }

  @PostMapping("/webhooks/alerts")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public Map<String,Object> alerts(@RequestHeader(value="X-Webhook-Token", required=false) String t,
                                   @RequestBody String rawJson) {
    requireToken(t);
    ops.recordWebhook("alerts", "alert.received", rawJson);
    return Map.of("status","accepted");
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  static class Unauthorized extends RuntimeException {}
}
