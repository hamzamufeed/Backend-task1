package com.task1.controllers.views;

import com.task1.controllers.DTOs.ResourceDTO;
import com.task1.services.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class ResourceController {

    @Autowired
    ResourceService resourceService;

    @GetMapping("/resource/{id}")
    public ResourceDTO getResource(@PathVariable int id){ return resourceService.getResource(id); }

    @PostMapping("/resource")
    public void addResource(@RequestBody ResourceDTO resourceDTO){ resourceService.addResource(resourceDTO); }

    @PutMapping("/resource/{id}")
    public ResourceDTO updateResource(@RequestBody ResourceDTO resourceDTO, @PathVariable("id") Integer id) { return resourceService.updateResource(resourceDTO); }

    @DeleteMapping("/resource/{id}")
    public void deleteResource(@PathVariable("id") Integer id) { resourceService.removeResourceById(id); }

    @PatchMapping(path = "/resource/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void patchResource(@PathVariable("id") int id,  @RequestBody String patch) {
        resourceService.patchResource(id, patch); }
}
