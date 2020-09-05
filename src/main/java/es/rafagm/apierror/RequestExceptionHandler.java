package es.rafagm.apierror;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import es.rafagm.apierror.exception.WrongTypeFileException;

@ControllerAdvice
public class RequestExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = "Atleast one parameter is missing";
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, error, ex);

		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(WrongTypeFileException.class)
	private ResponseEntity<Object> handleWrongTypeFile(WrongTypeFileException e) {
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		return buildResponseEntity(apiError);
	}

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}
