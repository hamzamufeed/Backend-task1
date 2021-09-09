package com.task1.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task1.DB.AerospikeResourceRepository;
import com.task1.DB.DTO;
import com.task1.DB.Model;
import com.task1.DB.ResourceModel;
import com.task1.controllers.DTOs.ResourceDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResourceService {

    @Autowired
    AerospikeResourceRepository aerospikeResourceRepository;

    @Autowired
    TransformerService transformerService;

    @Autowired
    public ObjectMapper objectMapper;

    public ResourceDTO getResource(int id){ return (ResourceDTO) transformerService.EntityToDto((Model) aerospikeResourceRepository.findById(id).get(), ResourceDTO.class); }

    public void addResource(ResourceDTO resourceDTO){ aerospikeResourceRepository.save((ResourceModel) transformerService.DtoToEntity((DTO) resourceDTO, ResourceModel.class)); }

    public ResourceDTO updateResource(ResourceDTO resourceDTO) {
        ResourceModel updatedResourceModel = aerospikeResourceRepository.save((ResourceModel) transformerService.DtoToEntity((DTO) resourceDTO, ResourceModel.class));
        return (ResourceDTO) transformerService.EntityToDto((Model) updatedResourceModel, ResourceDTO.class);
    }

    public void removeResourceById(int id) {  aerospikeResourceRepository.deleteById(id); }

    public void patchResource(int id, String patch) {
        //aerospikeResourceRepository.save(patch,id);
    }
}
