CREATE TABLE `addresses` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `cityName` varchar(50) NOT NULL,
   `streetName` varchar(50) NOT NULL,
   `zipCode` varchar(6) NOT NULL,
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
 CREATE TABLE `listofclients` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `dateOfRegistration` date NOT NULL,
   `firstName` varchar(50) NOT NULL,
   `lastName` varchar(50) NOT NULL,
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
 CREATE TABLE `client_main_address` (
   `address_id` bigint(20) DEFAULT NULL,
   `client_id` bigint(20) NOT NULL,
   PRIMARY KEY (`client_id`),
   KEY `FKst3m2khbhvq1y7ew9pci62ybh` (`address_id`),
   CONSTRAINT `FKf7a5mrdbfpbmv5f8woe2s91k5` FOREIGN KEY (`client_id`) REFERENCES `listofclients` (`id`),
   CONSTRAINT `FKst3m2khbhvq1y7ew9pci62ybh` FOREIGN KEY (`address_id`) REFERENCES `addresses` (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
 CREATE TABLE `client_address` (
   `client_id` bigint(20) DEFAULT NULL,
   `address_id` bigint(20) NOT NULL,
   PRIMARY KEY (`address_id`),
   KEY `FKef28ptuaju2u91lgrjqvojf35` (`client_id`),
   CONSTRAINT `FK1paqgs3dgg89jiby2wmq0g3d6` FOREIGN KEY (`address_id`) REFERENCES `addresses` (`id`),
   CONSTRAINT `FKef28ptuaju2u91lgrjqvojf35` FOREIGN KEY (`client_id`) REFERENCES `listofclients` (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
 
 -- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: clients
-- ------------------------------------------------------
-- Server version	5.7.17-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES (1,'Katowice','Krakowska','47-789'),(2,'Kraków','Katowicka','65-789'),(3,'Mysłowice','Gliwicka','47-987'),(4,'Gliwice','Mysłowicka','98-784'),(5,'Wrocław','Warszawska','78-987'),(6,'Warszawa','Wrocławska','98-784'),(7,'Czikago','Baker','66-789'),(8,'Arkanzas','Downtown','12-345'),(9,'Zamość','Jasnogórska','64-745'),(10,'Katowice','Górnośląska','34-576'),(11,'Katowice','Mysłowicka','40-478'),(12,'Mysłowice','Katowicka','47-977');
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `client_address`
--

LOCK TABLES `client_address` WRITE;
/*!40000 ALTER TABLE `client_address` DISABLE KEYS */;
INSERT INTO `client_address` VALUES (1,1),(1,2),(1,11),(1,12),(2,5),(2,6),(3,3),(3,4),(5,7),(5,8),(6,9),(7,10);
/*!40000 ALTER TABLE `client_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `client_main_address`
--

LOCK TABLES `client_main_address` WRITE;
/*!40000 ALTER TABLE `client_main_address` DISABLE KEYS */;
INSERT INTO `client_main_address` VALUES (1,1),(3,3),(5,2),(7,5),(9,6),(10,7);
/*!40000 ALTER TABLE `client_main_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `listofclients`
--

LOCK TABLES `listofclients` WRITE;
/*!40000 ALTER TABLE `listofclients` DISABLE KEYS */;
INSERT INTO `listofclients` VALUES (1,'2017-10-16','Dany','Devito'),(2,'2017-10-16','Bany','Devito'),(3,'2017-10-16','Nany','Devito'),(4,'2017-10-16','Without','Address'),(5,'2017-10-16','Devito','Kurkuma'),(6,'2017-10-16','Bill','Colab'),(7,'2017-10-16','Andrzej','Chrząszcz');
/*!40000 ALTER TABLE `listofclients` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-10-16 17:52:27
