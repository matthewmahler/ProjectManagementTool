package com.mattmahler.ppmtool.services;

import com.mattmahler.ppmtool.domain.Project;
import com.mattmahler.ppmtool.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public Project saveOrUpdate(Project project){
        //Lot of logic to go here
        return projectRepository.save(project);
    }
}
