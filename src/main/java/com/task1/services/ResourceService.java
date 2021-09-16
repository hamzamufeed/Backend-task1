package com.task1.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task1.DB.*;
import com.task1.controllers.DTOs.ResourceDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class ResourceService {

    @Autowired
    AerospikeResourceRepository aerospikeResourceRepository;

    @Autowired
    TransformerService transformerService;

    @Autowired
    public ObjectMapper objectMapper;

    ResourceCache resourceCache;

    @PostConstruct
    public void init(){
        resourceCache.RESOURCE_CACHE.setResources(
                StreamSupport
                        .stream( aerospikeResourceRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList())
        );
    }

    public List<ResourceDTO> getAllResources(){
        //Iterable<ResourceModel> all = aerospikeResourceRepository.findAll();
        ArrayList<ResourceModel> all = new ArrayList<>(resourceCache.getResources().values());
        return StreamSupport
                .stream(all.spliterator(), false)
                .map( i -> (ResourceDTO) transformerService.EntityToDto((Model) i, ResourceDTO.class))
                .collect(Collectors.toList());
    }

    public ResourceDTO getResource(int id){
        //Optional<ResourceModel> resource = aerospikeResourceRepository.findById(id);
        //return (resource.isPresent()) ? (ResourceDTO) transformerService.EntityToDto((Model) resource.get(), ResourceDTO.class) : null;
        ResourceModel resource = resourceCache.read(id);
        return (resource != null) ? (ResourceDTO) transformerService.EntityToDto((Model) resource, ResourceDTO.class) : null;
    }

    public void addResource(ResourceDTO resourceDTO){
        ResourceModel resourceModel = (ResourceModel) transformerService.DtoToEntity((DTO) resourceDTO, ResourceModel.class);
        aerospikeResourceRepository.save(resourceModel);
        resourceCache.write(resourceModel);
    }

    public ResourceDTO updateResource(ResourceDTO resourceDTO) {
        ResourceModel updatedResourceModel = aerospikeResourceRepository.save((ResourceModel) transformerService.DtoToEntity((DTO) resourceDTO, ResourceModel.class));
        resourceCache.update(updatedResourceModel.getId(), updatedResourceModel);
        return (ResourceDTO) transformerService.EntityToDto((Model) updatedResourceModel, ResourceDTO.class);
    }

    public void removeResourceById(int id) {
        aerospikeResourceRepository.deleteById(id);
        resourceCache.delete(id);
    }

    public void patchResource(int id, String patch) {
        resourceCache.patch(id, patch);
        //aerospikeResourceRepository.save(patch,id);
    }
}
