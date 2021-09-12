package com.task1.controllers.views;

import com.task1.controllers.DTOs.ResourceDTO;
import com.task1.services.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ResourceController {

    @Autowired
    ResourceService resourceService;

    @GetMapping("/resource/{id}")
    public ResponseEntity<?> getResource(@PathVariable int id){
        ResourceDTO resourceDTO = resourceService.getResource(id);
        return (resourceDTO == null) ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource Not Found") :
                ResponseEntity.status(HttpStatus.OK).body(resourceService.getResource(id));
    }

    @PostMapping("/resource")
    public ResponseEntity<?> addResource(@RequestBody ResourceDTO resourceDTO){
        if (resourceService.getResource(resourceDTO.getId()) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Resource Already Exists");
        else if (resourceDTO == null || resourceDTO.getId() == null || resourceDTO.getPrice() < 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Resource Not Valid");
        else if (resourceDTO.getQuantity() < 0 || resourceDTO.getQuantity() > 1000)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantity should be 0 - 1000");
        else {
            resourceService.addResource(resourceDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Resource Created");
        }
    }

    @PutMapping("/resource/{id}")
    public ResponseEntity<?> updateResource(@RequestBody ResourceDTO resourceDTO, @PathVariable("id") Integer id) {
        if (resourceService.getResource(id) == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource Not Found");
        else if (resourceDTO == null || resourceDTO.getId() == null || resourceDTO.getPrice() < 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Resource Not Valid");
        else if (resourceDTO.getQuantity() < 0 || resourceDTO.getQuantity() > 1000)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantity should be 0 - 1000");
        else
            return ResponseEntity.status(HttpStatus.OK).body(resourceService.updateResource(resourceDTO));
    }

    @DeleteMapping("/resource/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable("id") Integer id) {
        ResourceDTO resourceDTO = resourceService.getResource(id);
         if (resourceDTO == null)
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource Not Found");
         else {
             resourceService.removeResourceById(id);
             return ResponseEntity.status(HttpStatus.OK).body("Resource Deleted");
         }
    }

    @PatchMapping(path = "/resource/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void patchResource(@PathVariable("id") int id,  @RequestBody String patch) {
        resourceService.patchResource(id, patch);
    }
}
