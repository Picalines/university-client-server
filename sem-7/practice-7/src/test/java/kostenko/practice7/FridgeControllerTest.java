package kostenko.practice7;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import kostenko.practice7.controller.FridgeController;
import kostenko.practice7.model.Fridge;
import kostenko.practice7.repository.FridgeRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FridgeControllerTest {
    @Test
    public void testGetFridgeById() {
        var fridge = new Fridge();
        fridge.setId(1L);
        fridge.setType("Fedora");

        var fridgeRepository = Mockito.mock(FridgeRepository.class);
        when(fridgeRepository.findById(1L)).thenReturn(Mono.just(fridge));

        var fridgeController = new FridgeController(fridgeRepository);

        var response = fridgeController.getFridgeById(1L).block();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fridge, response.getBody());
    }

    @Test
    public void testGetAllFridges() {
        var fridge1 = new Fridge();
        fridge1.setId(1L);
        fridge1.setType("Fedora");
        fridge1.setSize(58.0f);

        var fridge2 = new Fridge();
        fridge2.setId(2L);
        fridge2.setType("Beanie");
        fridge2.setSize(56.0f);

        var fridgeRepository = Mockito.mock(FridgeRepository.class);
        when(fridgeRepository.findAll()).thenReturn(Flux.just(fridge1, fridge2));

        var fridgeController = new FridgeController(fridgeRepository);

        var response = fridgeController.getAllFridges(null);
        assertEquals(2, response.collectList().block().size());
    }

    @Test
    public void testCreateFridge() {
        var fridge = new Fridge();
        fridge.setId(1L);
        fridge.setType("Fedora");

        var fridgeRepository = Mockito.mock(FridgeRepository.class);
        when(fridgeRepository.save(fridge)).thenReturn(Mono.just(fridge));

        var fridgeController = new FridgeController(fridgeRepository);

        var response = fridgeController.createFridge(fridge);
        assertEquals(fridge, response.block());
    }

    @Test
    public void testUpdateFridge() {
        var existingFridge = new Fridge();
        existingFridge.setId(1L);
        existingFridge.setType("Fedora");

        var updatedFridge = new Fridge();
        updatedFridge.setId(1L);
        updatedFridge.setType("Beret");

        var fridgeRepository = Mockito.mock(FridgeRepository.class);
        when(fridgeRepository.findById(1L)).thenReturn(Mono.just(existingFridge));
        when(fridgeRepository.save(existingFridge)).thenReturn(Mono.just(updatedFridge));

        var fridgeController = new FridgeController(fridgeRepository);

        ResponseEntity<Fridge> response = fridgeController.updateFridge(1L, updatedFridge).block();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedFridge, response.getBody());
    }

    @Test
    public void testDeleteFridge() {
        var fridge = new Fridge();
        fridge.setId(1L);
        fridge.setType("Fedora");

        var fridgeRepository = Mockito.mock(FridgeRepository.class);
        when(fridgeRepository.findById(1L)).thenReturn(Mono.just(fridge));
        when(fridgeRepository.delete(fridge)).thenReturn(Mono.empty());

        var fridgeController = new FridgeController(fridgeRepository);

        var response = fridgeController.deleteFridge(1L).block();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
