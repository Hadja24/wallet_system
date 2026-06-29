package com.badwallet.repository;

import com.badwallet.model.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WalletRepository extends MongoRepository<Wallet, String> {
    Optional<Wallet> findByPhoneNumber(String phoneNumber);
    Page<Wallet> findAll(Pageable pageable);
    boolean existsByPhoneNumber(String phoneNumber);
}