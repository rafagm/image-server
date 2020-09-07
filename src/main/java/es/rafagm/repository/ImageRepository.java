package es.rafagm.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import es.rafagm.model.Image;

@Repository
@Transactional
public interface ImageRepository extends PagingAndSortingRepository<Image, Integer> {		
	public Optional<Image> findById(Long imageId);
	public Optional<Image> findByName(String name);
}
