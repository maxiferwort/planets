package com.maxi.planets.service;

import com.maxi.planets.persistence.model.ErrorResponse;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory
      .getLogger(GlobalExceptionHandler.class);


  @ExceptionHandler({ResourceNotFoundException.class, ObjectNotFoundException.class})
  public ResponseEntity handleResourceNotFound(HttpServletRequest request, Exception e) {
    return new ResponseEntity(
        new ErrorResponse().setCode(HttpStatus.NOT_FOUND.value()).setMessage(e.getMessage()),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
  public ResponseEntity handleNotSupportedType(HttpServletRequest httpServletRequest
      , Exception e) {
    logger.error("Unsopported media type", e);
    return new ResponseEntity(
        new ErrorResponse().setCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
            .setMessage(e.getMessage())
        , HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestParameterException.class,
      BindException.class, HttpMessageNotReadableException.class})
  public ResponseEntity handleBadRequest(HttpServletRequest request, Exception e) {
    logger.error("Bad request ", e);
    return new ResponseEntity(
        new ErrorResponse().setCode(HttpStatus.BAD_REQUEST.value()).setMessage(e.getMessage())
        , HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity handleBadRequestType(HttpServletRequest request, Exception e) {
    logger.error("Bad request ", e);
    return new ResponseEntity(
        new ErrorResponse().setCode(HttpStatus.BAD_REQUEST.value())
            .setMessage("Request params could not be parsed or are the wrong type")
        , HttpStatus.BAD_REQUEST);
  }


  @ExceptionHandler({EmptyResultDataAccessException.class})
  public ResponseEntity handleNotFound(HttpServletRequest request, Exception e) {
    return new ResponseEntity(new ErrorResponse().setCode(HttpStatus.FORBIDDEN.value())
        .setMessage(e.getMessage())
        , HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity handle(HttpServletRequest request, Exception e) {
    logger.error("Failed to handle request for url: " + getFullURL(request), e);
    return new ResponseEntity(new ErrorResponse().setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .setMessage(e.getMessage())
        , HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public String getFullURL(HttpServletRequest request) {
    StringBuffer requestURL = request.getRequestURL();
    String queryString = request.getQueryString();

    if (queryString == null) {
      return requestURL.toString();
    } else {
      return requestURL.append('?').append(queryString).toString();
    }
  }

}
