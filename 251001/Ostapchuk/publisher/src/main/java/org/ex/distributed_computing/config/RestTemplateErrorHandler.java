package org.ex.distributed_computing.config;

import org.ex.distributed_computing.exception.NotFoundException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

public class RestTemplateErrorHandler implements ResponseErrorHandler {

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
  }

  @Override
  public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
    if (response.getStatusCode().is4xxClientError()) {

      switch (response.getStatusCode()) {
        case HttpStatus.NOT_FOUND, HttpStatus.BAD_REQUEST -> throw new NotFoundException("Request %s:%s failed with 404 status".formatted(method, url));
        default -> {}
      }

    }
  }
}
