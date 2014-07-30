CREATE TABLE `loginLog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member_phone` varchar(100) DEFAULT NULL,
  `loginTime` timestamp NULL DEFAULT NULL,
  `activity` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=808 DEFAULT CHARSET=euckr;
