package org.example;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

@RestController
public class Inputservice {

    @Autowired
    private RestTemplate restTemplate;

    private static final String ip = "localhost";
    private static final String SERVER_LOCATION = "document";
    private static final String idParam = "123e4567-2kota-12d3-a456-426655440021";
    private static final String fileTypeParam = "pdf";


    @PostMapping("/filev1")
    public ResponseEntity<String> getFile(){


        byte[] bytes;
        try {

            bytes = restTemplate.getForObject(getUrl(), byte[].class);
            assert bytes != null;
            Files.write(Paths.get("docv1.pdf"), bytes);
            return ResponseEntity.status(HttpStatus.OK).body("OK");

        } catch (URISyntaxException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/filev2")
    public ResponseEntity<String> getFilev2() {


        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response;

            response = restTemplate.exchange(getUrl(), HttpMethod.GET, entity, byte[].class);

            if (response.getBody() == null){
                throw new IOException();
            }

            Files.write(Paths.get("docv2.pdf"), response.getBody());
            return ResponseEntity.status(HttpStatus.OK).body("OK");

        } catch (URISyntaxException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @PostMapping("/filev3")
    public ResponseEntity<String> getFilev3() {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponentsBuilder builder;
        ResponseEntity<byte[]> response;
        try {
            builder = UriComponentsBuilder.fromHttpUrl(getUrlWithoutParams())
                    .queryParam("id", idParam)
                    .queryParam("fileType", fileTypeParam);

            response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, byte[].class);

            if (response.getBody() == null){
                throw new IOException();
            }

            Files.write(Paths.get("docv3.pdf"), response.getBody());
            return ResponseEntity.status(HttpStatus.OK).body("OK");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PostMapping("/filev4stream")
    public void getFileStream() throws IOException, URISyntaxException {

        // Optional Accept header
        RequestCallback requestCallback = request -> request
                .getHeaders()
                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));

        // Streams the response instead of loading it all in memory
        ResponseExtractor<Void> responseExtractor = response -> {

            Path path = Paths.get("docv4.pdf");
            Files.copy(response.getBody(), path);
            return null;
        };
        restTemplate.execute(getUrl(), HttpMethod.GET, requestCallback, responseExtractor);

    }

    private String getUrl() throws URISyntaxException, MalformedURLException {

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(ip);
        builder.setPort(8080);
        builder.setPath(SERVER_LOCATION);
        builder.addParameter("id", idParam);
        builder.addParameter("fileType", fileTypeParam);

        return builder.build().toURL().toString();
    }

    private String getUrlWithoutParams() throws URISyntaxException, MalformedURLException {

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(ip);
        builder.setPort(8080);
        builder.setPath(SERVER_LOCATION);

        return builder.build().toURL().toString();
    }
}
