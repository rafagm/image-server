package es.rafagm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.rafagm.model.Image;
import es.rafagm.repository.ImageRepository;

@Service
@Transactional
public class ImageService {

	@Autowired
	ImageRepository imageRepository;

	public Optional<Image> findById(Long imageId) {
		return imageRepository.findById(imageId);
	}
	
	public Optional<Image> findByName(String name) {
		return imageRepository.findByName(name);
	}
	
	public List<Image> getAll() {
		return (List<Image>) imageRepository.findAll();
	}
	
	public List<Image> getAll(Integer pageNo, Integer pageSize, String sortBy) {
		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
		
		Page<Image> pagedResult = imageRepository.findAll(paging);
		
		return pagedResult.hasContent() ?  pagedResult.getContent() :  new ArrayList<Image>();
	}
	
	public void save(Image image) {
		 imageRepository.save(image);
	}
	
	public void delete(Image image) {
		imageRepository.delete(image);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static byte[] compressBytes(byte[] data) {
		Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();	
        } catch (IOException e ) {}
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
	}
	
	public static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException ioe) {
        } catch (DataFormatException e) {
        }
        return outputStream.toByteArray();

	}
	
}
