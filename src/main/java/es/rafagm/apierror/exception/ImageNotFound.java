package es.rafagm.apierror.exception;

public class ImageNotFound extends Exception {
	
	public ImageNotFound() {
		super("Image not found");
	}
	
	public ImageNotFound(String message) {
		super(message);
	}
}
