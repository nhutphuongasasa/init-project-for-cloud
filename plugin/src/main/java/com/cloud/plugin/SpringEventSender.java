// package com.cloud.plugin;

// import org.json.JSONObject;
// import java.net.URI;
// import java.net.http.*;
// import java.time.Duration;
// import java.util.Map;

// public class SpringEventSender {
//     private static final String ENDPOINT_DEFAULT = "https://fusty-nonsensically-esteban.ngrok-free.dev/internal/keycloak/event";
//     private static final String ENDPOINT_REGISTER = "https://fusty-nonsensically-esteban.ngrok-free.dev/internal/keycloak/event/register";
//     private static final String ENDPOINT_LOGIN = "https://fusty-nonsensically-esteban.ngrok-free.dev/internal/keycloak/event/login";
//     private static final String ENDPOINT_LOGOUT = "https://fusty-nonsensically-esteban.ngrok-free.dev/internal/keycloak/event/logout";
//     private static final HttpClient client = HttpClient.newBuilder().build();

//     public void sendEvent(String type, String userId, Map<String, String> details) {
//         try {
//             JSONObject payload = new JSONObject();
//             payload.put("type", type);
//             payload.put("userId", userId != null ? userId : "");
//             payload.put("details", details != null ? details : Map.of());

//             if ( type.equals("REGISTER") ){
//                 formEvent("REGISTER", payload, ENDPOINT_REGISTER);
//             }else if ( type.equals("LOGIN") ){
//                 formEvent("LOGIN", payload, ENDPOINT_LOGIN);
//             }else if ( type.equals("LOGOUT") ){
//                 formEvent("LOGOUT", payload, ENDPOINT_LOGOUT);
//             }else{
//                 formEvent("OTHER", payload, ENDPOINT_DEFAULT);
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     private void formEvent(String type, JSONObject payload, String ENDPOINT){
//         HttpRequest request = HttpRequest.newBuilder()
//                 .uri(URI.create(ENDPOINT))
//                 .header("Content-Type", "application/json")
//                 .header("X-Keycloak-Secret", "my-secret-header")
//                 .header("User-Agent", "Keycloak-Event-Listener/1.0")
//                 .header("Ngrok-Trace-Id", "keycloak-" + System.nanoTime())
//                 .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
//                 .timeout(Duration.ofSeconds(10))
//                 .build();

//         client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//             .thenAccept(r -> System.out.println("GỬI EVENT THÀNH CÔNG " + type + "→ Status: " + r.statusCode()))
//             .exceptionally(e -> {
//                 System.out.println("GỬI EVENT THẤT BẠI: " + e.getMessage());
//                 return null;
//             });
//     }
// }