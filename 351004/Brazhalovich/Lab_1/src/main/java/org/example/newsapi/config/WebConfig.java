@Bean
public WebClient discussionWebClient() {
    return WebClient.builder()
            .baseUrl("http://localhost:24130")   // только хост+порт
            .build();
}