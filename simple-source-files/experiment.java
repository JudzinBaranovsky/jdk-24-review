import module java.net.http;

void main() throws IOException, InterruptedException {
    var client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(20))
        .build();

    var request = HttpRequest.newBuilder()
        .uri(URI.create("https://zenquotes.io/?api=random"))
        .build();
    var response = client.send(request, HttpResponse.BodyHandlers.ofString());

    println(response.statusCode());
    println(response.body());
}
