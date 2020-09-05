package es.rafagm.dto;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class UploadedImageDTO implements Serializable {
	private Long id;
	private String name;

	public UploadedImageDTO() {
		super();
	}

	public UploadedImageDTO(Long id,String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public Long getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
