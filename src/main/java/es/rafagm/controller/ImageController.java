package es.rafagm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.ResponseStatus;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api(value = "Image server")
@RequestMapping("image")
public class ImageController {

	Logger log = LoggerFactory.getLogger(ImageController.class);

	@Autowired
	private ImageService imageService;

	@ApiOperation(value = "View list of all images", response = Image.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden") })
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/all")
	public ResponseEntity<List<Image>> getAllImages() throws IOException {
		if (log.isDebugEnabled())
			log.debug("GET /alarma");

		List<Image> images = imageService.getAll();
		
		images.stream().forEach(image -> image.setImageBytes(imageService.decompressBytes(image.getImageBytes())));

		return new ResponseEntity<List<Image>>(images, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "View list of images with pagination", response = Image.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden") })
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "")
	public ResponseEntity<List<Image>> getAllImages(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "createDateTime") String sortBy) throws IOException {
		if (log.isDebugEnabled()) 
			log.debug("GET /alarma (with pagination)");
		
		List<Image> images = imageService.getAll(pageNo, pageSize, sortBy);
		
		images.stream().forEach(image -> image.setImageBytes(imageService.decompressBytes(image.getImageBytes())));
		
		return new ResponseEntity<List<Image>>(images, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "View an image", response = Image.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved image", response = Image.class),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "Image with id: ${imageId} could not be found") })
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/{imageId}")
	public ResponseEntity<?> getImage(@PathVariable("imageId") Long imageId) throws IOException, ImageNotFound {
		if (log.isDebugEnabled())
			log.debug("GET /image/{imageId}" + imageId + " invoked");

		Optional<Image> fetchedImage = imageService.findById(imageId);

		if (fetchedImage.isPresent()) {
			Image image = new Image(fetchedImage.get().getId(), fetchedImage.get().getName(),
					fetchedImage.get().getType(), imageService.decompressBytes(fetchedImage.get().getImageBytes()),
					fetchedImage.get().getCreateDateTime());

			return new ResponseEntity<Image>(image, HttpStatus.OK);
		} else {
			throw new ImageNotFound("Image with id: " + "'" + imageId + "'" + " could not be found");
		}
	}

	@ApiOperation(value = "Upload an image", notes = "The file must be a kind of image file")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Image created", response = Image.class),
			@ApiResponse(code = 401, message = "You are not authorized to create the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 400, message = "You must attach an image") })
	@ResponseStatus(HttpStatus.CREATED)
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

		return new ResponseEntity<UploadedImageDTO>(Mapper.mapUploadImageDTO(image), HttpStatus.CREATED);
	}

	@ApiOperation(value = "Delete an image ")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Image deleted", response = StandardResponse.class),
			@ApiResponse(code = 401, message = "You are not authorized"),
			@ApiResponse(code = 404, message = "Image not found", response = String.class) })
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping(value = "/{imageId}")
	public ResponseEntity<?> deleteImage(@PathVariable("imageId") Long imageId) throws ImageNotFound {
		if (log.isDebugEnabled())
			log.debug("DELETE /alarma/" + imageId + " invoked");

		Optional<Image> image = imageService.findById(imageId);

		if (image.isPresent()) {
			imageService.delete(image.get());

			return new ResponseEntity<StandardResponse>(
					new StandardResponse("Image with id: " + imageId + " has been sucessfully deleted"), HttpStatus.OK);
		} else {
			throw new ImageNotFound("Image with id: " + "'" + imageId + "'" + " could not be found");
		}
	}

}
