package kostenko.practice7.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import kostenko.practice7.exception.CustomException;
import kostenko.practice7.model.Fridge;
import kostenko.practice7.repository.FridgeRepository;

@RestController
@RequestMapping("/fridges")
public class FridgeController {
    private final FridgeRepository fridgeRepository;

    @Autowired
    public FridgeController(FridgeRepository fridgeRepository) {
        this.fridgeRepository = fridgeRepository;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Fridge>> getFridgeById(@PathVariable Long id) {
        return fridgeRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<Fridge> getAllFridges(@RequestParam(name = "minsize", required = false) Float minSize) {
        Flux<Fridge> fridges = fridgeRepository.findAll();
        if (minSize != null && minSize > 0) {
            fridges = fridges.filter(fridge -> fridge.getSize() >= minSize);
        }
        return fridges
                .map(this::transformFridge)
                .onErrorResume(e -> Flux.error(new CustomException("Failed to fetch fridges", e)))
                .onBackpressureBuffer();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Fridge> createFridge(@RequestBody Fridge fridge) {
        return fridgeRepository.save(fridge);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Fridge>> updateFridge(@PathVariable Long id, @RequestBody Fridge updatedFridge) {
        return fridgeRepository.findById(id)
                .flatMap(existingFridge -> {
                    existingFridge.setType(updatedFridge.getType());
                    existingFridge.setColor(updatedFridge.getColor());
                    existingFridge.setSize(updatedFridge.getSize());
                    existingFridge.setPrice(updatedFridge.getPrice());
                    return fridgeRepository.save(existingFridge);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteFridge(@PathVariable Long id) {
        return fridgeRepository.findById(id)
                .flatMap(existingFridge ->
                        fridgeRepository.delete(existingFridge)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private Fridge transformFridge(Fridge fridge) {
        fridge.setType(fridge.getType().toUpperCase());
        return fridge;
    }
}
