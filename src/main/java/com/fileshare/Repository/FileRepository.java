package com.fileshare.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fileshare.Model.File;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByTransactionIdAndDownloadPassphrase(String transactionId, String passphrase);

    List<File> findByDownloadPassphrase(String passphrase);

}
