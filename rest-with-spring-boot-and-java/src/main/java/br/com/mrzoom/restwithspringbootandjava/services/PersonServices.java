package br.com.mrzoom.restwithspringbootandjava.services;

import br.com.mrzoom.restwithspringbootandjava.controllers.PersonController;
import br.com.mrzoom.restwithspringbootandjava.data.vo.v1.PersonVO;
import br.com.mrzoom.restwithspringbootandjava.data.vo.v2.PersonVOV2;
import br.com.mrzoom.restwithspringbootandjava.exceptions.RequiredObjectIsNullException;
import br.com.mrzoom.restwithspringbootandjava.exceptions.ResourceNotFoundException;
import br.com.mrzoom.restwithspringbootandjava.mapper.ModelMapper;
import br.com.mrzoom.restwithspringbootandjava.mapper.custom.PersonMapper;
import br.com.mrzoom.restwithspringbootandjava.model.Person;
import br.com.mrzoom.restwithspringbootandjava.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonServices {

    private Logger logger = Logger.getLogger(PersonServices.class.getName());

    @Autowired
    PersonRepository repository;

    @Autowired
    PagedResourcesAssembler<PersonVO> assembler;

    @Autowired
    PersonMapper mapper;

    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {
        logger.info("Finding all people!");

        Page<Person> personPage = repository.findAll(pageable);
        Page<PersonVO> personVosPage = personPage.map(p -> ModelMapper.parseObject(p, PersonVO.class));
        personVosPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getId())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class).findAll(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                "asc"))
                .withSelfRel();

        return assembler.toModel(personVosPage, link);
    }

    public PagedModel<EntityModel<PersonVO>> findPeopleByName(String firstName, Pageable pageable) {
        logger.info("Finding all people!");

        Page<Person> personPage = repository.findPeopleByName(firstName, pageable);
        Page<PersonVO> personVosPage = personPage.map(p -> ModelMapper.parseObject(p, PersonVO.class));
        personVosPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getId())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class).findAll(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                "asc"))
                .withSelfRel();

        return assembler.toModel(personVosPage, link);
    }

    public PersonVO findById(Long id){
        logger.info("Finding one person!");
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        PersonVO vo = ModelMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public PersonVO create(PersonVO person) {
        if (person == null) throw new RequiredObjectIsNullException();
        logger.info("Creating one person!");

        Person entity = ModelMapper.parseObject(person, Person.class);
        PersonVO vo = ModelMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getId())).withSelfRel());
        return vo;
    }

    public PersonVOV2 createV2(PersonVOV2 person) {
        logger.info("Creating one person!");

        Person entity = mapper.convertVOToEntity(person);
        PersonVOV2 vo = mapper.convertEntityToVO(repository.save(entity));
//        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getId())).withSelfRel());
        return vo;
    }

    public PersonVO update(PersonVO person) {
        if (person == null) throw new RequiredObjectIsNullException();
        logger.info("Updating one person!");
        Person entity = repository.findById(person.getId()).orElseThrow(() -> new ResourceNotFoundException("No records found for this Id!"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        PersonVO vo = ModelMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getId())).withSelfRel());
        return vo;
    }

    @Transactional
    public PersonVO disablePerson(Long id){
        logger.info("Disabling one person!");
        repository.disablePerson(id);
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        PersonVO vo = ModelMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public void delete(Long id){
        logger.info("Deleting one person!");
        Person entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this Id!"));
        repository.delete(entity);
    }
}
