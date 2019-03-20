package com.mattmahler.ppmtool.services;

import com.mattmahler.ppmtool.domain.Backlog;
import com.mattmahler.ppmtool.domain.Project;
import com.mattmahler.ppmtool.domain.ProjectTask;
import com.mattmahler.ppmtool.exceptions.ProjectNotFoundException;
import com.mattmahler.ppmtool.repositories.BacklogRepository;
import com.mattmahler.ppmtool.repositories.ProjectRepository;
import com.mattmahler.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {
    @Autowired
    private BacklogRepository backlogRepository;
    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask){

        try {
            //tasks must be added to a specific backlog/project
            Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);

            //set backlog
            projectTask.setBacklog(backlog);

            //set project sequence
            Integer BacklogSequence = backlog.getPTSequence();
            BacklogSequence++; //increment

            backlog.setPTSequence(BacklogSequence);

            //Add to task
            projectTask.setProjectSequence(projectIdentifier+"-"+BacklogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            //initial priority
            if(projectTask.getPriority()==null){
                projectTask.setPriority(3);
            }

            //initial status
            if(projectTask.getStatus()==""|| projectTask.getStatus()==null){
                projectTask.setStatus("TO_DO");
            }
            return projectTaskRepository.save(projectTask);
        }catch (Exception e){
            throw new ProjectNotFoundException("Project not found");
        }

    }

    public Iterable<ProjectTask> findBacklogById(String id){

        Project project = projectRepository.findByProjectIdentifier(id);

        if(project==null){
            throw new ProjectNotFoundException("Project with ID: "+id+" does not exist");
        }

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id){

        //make sure we are searching the correct backlog
        Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
        if (backlog==null){
            throw new ProjectNotFoundException("Project with ID: "+backlog_id+" does not exist");
        }

        //make sure the task exists
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
        if (projectTask==null){
            throw new ProjectNotFoundException("Project Task: "+pt_id+" not found");
        }
        //make sure the backlog id in the path corresponds to the right project
        if (!projectTask.getProjectIdentifier().equals(backlog_id)){
            throw new ProjectNotFoundException("Project Task: "+pt_id+" does not exist in project "+backlog_id);
        }

        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id){
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);
        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);
    }
    public void deletePTByProjectSequence(String backlog_id, String pt_id){
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);



        projectTaskRepository.delete(projectTask);
    }

}
