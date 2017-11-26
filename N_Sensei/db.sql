-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 26, 2017 at 01:25 AM
-- Server version: 5.7.14
-- PHP Version: 5.6.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bajtahack`
--
CREATE DATABASE IF NOT EXISTS `bajtahack` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `bajtahack`;

-- --------------------------------------------------------

--
-- Table structure for table `meritev`
--

CREATE TABLE `meritev` (
  `ID` int(11) NOT NULL,
  `SENZOR_ID` int(11) NOT NULL,
  `VALUE` varchar(100) CHARACTER SET latin1 NOT NULL,
  `CREATED` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `meritev`
--

INSERT INTO `meritev` (`ID`, `SENZOR_ID`, `VALUE`, `CREATED`) VALUES
(1, 9, '12', '2017-11-26 01:24:11'),
(2, 9, '25', '2017-11-26 01:48:10'),
(3, 9, '25', '2017-11-26 01:53:12'),
(4, 9, '26', '2017-11-26 01:53:12'),
(5, 9, '22', '2017-11-26 01:53:12'),
(6, 9, '22', '2017-11-26 01:53:12'),
(7, 9, '21', '2017-11-26 01:53:12'),
(8, 9, '25', '2017-11-26 01:53:12'),
(9, 9, '24', '2017-11-26 01:53:12'),
(10, 9, '26', '2017-11-26 01:53:12'),
(11, 9, '23', '2017-11-26 01:53:12'),
(12, 9, '25', '2017-11-26 01:53:12'),
(13, 9, '20', '2017-11-26 01:53:12'),
(14, 9, '26', '2017-11-26 01:53:12'),
(15, 9, '25', '2017-11-26 01:53:12'),
(16, 9, '21', '2017-11-26 01:53:12'),
(17, 9, '20', '2017-11-26 01:53:12'),
(18, 9, '20', '2017-11-26 01:53:12'),
(19, 9, '25', '2017-11-26 01:53:12'),
(20, 9, '26', '2017-11-26 01:53:12'),
(21, 9, '21', '2017-11-26 01:53:12'),
(22, 9, '23', '2017-11-26 01:53:12'),
(23, 13, '25', '2017-11-26 01:54:11'),
(24, 13, '26', '2017-11-26 01:54:11'),
(25, 13, '22', '2017-11-26 01:54:11'),
(26, 13, '22', '2017-11-26 01:54:11'),
(27, 13, '21', '2017-11-26 01:54:11'),
(28, 13, '25', '2017-11-26 01:54:11'),
(29, 13, '24', '2017-11-26 01:54:11'),
(30, 13, '26', '2017-11-26 01:54:11'),
(31, 13, '23', '2017-11-26 01:54:11'),
(32, 13, '25', '2017-11-26 01:54:11'),
(33, 13, '20', '2017-11-26 01:54:11'),
(34, 13, '26', '2017-11-26 01:54:11'),
(35, 13, '25', '2017-11-26 01:54:11'),
(36, 13, '21', '2017-11-26 01:54:11'),
(37, 13, '20', '2017-11-26 01:54:12'),
(38, 13, '20', '2017-11-26 01:54:12'),
(39, 13, '25', '2017-11-26 01:54:12'),
(40, 13, '26', '2017-11-26 01:54:12'),
(41, 13, '21', '2017-11-26 01:54:12'),
(42, 13, '23', '2017-11-26 01:54:12'),
(43, 10, '358', '2017-11-26 01:55:29'),
(44, 10, '805', '2017-11-26 01:55:29'),
(45, 10, '689', '2017-11-26 01:55:29'),
(46, 10, '941', '2017-11-26 01:55:29'),
(47, 10, '593', '2017-11-26 01:55:29'),
(48, 10, '348', '2017-11-26 01:55:29'),
(49, 10, '820', '2017-11-26 01:55:29'),
(50, 10, '708', '2017-11-26 01:55:29'),
(51, 10, '816', '2017-11-26 01:55:29'),
(52, 10, '763', '2017-11-26 01:55:29'),
(53, 10, '568', '2017-11-26 01:55:29'),
(54, 10, '718', '2017-11-26 01:55:29'),
(55, 10, '999', '2017-11-26 01:55:29'),
(56, 10, '548', '2017-11-26 01:55:29'),
(57, 10, '410', '2017-11-26 01:55:29'),
(58, 10, '930', '2017-11-26 01:55:29'),
(59, 10, '147', '2017-11-26 01:55:29'),
(60, 10, '304', '2017-11-26 01:55:29'),
(61, 10, '864', '2017-11-26 01:55:29'),
(62, 10, '940', '2017-11-26 01:55:29');

--
-- Triggers `meritev`
--
DELIMITER $$
CREATE TRIGGER `data_BINS` BEFORE INSERT ON `meritev` FOR EACH ROW BEGIN
    SET NEW.CREATED = NOW();
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `naprava`
--

CREATE TABLE `naprava` (
  `ID` int(11) NOT NULL,
  `NAZIV` varchar(200) CHARACTER SET latin1 NOT NULL,
  `NASLOV` varchar(100) CHARACTER SET latin1 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `naprava`
--

INSERT INTO `naprava` (`ID`, `NAZIV`, `NASLOV`) VALUES
(1, 'N1', 'https://n1-wlan0.srm.bajtahack.si:14100/'),
(2, 'N2', 'https://n2-wlan0.srm.bajtahack.si:14200/'),
(3, 'N3', 'https://n3-wlan0.srm.bajtahack.si:14300/');

-- --------------------------------------------------------

--
-- Table structure for table `pomen`
--

CREATE TABLE `pomen` (
  `ID` int(11) NOT NULL,
  `SENZOR_ID` int(11) NOT NULL,
  `POMEN` varchar(200) NOT NULL,
  `MIN_VREDNOST` varchar(100) CHARACTER SET latin1 NOT NULL,
  `MAX_VREDNOST` varchar(100) CHARACTER SET latin1 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pomen`
--

INSERT INTO `pomen` (`ID`, `SENZOR_ID`, `POMEN`, `MIN_VREDNOST`, `MAX_VREDNOST`) VALUES
(1, 10, 'Luč je prižgana', '100', '1000');

-- --------------------------------------------------------

--
-- Table structure for table `senzor`
--

CREATE TABLE `senzor` (
  `ID` int(11) NOT NULL,
  `NAZIV` varchar(300) CHARACTER SET latin1 NOT NULL,
  `NAPRAVA_ID` int(11) NOT NULL,
  `TIP_ID` int(11) NOT NULL,
  `NASLOV` varchar(300) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `senzor`
--

INSERT INTO `senzor` (`ID`, `NAZIV`, `NAPRAVA_ID`, `TIP_ID`, `NASLOV`) VALUES
(9, 'Temperatura', 3, 1, '/phy/i2c/1/slaves/64'),
(10, 'Svetlost', 3, 3, '/phy/i2c/1/slaves/41'),
(13, 'Vlažnost', 3, 2, '/phy/i2c/1/slaves/64');

-- --------------------------------------------------------

--
-- Table structure for table `tip`
--

CREATE TABLE `tip` (
  `ID` int(11) NOT NULL,
  `NAZIV` varchar(200) CHARACTER SET latin1 NOT NULL,
  `EM` varchar(10) CHARACTER SET latin1 DEFAULT NULL,
  `PROTOKOL` int(11) DEFAULT NULL COMMENT '0...navaden GPIO; 1... I2C',
  `DATA` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `tip`
--

INSERT INTO `tip` (`ID`, `NAZIV`, `EM`, `PROTOKOL`, `DATA`) VALUES
(1, 'Temperatura', '°C', 1, '"00"'),
(2, 'Vlažnost', '%', 1, '"01"'),
(3, 'Svetlost', 'lux', 1, '"B4"'),
(4, 'Motion', '', 0, NULL),
(5, 'CO', '%', 0, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `meritev`
--
ALTER TABLE `meritev`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `senzor_fk` (`SENZOR_ID`);

--
-- Indexes for table `naprava`
--
ALTER TABLE `naprava`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `pomen`
--
ALTER TABLE `pomen`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `senzor_fk1` (`SENZOR_ID`);

--
-- Indexes for table `senzor`
--
ALTER TABLE `senzor`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `naprava_fk` (`NAPRAVA_ID`),
  ADD KEY `tip_fk1` (`TIP_ID`);

--
-- Indexes for table `tip`
--
ALTER TABLE `tip`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `meritev`
--
ALTER TABLE `meritev`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=63;
--
-- AUTO_INCREMENT for table `naprava`
--
ALTER TABLE `naprava`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `pomen`
--
ALTER TABLE `pomen`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `senzor`
--
ALTER TABLE `senzor`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;
--
-- AUTO_INCREMENT for table `tip`
--
ALTER TABLE `tip`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `meritev`
--
ALTER TABLE `meritev`
  ADD CONSTRAINT `senzor_fk` FOREIGN KEY (`SENZOR_ID`) REFERENCES `senzor` (`ID`);

--
-- Constraints for table `pomen`
--
ALTER TABLE `pomen`
  ADD CONSTRAINT `senzor_fk1` FOREIGN KEY (`SENZOR_ID`) REFERENCES `senzor` (`ID`);

--
-- Constraints for table `senzor`
--
ALTER TABLE `senzor`
  ADD CONSTRAINT `naprava_fk` FOREIGN KEY (`NAPRAVA_ID`) REFERENCES `naprava` (`ID`),
  ADD CONSTRAINT `tip_fk1` FOREIGN KEY (`TIP_ID`) REFERENCES `tip` (`ID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
