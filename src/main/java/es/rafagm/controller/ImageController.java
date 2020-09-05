package es.rafagm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import es.rafagm.apierror.exception.ImageNotFound;
import es.rafagm.apierror.exception.WrongTypeFileException;
import es.rafagm.dto.UploadedImageDTO;
import es.rafagm.mapper.Mapper;
import es.rafagm.model.Image;
import es.rafagm.service.ImageService;

@Controller
@RequestMapping("image")
public class ImageController {

	Logger log = LoggerFactory.getLogger(ImageController.class);

	@Autowired
	private ImageService imageService;

	@GetMapping(value = "/{imageName}")
	public ResponseEntity<?> getImage(@PathVariable("imageName") String imageName) throws IOException, ImageNotFound {
		if (log.isDebugEnabled())
			log.debug("GET /image/{imageName}" + imageName + " invoked");

		Optional<Image> fetchedImage = imageService.findByName(imageName);

		if (fetchedImage.isPresent()) {
			Image image = new Image(fetchedImage.get().getName(), fetchedImage.get().getType(),
					imageService.decompressBytes(fetchedImage.get().getImageBytes()));

			return new ResponseEntity<Image>(image, HttpStatus.OK);
		} else {
			throw new ImageNotFound("Image with name: " + imageName + " could not be found");
		}
	}

	@PostMapping(value = "")
	public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile uploadedImage) throws IOException, WrongTypeFileException {
		if (log.isDebugEnabled())
			log.debug("POST /image/{uploadImage}" + uploadedImage + " invoked");
		
		if (!uploadedImage.getContentType().startsWith("image/"))
			throw new WrongTypeFileException("Only image files are allowed");

		Image image = new Image(uploadedImage.getOriginalFilename(), uploadedImage.getContentType(),
				imageService.compressBytes(uploadedImage.getBytes()));

		imageService.save(image);

		return new ResponseEntity<UploadedImageDTO>(Mapper.mapUploadImageDTO(image), HttpStatus.OK);
	}

}
