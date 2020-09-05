package es.rafagm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import es.rafagm.apierror.exception.ImageNotFound;
import es.rafagm.apierror.exception.WrongTypeFileException;
import es.rafagm.dto.UploadedImageDTO;
import es.rafagm.mapper.Mapper;
import es.rafagm.model.Image;
import es.rafagm.response.StandardResponse;
import es.rafagm.service.ImageService;

@Controller
@RequestMapping("image")
public class ImageController {

	Logger log = LoggerFactory.getLogger(ImageController.class);

	@Autowired
	private ImageService imageService;

	@GetMapping(value = "")
	public ResponseEntity<?> getAllImages() throws IOException {
		if (log.isDebugEnabled())
			log.debug("GET /alarma");
		
		List<Image> images = imageService.findAll();
		images.stream().forEach(image -> image.setImageBytes(imageService.decompressBytes(image.getImageBytes())));

		return new ResponseEntity<List<Image>>(images, HttpStatus.OK);
	}

	@GetMapping(value = "/{imageId}")
	public ResponseEntity<?> getImage(@PathVariable("imageId") Long imageId) throws IOException, ImageNotFound {
		if (log.isDebugEnabled())
			log.debug("GET /image/{imageId}" + imageId + " invoked");

		Optional<Image> fetchedImage = imageService.findById(imageId);

		if (fetchedImage.isPresent()) {
			Image image = new Image(fetchedImage.get().getId(), fetchedImage.get().getName(), fetchedImage.get().getType(),
					imageService.decompressBytes(fetchedImage.get().getImageBytes()));

			return new ResponseEntity<Image>(image, HttpStatus.OK);
		} else {
			throw new ImageNotFound("Image with id: " + "'" + imageId + "'" + " could not be found");
		}
	}

	@PostMapping(value = "")
	public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile uploadedImage)
			throws IOException, WrongTypeFileException {
		if (log.isDebugEnabled())
			log.debug("POST /image/{uploadImage}" + uploadedImage + " invoked");

		if (!uploadedImage.getContentType().startsWith("image/"))
			throw new WrongTypeFileException("Only image files are allowed");

		Image image = new Image(uploadedImage.getOriginalFilename(), uploadedImage.getContentType(),
				imageService.compressBytes(uploadedImage.getBytes()));

		imageService.save(image);

		return new ResponseEntity<UploadedImageDTO>(Mapper.mapUploadImageDTO(image), HttpStatus.OK);
	}
	
	@DeleteMapping(value ="/{imageId}")
	public ResponseEntity<?> deleteImage(@PathVariable("imageId") Long imageId) throws ImageNotFound{
		if (log.isDebugEnabled())
			log.debug("DELETE /alarma/" + imageId + " invoked");
		
		Optional<Image> image = imageService.findById(imageId);
		
		if (image.isPresent()) {
			imageService.delete(image.get());
			
			return new ResponseEntity<StandardResponse>(new StandardResponse("Image with id: " + imageId + " has been sucessfully deleted"), HttpStatus.OK);
		} else {
			throw new ImageNotFound("Image with id: " + "'" + imageId + "'" + " could not be found");
		}
	}

}
