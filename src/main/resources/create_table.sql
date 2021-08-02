
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

--
-- Struktura tabulky `results`
--

CREATE TABLE `results` (
  `id` int(11) NOT NULL,
  `repository_name` varchar(50) COLLATE utf8_czech_ci NOT NULL,
  `incompatible` int(11) NOT NULL,
  `compatible` int(11) NOT NULL,
  `redundant` int(11) NOT NULL,
  `must_remove` int(11) NOT NULL,
  `c1` int(11) NOT NULL,
  `c2` int(11) NOT NULL,
  `c3` int(11) NOT NULL,
  `m1` int(11) NOT NULL,
  `m2` int(11) NOT NULL,
  `f1` int(11) NOT NULL,
  `f2` int(11) NOT NULL,
  `modd` int(11) NOT NULL,
  `mm1` int(11) NOT NULL,
  `f7` int(11) NOT NULL,
  `score` int(11) NOT NULL,
  `json_graph` mediumblob NOT NULL,
  `html_report` mediumblob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;


ALTER TABLE `results`
  ADD PRIMARY KEY (`id`);


ALTER TABLE `results`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=80;
COMMIT;

