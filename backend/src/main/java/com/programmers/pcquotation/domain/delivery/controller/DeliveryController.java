package com.programmers.pcquotation.domain.delivery.controller;

import com.programmers.pcquotation.domain.delivery.entity.DeliveryCreateRequest;
import com.programmers.pcquotation.domain.delivery.entity.DeliveryDto;
import com.programmers.pcquotation.domain.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @GetMapping
    public List<DeliveryDto> getDeliveryList(){
        return deliveryService.findAll();
    }

    @GetMapping("/{id}")
    public DeliveryDto getDeliveryDetail(@PathVariable Integer id){
        return deliveryService.findOne(id);
    }

    @PostMapping
    public ResponseEntity<String> createDelivery(
            @RequestBody @Valid DeliveryCreateRequest deliveryCreateRequest,
            @RequestParam("id") Integer id){
        deliveryService.create(deliveryCreateRequest.address(), id);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("주문이 완료되었습니다.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDelivery(@PathVariable Integer id){
        deliveryService.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("주문이 취소되었습니다.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> modifyDelivery(
            @PathVariable Integer id,
            @RequestBody @Valid DeliveryCreateRequest deliveryCreateRequest){
        deliveryService.modify(id, deliveryCreateRequest.address());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("주문이 수정되었습니다.");
    }
}
