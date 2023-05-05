package com.example.demo.component.rpc.idservice.impl;

import com.example.demo.component.rpc.idservice.tools.IdGenerator;
import com.example.demo.remote.IdGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class IdGeneratorServiceImpl implements IdGeneratorService {
    @Override
    public String createId(String prefix) {
        Long id= IdGenerator.generate(IdGenerator.toHash(prefix));
        log.info("generate id:{}",id);
        return id.toString();
    }
}
