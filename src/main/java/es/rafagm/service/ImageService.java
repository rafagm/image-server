package es.rafagm.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.rafagm.repository.ImageRepository;

@Service
@Transactional
public class ImageService {

	@Autowired
	ImageRepository imageRepository;
}
