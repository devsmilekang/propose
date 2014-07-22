CREATE TABLE `memberItem` (
  `starte_date` date NOT NULL,
  `end_date` date NOT NULL,
  `use_flag` varchar(1) NOT NULL,
  `member_phone` varchar(20) NOT NULL,
  `item_id` int(11) NOT NULL,
  `membe_item_id` int(11) NOT NULL,
  PRIMARY KEY (`membe_item_id`),
  KEY `fk_memberItem_member1_idx` (`member_phone`),
  KEY `fk_memberItem_item1_idx` (`item_id`),
  CONSTRAINT `fk_memberItem_member1` FOREIGN KEY (`member_phone`) REFERENCES `member` (`member_phone`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_memberItem_item1` FOREIGN KEY (`item_id`) REFERENCES `item` (`item_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=euckr;
