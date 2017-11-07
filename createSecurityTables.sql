CREATE TABLE `authority` (
  `id` bigint(20) NOT NULL,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `first_name` varchar(30) NOT NULL,
  `last_password_reset_date` datetime NOT NULL,
  `last_name` varchar(30) NOT NULL,
  `password` varchar(100) NOT NULL,
  `username` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_lb5yrvw2c22im784wwrpwuq06` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_authority` (
  `user_id` bigint(20) NOT NULL,
  `authority_id` bigint(20) NOT NULL,
  KEY `FK5o90demi9pdvawuvokvkqhfg2` (`authority_id`),
  KEY `FKkqt0we0xoa20dcsogl5gqrhsl` (`user_id`),
  CONSTRAINT `FKgvxjs381k6f48d5d2yi11uh89` FOREIGN KEY (`authority_id`) REFERENCES `authority` (`id`),
  CONSTRAINT `FKpqlsjpkybgos9w2svcri7j8xy` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO USER (id, username, password, first_name, last_name, email, enabled, last_password_reset_date) VALUES
  (1, 'admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin', 'admin', 'admin@admin.com', 1,
   now());
INSERT INTO USER (id, username, password, first_name, last_name, email, enabled, last_password_reset_date) VALUES
  (2, 'user', '$2a$12$a7L.hO06B/ikT2cqtnndwepRyIJiYCvlahzGtv./EUrdpAhqGfukO', 'user', 'user', 'enabled@user.com', 1,
   now());
INSERT INTO USER (id, username, password, first_name, last_name, email, enabled, last_password_reset_date) VALUES
  (3, 'disabled', '$2a$12$rLFu5c04SJmUnrXRRRo24Oq24IdxrGOLru3dVB0ZVNwuGm4vrPHMC', 'user', 'user', 'disabled@user.com',
   0, now());

INSERT INTO AUTHORITY (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO AUTHORITY (id, name) VALUES (2, 'ROLE_ADMIN');

INSERT INTO USER_AUTHORITY (user_id, authority_id) VALUES (1, 1);
INSERT INTO USER_AUTHORITY (user_id, authority_id) VALUES (1, 2);
INSERT INTO USER_AUTHORITY (user_id, authority_id) VALUES (2, 1);
INSERT INTO USER_AUTHORITY (user_id, authority_id) VALUES (3, 1);

# This user is just for test purpose
INSERT INTO USER (id, username, password, first_name, last_name, email, enabled, last_password_reset_date) VALUES
  (4, 'someuser', '$2a$12$X58/Gfz/mMPeFsvZ7SV33uiLfI2ysCnZpNHMhyhsxDqpZ764gVXue', 'someuser', 'someuser',
   'someuser@someuser.com', 1, now());
INSERT INTO USER_AUTHORITY (user_id, authority_id) VALUES (4, 1);
INSERT INTO USER_AUTHORITY (user_id, authority_id) VALUES (4, 2);