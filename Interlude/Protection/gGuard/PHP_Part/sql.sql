

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

CREATE TABLE `nprotect` (
  `index` int(11) NOT NULL,
  `id` text NOT NULL,
  `ip` text NOT NULL,
  `key` text NOT NULL,
  PRIMARY KEY  (`index`),
  FULLTEXT KEY `key` (`key`)
) ENGINE=MyISAM DEFAULT CHARSET=cp1251;

--
-- Dumping data for table `nprotect`
--

INSERT INTO `nprotect` VALUES(2, 'vados', '*', 'FFCCAA');
INSERT INTO `nprotect` VALUES(3, 'crach', '*', 'AACCAA');
INSERT INTO `nprotect` VALUES(6, 'thefost', '*', 'FFEECC');
INSERT INTO `nprotect` VALUES(7, 'minibuka', '*', 'ABCABC');
