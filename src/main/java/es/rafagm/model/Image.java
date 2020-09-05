package es.rafagm.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "image")
public class Image implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String name;
	
	@Column
	private String type;
	
	@Column(length = 500000)
	private byte[] imageBytes;
	
	
	public Image() {
		super();
	}

	public Image(String name, String type, byte[] imageBytes) {
		super();
		this.name = name;
		this.type = type;
		this.imageBytes = imageBytes;
	}
	
	public Image(Long id, String name, String type, byte[] imageBytes) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.imageBytes = imageBytes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getImageBytes() {
		return imageBytes;
	}

	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
	}

	public Long getId() {
		return id;
	}
	
	
}
