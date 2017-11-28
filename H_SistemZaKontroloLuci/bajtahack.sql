-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Gostitelj: 127.0.0.1
-- Čas nastanka: 26. nov 2017 ob 08.04
-- Različica strežnika: 10.1.28-MariaDB
-- Različica PHP: 7.1.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Zbirka podatkov: `bajtahack`
--

-- --------------------------------------------------------

--
-- Struktura tabele `room`
--

CREATE TABLE `room` (
  `roomID` int(11) NOT NULL,
  `roomName` text NOT NULL,
  `roomDescription` text,
  `ipAddress` text,
  `connected` int(11) DEFAULT NULL,
  `configured` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Odloži podatke za tabelo `room`
--

INSERT INTO `room` (`roomID`, `roomName`, `roomDescription`, `ipAddress`, `connected`, `configured`) VALUES
(30, 'Peter', '', '192.168.0.130', 0, 0),
(34, 'test', '', '10.10.10.10', 0, NULL),
(35, 'test120', '', '192.168.0.120', 0, NULL);

-- --------------------------------------------------------

--
-- Struktura tabele `room_lights`
--

CREATE TABLE `room_lights` (
  `lightID` int(11) NOT NULL,
  `roomID` int(11) NOT NULL DEFAULT '0',
  `offsetX` double NOT NULL DEFAULT '0',
  `offsetY` double NOT NULL DEFAULT '0',
  `gpioPin` int(11) NOT NULL DEFAULT '0',
  `lightStatus` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Odloži podatke za tabelo `room_lights`
--

INSERT INTO `room_lights` (`lightID`, `roomID`, `offsetX`, `offsetY`, `gpioPin`, `lightStatus`) VALUES
(348, 35, 0.4218181818181818, 0.19859983862900152, 16, 0),
(349, 35, 0.5763636363636364, 0.19046975732818852, 24, 0),
(350, 35, 0.4218181818181818, 0.3883017356479717, 25, 0),
(351, 35, 0.58, 0.39101176274824273, 26, 0),
(352, 35, 0.5, 0.29074076003821564, 27, 0),
(357, 30, 0.4581818181818182, 0.20130986572927254, 25, 0),
(358, 30, 0.509090909090909, 0.4696025486561018, 24, 0),
(359, 30, 0.45454545454545453, 0.5799457994579946, 26, 0),
(360, 30, 0.5363636363636364, 0.23848238482384823, 27, 0);

--
-- Indeksi zavrženih tabel
--

--
-- Indeksi tabele `room`
--
ALTER TABLE `room`
  ADD PRIMARY KEY (`roomID`);

--
-- Indeksi tabele `room_lights`
--
ALTER TABLE `room_lights`
  ADD PRIMARY KEY (`lightID`,`roomID`),
  ADD KEY `FK_room_lights_room` (`roomID`);

--
-- AUTO_INCREMENT zavrženih tabel
--

--
-- AUTO_INCREMENT tabele `room`
--
ALTER TABLE `room`
  MODIFY `roomID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;

--
-- AUTO_INCREMENT tabele `room_lights`
--
ALTER TABLE `room_lights`
  MODIFY `lightID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=361;

--
-- Omejitve tabel za povzetek stanja
--

--
-- Omejitve za tabelo `room_lights`
--
ALTER TABLE `room_lights`
  ADD CONSTRAINT `FK_room_lights_room` FOREIGN KEY (`roomID`) REFERENCES `room` (`roomID`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
