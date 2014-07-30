CREATE TABLE `memberItem` (
  `memberItemid` int(11) NOT NULL AUTO_INCREMENT,
  `use_flag` varchar(1) NOT NULL,
  `member_phone` varchar(20) NOT NULL,
  `item_id` int(11) NOT NULL,
  `start_date` timestamp NULL DEFAULT NULL,
  `end_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`memberItemid`),
  KEY `fk_memberItem_member1_idx` (`member_phone`),
  KEY `fk_memberItem_item1_idx` (`item_id`),
  CONSTRAINT `fk_memberItem_item1` FOREIGN KEY (`item_id`) REFERENCES `item` (`item_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_memberItem_member1` FOREIGN KEY (`member_phone`) REFERENCES `member` (`member_phone`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=euckr;
