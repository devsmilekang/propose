CREATE TABLE `member` (
  `member_phone` varchar(20) NOT NULL,
  `member_point` int(11) DEFAULT NULL,
  `deviceNumber` varchar(100) NOT NULL,
  `id` varchar(100) NOT NULL,
  `pushId` varchar(200) DEFAULT NULL,
  `heartCount` int(11) DEFAULT '1',
  `appCheck` int(11) DEFAULT '1',
  `memberSession` varchar(100) DEFAULT NULL,
  `chargedCheck` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`member_phone`)
) ENGINE=InnoDB DEFAULT CHARSET=euckr;
