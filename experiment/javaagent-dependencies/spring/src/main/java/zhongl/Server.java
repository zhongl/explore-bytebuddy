package zhongl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@SpringBootApplication
public class Server {

    final RestTemplate template = new RestTemplate();

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> head(@RequestParam("url") String url) {
        return template.headForHeaders(url).toSingleValueMap();
    }

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
}
