package com.task1.services;

import com.task1.DB.DTO;
import com.task1.DB.Model;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Type;


@Service
@AllArgsConstructor
public class TransformerService {

    @Autowired
    public ModelMapper modelMapper;

    public final Logger logger = LogManager.getLogger(TransformerService.class);

    public DTO EntityToDto(Model object, Class DtoClass){
        logger.info(object.getClass().getName());
        return modelMapper.map(object, (Type) DtoClass);
    }

    public Model DtoToEntity(DTO object, Class ModelClass) {
        return modelMapper.map(object, (Type) ModelClass);
    }
}
