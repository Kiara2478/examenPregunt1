BASE DE DATOS:
-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 27, 2025 at 03:05 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `matriculados`
--

-- --------------------------------------------------------

--
-- Table structure for table `estudiante`
--

CREATE TABLE `estudiante` (
  `codiEstdWeb` int(11) NOT NULL,
  `ndniEstdWeb` varchar(50) NOT NULL,
  `appaEstdWeb` varchar(50) NOT NULL,
  `apmaEstdWeb` varchar(50) NOT NULL,
  `nombEstdWeb` varchar(50) NOT NULL,
  `fechNaciEstdWeb` date NOT NULL,
  `logiEstd` varchar(100) NOT NULL,
  `passEstd` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `estudiante`
--

INSERT INTO `estudiante` (`codiEstdWeb`, `ndniEstdWeb`, `appaEstdWeb`, `apmaEstdWeb`, `nombEstdWeb`, `fechNaciEstdWeb`, `logiEstd`, `passEstd`) VALUES
(1, '12345678', 'CHAVEZ', 'ROJAS', 'CARLOS', '1975-05-26', 'kike', '$2a$12$VJIPiNnE37uGnmdrC0Woz.FkLsCE/g2K5TGstyJ6th9J9TEZFMhfO'),
(2, '23456789', 'MONTALVO', 'ROMERO', 'KIARA', '2005-04-03', 'KIARA', '1234');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `estudiante`
--
ALTER TABLE `estudiante`
  ADD PRIMARY KEY (`codiEstdWeb`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `estudiante`
--
ALTER TABLE `estudiante`
  MODIFY `codiEstdWeb` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
