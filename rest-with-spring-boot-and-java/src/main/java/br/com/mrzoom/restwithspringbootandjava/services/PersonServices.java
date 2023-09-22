package br.com.mrzoom.restwithspringbootandjava.services;

import br.com.mrzoom.restwithspringbootandjava.data.vo.v1.PersonVO;
import br.com.mrzoom.restwithspringbootandjava.mapper.DozerMapper;
import br.com.mrzoom.restwithspringbootandjava.model.Person;
import br.com.mrzoom.restwithspringbootandjava.repository.PersonRepository;
import br.com.mrzoom.restwithspringbootandjava.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class PersonServices {

    private Logger logger = Logger.getLogger(PersonServices.class.getName());

    @Autowired
    PersonRepository repository;

    public List<PersonVO> findAll() {
        logger.info("Finding all people!");
        return DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
    }

    public PersonVO findById(Long id){
        logger.info("Finding one person!");
        Person entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this Id!"));
        return DozerMapper.parseObject(entity, PersonVO.class);
    }

    public PersonVO create(PersonVO person) {
        logger.info("Creating one person!");

        Person entity = DozerMapper.parseObject(person, Person.class);
        PersonVO vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);

        return vo;
    }

    public PersonVO update(PersonVO person) {
        logger.info("Updating one person!");
        Person entity = repository.findById(person.getId()).orElseThrow(() -> new ResourceNotFoundException("No records found for this Id!"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        PersonVO vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        return vo;
    }

    public void delete(Long id){
        logger.info("Deleting one person!");
        Person entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this Id!"));
        repository.delete(entity);
    }
}
