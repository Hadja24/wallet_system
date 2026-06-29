package com.payment.repository;

import com.payment.model.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends MongoRepository<Bill, String> {
    List<Bill> findByPhoneNumber(String phoneNumber);
    List<Bill> findByPhoneNumberAndStatus(String phoneNumber, String status);
    List<Bill> findByProvider(String provider);
    List<Bill> findByPhoneNumberAndProviderAndMonthAndYear(String phoneNumber, String provider, String month, String year);
    Optional<Bill> findByReference(String reference);
    List<Bill> findByReferenceIn(List<String> references);
}