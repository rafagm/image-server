package es.rafagm.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.rafagm.model.Image;

@Repository
@Transactional
public interface ImageRepository extends CrudRepository<Image, Integer> {		
	public Optional<Image> findById(Long imageId);
	public Optional<Image> findByName(String name);
}
