package com.programmers.pcquotation.domain.delivery.service;

import com.programmers.pcquotation.domain.delivery.entity.Delivery;
import com.programmers.pcquotation.domain.delivery.entity.DeliveryDto;
import com.programmers.pcquotation.domain.delivery.entity.DeliveryStatus;
import com.programmers.pcquotation.domain.delivery.exception.NullEntityException;
import com.programmers.pcquotation.domain.delivery.repository.DeliveryRepository;
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository;
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest;
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequestStatus;
import com.programmers.pcquotation.domain.estimaterequest.repository.EstimateRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final EstimateRepository estimateRepository;

    //배달 생성을 통해 채택된 상태로 변경
    public void create(String address, Integer id) {
        Delivery delivery = deliveryRepository.save(Delivery
                .builder()
                    .address(address)
                    .status(DeliveryStatus.ORDER_COMPLETED)
                    .estimate(estimateRepository.findById(id).orElseThrow(NullEntityException::new))
                .build());

        delivery.getEstimate().getEstimateRequest().UpdateDeliveryStatus(EstimateRequestStatus.Adopt);
    }

    public List<DeliveryDto> findAll() {
        return deliveryRepository
                .findAll()
                .stream()
                .map(DeliveryDto::new).toList();
    }

    public DeliveryDto findOne(Integer id) {
        return deliveryRepository
                .findById(id)
                .stream()
                .map(DeliveryDto::new)
                .findAny()
                .orElseThrow(NullEntityException::new);
    }

    //배달 삭제 로직을 통해 견적 요청 상태가 초기값으로 돌아가게함
    public void delete(Integer id) {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(NullEntityException::new);
        delivery.getEstimate().getEstimateRequest().UpdateDeliveryStatus(EstimateRequestStatus.Wait);

        deliveryRepository.delete(delivery);
    }

    public void modify(Integer id, String address) {
        deliveryRepository
                .findById(id)
                .orElseThrow(NullEntityException::new)
                .updateAddress(address);
    }
}