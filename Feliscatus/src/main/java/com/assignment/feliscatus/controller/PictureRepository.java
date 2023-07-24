package com.assignment.feliscatus.controller;

import com.assignment.feliscatus.model.Picture;
import org.springframework.data.repository.CrudRepository;

public interface PictureRepository extends CrudRepository<Picture, Long> {

}