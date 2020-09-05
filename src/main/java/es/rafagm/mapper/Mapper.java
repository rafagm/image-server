package es.rafagm.mapper;

import org.springframework.stereotype.Component;

import es.rafagm.dto.UploadedImageDTO;
import es.rafagm.model.Image;

@Component("mapper")
public class Mapper {
	public static UploadedImageDTO mapUploadImageDTO(Image image) {
		if (image == null) return null;
		
		return new UploadedImageDTO(image.getId(), image.getName());
	}
}
