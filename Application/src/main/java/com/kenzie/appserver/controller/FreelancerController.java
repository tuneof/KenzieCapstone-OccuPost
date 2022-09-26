package com.kenzie.appserver.controller;

import com.kenzie.appserver.controller.model.FreelancerCreateRequest;
import com.kenzie.appserver.controller.model.FreelancerResponse;
import com.kenzie.appserver.controller.model.FreelancerUpdateRequest;
import com.kenzie.appserver.service.FreelancerService;
import com.kenzie.appserver.service.model.Freelancer;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/freelancers")
public class FreelancerController {

    private FreelancerService freelancerService;

    FreelancerController(FreelancerService service) {
        this.freelancerService = service;
    }

    @PostMapping
    public ResponseEntity<FreelancerResponse> createFreelancer(@RequestBody FreelancerCreateRequest request) {
        Freelancer freelancer = new Freelancer(randomUUID().toString(), request.getName(), request.getExpertise(),
                request.getRate(), request.getLocation(), request.getContact());

        try {
            freelancerService.addNewFreelancer(freelancer);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        FreelancerResponse response = freelancerToResponse(freelancer);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FreelancerResponse> getFreelancerById(@PathVariable("id") String id) throws Exception {
        Freelancer freelancer = freelancerService.findById(id);
        FreelancerResponse freelancerResponse = freelancerToResponse(freelancer);
        return ResponseEntity.ok(freelancerResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<FreelancerResponse>> getAllFreelancers() {
        List<Freelancer> freelancers = freelancerService.findAll();

        if (freelancers == null || freelancers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<FreelancerResponse> responses = freelancers.stream()
                .map(this::freelancerToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }


    @PutMapping
    public ResponseEntity<FreelancerResponse> updateFreelancer(@RequestBody FreelancerUpdateRequest request) {
        //if the freelancer that is being updated doesn't exist, returns 204
        if (freelancerService.findById(request.getId()) == null) {
            return ResponseEntity.noContent().build();
        }

        //Otherwise, continue executing method
        Freelancer freelancer = new Freelancer(request.getId(),
                request.getName(),
                request.getExpertise(),
                request.getRate(),
                request.getLocation(),
                request.getContact());
        freelancerService.updateFreelancer(freelancer);

        FreelancerResponse response = freelancerToResponse(freelancer);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteFreelancerById(@PathVariable("id") String id) {
        if (freelancerService.findById(id) == null) {
            return ResponseEntity.noContent().build();
        }
        freelancerService.deleteFreelancer(id);
        return ResponseEntity.ok().build();
    }

    private FreelancerResponse freelancerToResponse(Freelancer freelancer) {
        FreelancerResponse freelancerResponse = new FreelancerResponse();
        freelancerResponse.setId(freelancer.getId());
        freelancerResponse.setName(freelancer.getName());
        freelancerResponse.setExpertise(freelancer.getExpertise());
        freelancerResponse.setContact(freelancer.getContact());
        freelancerResponse.setLocation(freelancer.getLocation());
        freelancerResponse.setRate(freelancer.getRate());

        return freelancerResponse;
    }
}