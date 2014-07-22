CREATE TABLE `loveMember` (
  `member_phone` varchar(20) NOT NULL,
  `member_love_phone` varchar(20) NOT NULL,
  `heart_flag` varchar(1) NOT NULL DEFAULT '',
  `heart_send_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `cancel_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`member_phone`,`member_love_phone`,`heart_flag`,`heart_send_time`),
  KEY `fk_loveMemeber_member_idx` (`member_phone`),
  CONSTRAINT `fk_loveMemeber_member` FOREIGN KEY (`member_phone`) REFERENCES `member` (`member_phone`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=euckr;
