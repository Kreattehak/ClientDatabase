CREATE TABLE `authority` (
   `ID` bigint(20) NOT NULL,
   `NAME` varchar(50) NOT NULL,
   PRIMARY KEY (`ID`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
 CREATE TABLE `user` (
   `ID` bigint(20) NOT NULL,
   `EMAIL` varchar(50) NOT NULL,
   `ENABLED` bit(1) NOT NULL,
   `FIRSTNAME` varchar(50) NOT NULL,
   `LASTPASSWORDRESETDATE` datetime NOT NULL,
   `LASTNAME` varchar(50) NOT NULL,
   `PASSWORD` varchar(100) NOT NULL,
   `USERNAME` varchar(50) NOT NULL,
   PRIMARY KEY (`ID`),
   UNIQUE KEY `UK_lb5yrvw2c22im784wwrpwuq06` (`USERNAME`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
 CREATE TABLE `user_authority` (
   `USER_ID` bigint(20) NOT NULL,
   `AUTHORITY_ID` bigint(20) NOT NULL,
   KEY `FK5o90demi9pdvawuvokvkqhfg2` (`AUTHORITY_ID`),
   KEY `FKkqt0we0xoa20dcsogl5gqrhsl` (`USER_ID`),
   CONSTRAINT `FK5o90demi9pdvawuvokvkqhfg2` FOREIGN KEY (`AUTHORITY_ID`) REFERENCES `authority` (`ID`),
   CONSTRAINT `FKkqt0we0xoa20dcsogl5gqrhsl` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`ID`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
 INSERT INTO USER (ID, USERNAME, PASSWORD, FIRSTNAME, LASTNAME, EMAIL, ENABLED, LASTPASSWORDRESETDATE) VALUES (1, 'admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin', 'admin', 'admin@admin.com', 1, now());
INSERT INTO USER (ID, USERNAME, PASSWORD, FIRSTNAME, LASTNAME, EMAIL, ENABLED, LASTPASSWORDRESETDATE) VALUES (2, 'user', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'user', 'user', 'enabled@user.com', 1, now());
INSERT INTO USER (ID, USERNAME, PASSWORD, FIRSTNAME, LASTNAME, EMAIL, ENABLED, LASTPASSWORDRESETDATE) VALUES (3, 'disabled', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'user', 'user', 'disabled@user.com', 0, now());

INSERT INTO AUTHORITY (ID, NAME) VALUES (1, 'ROLE_USER');
INSERT INTO AUTHORITY (ID, NAME) VALUES (2, 'ROLE_ADMIN');

INSERT INTO USER_AUTHORITY (USER_ID, AUTHORITY_ID) VALUES (1, 1);
INSERT INTO USER_AUTHORITY (USER_ID, AUTHORITY_ID) VALUES (1, 2);
INSERT INTO USER_AUTHORITY (USER_ID, AUTHORITY_ID) VALUES (2, 1);
INSERT INTO USER_AUTHORITY (USER_ID, AUTHORITY_ID) VALUES (3, 1);