CREATE DATABASE resident;

USE resident;


CREATE TABLE `agenda` (
                          `end_date` date NOT NULL,
                          `created_at` datetime(6) DEFAULT NULL,
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `apartment_code` varchar(255) NOT NULL,
                          `title` varchar(255) NOT NULL,
                          `details` tinytext,
                          `secret` bit(1) DEFAULT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `agenda_history` (
                                  `end_date` date NOT NULL,
                                  `id` bigint NOT NULL,
                                  `title` varchar(255) NOT NULL,
                                  `details` tinytext,
                                  `apartment_code` varchar(255) DEFAULT NULL,
                                  `created_at` datetime(6) DEFAULT NULL,
                                  `secret` bit(1) DEFAULT NULL,
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `batch_job_instance` (
                                      `JOB_INSTANCE_ID` bigint NOT NULL,
                                      `VERSION` bigint DEFAULT NULL,
                                      `JOB_NAME` varchar(100) NOT NULL,
                                      `JOB_KEY` varchar(32) NOT NULL,
                                      PRIMARY KEY (`JOB_INSTANCE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `batch_job_execution` (
                                       `JOB_EXECUTION_ID` bigint NOT NULL,
                                       `VERSION` bigint DEFAULT NULL,
                                       `JOB_INSTANCE_ID` bigint NOT NULL,
                                       `CREATE_TIME` timestamp NOT NULL,
                                       `START_TIME` timestamp NULL DEFAULT NULL,
                                       `END_TIME` timestamp NULL DEFAULT NULL,
                                       `STATUS` varchar(10) DEFAULT NULL,
                                       `EXIT_CODE` varchar(20) DEFAULT NULL,
                                       `EXIT_MESSAGE` varchar(2500) DEFAULT NULL,
                                       `LAST_UPDATED` timestamp NULL DEFAULT NULL,
                                       PRIMARY KEY (`JOB_EXECUTION_ID`),
                                       KEY `JOB_INSTANCE_EXECUTION_FK` (`JOB_INSTANCE_ID`),
                                       CONSTRAINT `JOB_INSTANCE_EXECUTION_FK` FOREIGN KEY (`JOB_INSTANCE_ID`) REFERENCES `batch_job_instance` (`JOB_INSTANCE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `batch_job_execution_context` (
                                               `JOB_EXECUTION_ID` bigint NOT NULL,
                                               `SHORT_CONTEXT` varchar(2500) NOT NULL,
                                               `SERIALIZED_CONTEXT` blob,
                                               PRIMARY KEY (`JOB_EXECUTION_ID`),
                                               CONSTRAINT `JOB_EXEC_CTX_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `batch_job_execution` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `batch_job_execution_params` (
                                              `JOB_EXECUTION_ID` bigint NOT NULL,
                                              `PARAMETER_NAME` varchar(100) NOT NULL,
                                              `PARAMETER_TYPE` varchar(100) NOT NULL,
                                              `PARAMETER_VALUE` varchar(2500) DEFAULT NULL,
                                              `IDENTIFYING` char(1) NOT NULL,
                                              KEY `JOB_EXEC_PARAMS_FK` (`JOB_EXECUTION_ID`),
                                              CONSTRAINT `JOB_EXEC_PARAMS_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `batch_job_execution` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `batch_job_execution_seq` (
    `ID` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `batch_job_seq` (
    `ID` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `batch_step_execution` (
                                        `STEP_EXECUTION_ID` bigint NOT NULL,
                                        `VERSION` bigint NOT NULL,
                                        `STEP_NAME` varchar(100) NOT NULL,
                                        `JOB_EXECUTION_ID` bigint NOT NULL,
                                        `CREATE_TIME` timestamp NOT NULL,
                                        `START_TIME` timestamp NULL DEFAULT NULL,
                                        `END_TIME` timestamp NULL DEFAULT NULL,
                                        `STATUS` varchar(10) DEFAULT NULL,
                                        `COMMIT_COUNT` bigint DEFAULT NULL,
                                        `READ_COUNT` bigint DEFAULT NULL,
                                        `FILTER_COUNT` bigint DEFAULT NULL,
                                        `WRITE_COUNT` bigint DEFAULT NULL,
                                        `READ_SKIP_COUNT` bigint DEFAULT NULL,
                                        `WRITE_SKIP_COUNT` bigint DEFAULT NULL,
                                        `PROCESS_SKIP_COUNT` bigint DEFAULT NULL,
                                        `ROLLBACK_COUNT` bigint DEFAULT NULL,
                                        `EXIT_CODE` varchar(20) DEFAULT NULL,
                                        `EXIT_MESSAGE` varchar(2500) DEFAULT NULL,
                                        `LAST_UPDATED` timestamp NULL DEFAULT NULL,
                                        PRIMARY KEY (`STEP_EXECUTION_ID`),
                                        KEY `JOB_EXECUTION_STEP_FK` (`JOB_EXECUTION_ID`),
                                        CONSTRAINT `JOB_EXECUTION_STEP_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `batch_job_execution` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `batch_step_execution_context` (
                                                `STEP_EXECUTION_ID` bigint NOT NULL,
                                                `SHORT_CONTEXT` varchar(2500) NOT NULL,
                                                `SERIALIZED_CONTEXT` blob,
                                                PRIMARY KEY (`STEP_EXECUTION_ID`),
                                                CONSTRAINT `STEP_EXEC_CTX_FK` FOREIGN KEY (`STEP_EXECUTION_ID`) REFERENCES `batch_step_execution` (`STEP_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `batch_step_execution_seq` (
    `ID` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `select_option` (
                                 `agenda_id` bigint NOT NULL,
                                 `created_at` datetime(6) DEFAULT NULL,
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `summary` varchar(255) NOT NULL,
                                 `details` tinytext,
                                 PRIMARY KEY (`id`),
                                 KEY `FKmimf3ruto7hkkbse0pblvk4bl` (`agenda_id`),
                                 CONSTRAINT `FKmimf3ruto7hkkbse0pblvk4bl` FOREIGN KEY (`agenda_id`) REFERENCES `agenda` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `select_option_history` (
                                         `count` int NOT NULL,
                                         `agenda_id` bigint DEFAULT NULL,
                                         `id` bigint NOT NULL,
                                         `details` varchar(255) DEFAULT NULL,
                                         `summary` varchar(255) NOT NULL,
                                         `created_at` datetime(6) DEFAULT NULL,
                                         PRIMARY KEY (`id`),
                                         KEY `FK2hiqu3jtd8aikbx8lpwmofcyj` (`agenda_id`),
                                         CONSTRAINT `FK2hiqu3jtd8aikbx8lpwmofcyj` FOREIGN KEY (`agenda_id`) REFERENCES `agenda_history` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_role_token` (
                                   `building` int NOT NULL,
                                   `expired` bit(1) NOT NULL,
                                   `unit` int NOT NULL,
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `apartment_code` varchar(255) NOT NULL,
                                   `role` enum('ADMIN','HOUSE_LEADER','LEADER','MEMBER','UNREGISTERED','VICE_LEADER') NOT NULL,
                                   `token` varchar(255) NOT NULL,
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `UK_e3qu2wij68lpw37qfg2bnq2o9` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `users` (
                         `building` int NOT NULL,
                         `unit` int NOT NULL,
                         `created_at` datetime(6) DEFAULT NULL,
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `updated_at` datetime(6) DEFAULT NULL,
                         `apartment_code` varchar(255) DEFAULT NULL,
                         `email` varchar(255) NOT NULL,
                         `name` varchar(255) NOT NULL,
                         `password` varchar(255) NOT NULL,
                         `phone` varchar(255) NOT NULL,
                         `role` enum('ADMIN','HOUSE_LEADER','LEADER','MEMBER','UNREGISTERED','VICE_LEADER') NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
                         UNIQUE KEY `UK_du5v5sr43g5bfnji4vb8hg5s3` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `vote` (
                        `created_at` datetime(6) DEFAULT NULL,
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `select_option_id` bigint NOT NULL,
                        `user_id` bigint NOT NULL,
                        PRIMARY KEY (`id`),
                        KEY `FK57tbdcy506f0hw3qx7chxpxhp` (`select_option_id`),
                        KEY `idx_usderId_created_time` (`user_id`,`created_at` DESC),
                        CONSTRAINT `FK57tbdcy506f0hw3qx7chxpxhp` FOREIGN KEY (`select_option_id`) REFERENCES `select_option` (`id`),
                        CONSTRAINT `vote_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `voter_ids` (
                             `select_option_history_id` bigint NOT NULL,
                             `voter_ids` bigint DEFAULT NULL,
                             KEY `FKh8efpcsaw4c3ep06ioc6y11iy` (`select_option_history_id`),
                             CONSTRAINT `FKh8efpcsaw4c3ep06ioc6y11iy` FOREIGN KEY (`select_option_history_id`) REFERENCES `select_option_history` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
