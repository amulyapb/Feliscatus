package com.assignment.feliscatus.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Picture {

  public Picture(Long id, String fileName, String description) {
    this.id = id;
    this.fileName = fileName;
    this.description = description;
  }

  public Picture(String fileName, String description) {
    this.fileName = fileName;
    this.description = description;
  }

  public Picture() {

  }

  public void setId(Long id) {
    this.id = id;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String fileName;
  private String description;

  public String getFileName() {
    return this.fileName;
  }

  public Long getId() {
    return id;
  }

  public void setFileName(String name) {
    this.fileName = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String url) {
    this.description = url;
  }
}
