// package com.cloud.product_service.application.service;

// import java.time.Instant;
// import java.util.UUID;

// import org.springframework.stereotype.Service;

// import com.cloud.product_service.common.utils.jwt.JwtUtils;
// import com.cloud.product_service.domain.enums.ImportStatus;
// import com.cloud.product_service.domain.model.ImportJob;
// import com.cloud.product_service.infrastructure.adapter.outbound.repository.ImportJobRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class ImportJobService {

//     private final ImportJobRepository importJobRepository;
//     private final JwtUtils jwtUtils;
    

//     public ImportJob createImportJob(String fileName) {
//         ImportJob importJob = ImportJob.builder()
//             .vendorId(jwtUtils.getCurrentUserId())
//             .status(ImportStatus.PENDING)
//             .fileName(fileName)
//             .processedRows(0L)
//             .successRows(0L)
//             .failedRows(0L)
//             .totalRows(0L)
//             .build();
//         return importJobRepository.save(importJob);
//     }

//     public ImportJob updateProgressImportJob(UUID id, long processedRows, long totalRows) {
//         ImportJob job = getJob(id);
//         job.setProcessedRows(processedRows);
//         job.setTotalRows(totalRows);
//         return importJobRepository.save(job);
//     }

//     public ImportJob updateStatusImportJob(UUID id, ImportStatus status) {
//         ImportJob job = getJob(id);
//         job.setStatus(status);
//         if (status == ImportStatus.COMPLETED || status == ImportStatus.FAILED) {
//             job.setFinishedAt(Instant.now());
//         }
//         return importJobRepository.save(job);
//     }

//     // HÀM MỚI 1: Cập nhật tiến độ + success + failed
//     public void updateProgress(UUID jobId, long processedRows, long totalRows, long successRows, long failedRows) {
//         ImportJob job = getJob(jobId);
//         job.setProcessedRows(processedRows);
//         job.setTotalRows(totalRows);
//         job.setSuccessRows(successRows);
//         job.setFailedRows(failedRows);
//         importJobRepository.save(job);
//     }

//     public void startJob(UUID jobId, long totalRows) {
//         ImportJob job = getJob(jobId);
//         job.setStatus(ImportStatus.RUNNING);
//         job.setStartedAt(Instant.now());
//         job.setTotalRows(totalRows);
//         importJobRepository.save(job);
//     }

//     // HÀM MỚI 3: Set lỗi khi job crash
//     public void failJob(UUID jobId, String errorMessage) {
//         ImportJob job = getJob(jobId);
//         job.setStatus(ImportStatus.FAILED);
//         job.setErrorMessage(errorMessage);
//         job.setFinishedAt(Instant.now());
//         importJobRepository.save(job);
//     }

//     // Helper
//     private ImportJob getJob(UUID id) {
//         return importJobRepository.findById(id)
//             .orElseThrow(() -> new RuntimeException("Import job not found: " + id));
//     }
// }