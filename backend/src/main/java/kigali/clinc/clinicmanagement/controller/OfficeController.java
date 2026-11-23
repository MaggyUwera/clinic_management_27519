package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.service.OfficeService;


@RestController
@RequestMapping({"/api/office", "/api/offices"})
@CrossOrigin(origins = "http://localhost:5173")
public class OfficeController {

    @Autowired
    private OfficeService officeService;

    @GetMapping(value = "/free", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Office>> getFreeOffices() {
        return new ResponseEntity<>(officeService.getFreeOffices(), HttpStatus.OK);
    }
        // GET /api/office/search?search=rec  -> offices whose name or office number contains "rec"
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Office>> searchOffices(@RequestParam(name = "search", defaultValue = "") String search) {
        String term = search.trim().toLowerCase();
        List<Office> result = officeService.getAllOffices().stream()
                .filter(o -> (o.getName() != null && o.getName().toLowerCase().contains(term))
                        || String.valueOf(o.getOfficeNumber()).contains(term))
                .toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/busiest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBusiestOffice() {
        Optional<Object[]> busiest = officeService.getBusiestOffice();
        if (busiest.isPresent()) return new ResponseEntity<>(busiest.get(), HttpStatus.OK);
        return new ResponseEntity<>("No appointments yet", HttpStatus.OK);
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveOffice(@RequestBody Office office) {
        String msg = officeService.saveOffice(office);
        if (msg.equals("Office is saved Successfully")) return new ResponseEntity<>(msg, HttpStatus.CREATED);
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Office>> getAllOffices() {
        return new ResponseEntity<>(officeService.getAllOffices(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOfficeById(@PathVariable UUID id) {
        Office office = officeService.getOfficeById(id);
        if (office != null) return new ResponseEntity<>(office, HttpStatus.OK);
        return new ResponseEntity<>("Office with id " + id + " not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateOffice(@PathVariable UUID id, @RequestBody Office office) {
        String msg = officeService.updateOffice(id, office);
        if (msg.equals("Office is updated Successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.endsWith("not found")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteOffice(@PathVariable UUID id) {
        String msg = officeService.deleteOffice(id);
        if (msg.equals("Office is deleted Successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
}