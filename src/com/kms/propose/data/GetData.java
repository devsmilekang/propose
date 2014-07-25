package com.kms.propose.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.kms.api.db.KmsDbApi;

public class GetData extends KmsDbApi{
	
	private JSONObject obj;
	private String myPhoneNumber;
	private String deviceNumber;
	private String pushId;
	private String loveMemberPhone;
	private String id;
	private String select;
	private String loveMsg;
	private String itemId;
	private String memberSession;
	private String point;
	private String flag;

	public GetData(){
		if(!super.MysqlConn("121.254.179.10", "3306", "propose", "kms", "kms1234")){
			this.obj.put("eroor", "1");
			this.obj.put("msg","DB접속에 실패하였습니다.");
		}
		this.obj = new JSONObject();
	}
	
	public String dataSelect(){
		try{
			if("main".equals(select)){		
				if("".equals(myPhoneNumber)){
					obj.put("error","1");
					obj.put("msg", "내 전화번호가 없습니다.");
				}
				else{
					getLoveCount();		//내가 여지껏 받은 하트 개순
					getMyPoint();		//내 포인트 가져오기
					getHeartList();		//하트 리스트 가져오기		
					getMainHeartCount();
					obj.put("error","0");
				}
			}
			else if("sendHeart".equals(select)){
				if("".equals(myPhoneNumber)){
					obj.put("error","1");
					obj.put("msg", "내 전화번호가 없습니다.");
				}
				else{
					getMyPoint();	//내 포인트 가져오기
					getHeartList("0"); //내가 하트를 보낸 사람
					obj.put("error","0");
				}
			}
			else if("checkMember".equals(select)) {
				if("".equals(deviceNumber) || "".equals(myPhoneNumber) ||  "".equals(pushId) ){
					obj.put("error","1");
					obj.put("msg", "전화번호 및 장비, PushId가 부족합니다.");
				}
				else{
					if(1 == getCheckMember()){
						updateMemberPushId();
						obj.put("error","0");
						//insertLog();
					}
				}
			}
			else if("insertMember".equals(select)){
				if("".equals(id)){
					obj.put("error","1");
					obj.put("msg", "ID를 넣어주세요.");
				}
				else if("".equals(deviceNumber) || "".equals(myPhoneNumber) || "".equals(pushId)) {
					obj.put("error","1");
					obj.put("msg", "전화번호 및 장비정보가 부족합니다.");
				}
				else{
					if(1 == getCheckMemberId()){
						obj.put("error","1");
						obj.put("insertFlag", "0");
						obj.put("msg", "이미 아이디가 있습니다.");
					}
					else{
						insertMember();
						obj.put("error","0");
					}
				}			
			}
			else if("heartCount".equals(select)){
				if("".equals(myPhoneNumber)){
					errorMsg("전화번호가 없습니다.");
				}
				else{
					getHeartCount();
					getHeartTime();
					getMyItemCount();
					obj.put("error","0");
				}
			}
			else if("insertLoevMember".equals(select)){
				if("".equals(myPhoneNumber) || "".equals(loveMemberPhone) || "".equals(flag)){
					errorMsg("전화번호 및 상대전화번호가 없습니다.");
				}
				else{
					if(getHeartCount() < 1 && "free".equals(flag)){
						obj.put("insertFlag","0");
						errorMsg("하트가 없습니다.");
					}
					else{
						if("charged".equals(flag) || "free".equals(flag)){
							if("".equals(loveMsg)){
								if(insertLoevMember()){
									obj.put("error","0");
								}								
							}
							else{
								if(checkItemCnt("2") > 0){
									if(insertLoevMember()){
										obj.put("error","0");
									}									
								}
								else{
									errorMsg("아이템 부족합니다.");
								}
							}
						}
						else{
							errorMsg("플래그 값이 없습니다.");
						}
					}
				}
			}
			else if("cancelLove".equals(select)){
				if("".equals(myPhoneNumber) || "".equals(loveMemberPhone)){
					errorMsg("전화번호 및 상대전화번호가 없습니다.");
				}
				else{
					if(cancelLove() < 1){
						errorMsg("취소된 내역이 없습니다.");
					}
				}
			}
			else if("appDelete".equals(select)){
				if("".equals(myPhoneNumber)){
					errorMsg("전화번가 없습니다.");
				}
				else{
					appDelete();
					obj.put("error","0");
				}
			}
			else if("getMyItem".equals(select)){
				if("".equals(myPhoneNumber)){
					errorMsg("전화번가 없습니다.");
				}
				else{
					//getMyItemList();
					getHeartCount();
					getMyItemCount();
					getMyPoint();
					obj.put("error","0");
				}
			}
			else if("buyItem".equals(select)){		//아이템 구매
				if("".equals(myPhoneNumber) || "".equals(this.itemId) || "".equals(memberSession)){
					errorMsg("전화번호 또는 선택한 아이템 또는 세션값이 없습니다.");
				}
				else{
					int price = getItemPrice(); 
					if( price > 0){
						int myPoint = getMyPointInt();
						if(price > myPoint){
							errorMsg("포인트가 부족합니다.");
						}
						else if(!checkSession()){
							errorMsg("세션값이 맞지 않습니다.");
						}
						else{
							buyItem();
							obj.put("error","0");
						}						
					}
					else{
						errorMsg("아이템 정보가 잘못되었습니다.");
					}
				}
			}
			else if("buyPoint".equals(select)){		//아이템 구매
				if("".equals(myPhoneNumber) || "".equals(memberSession)){
					errorMsg("전화번호 또는 세션값이 없습니다.");
				}
				else{
					if(!checkSession()){
						errorMsg("세션값이 맞지 않습니다.");
					}
					else{
						if("".equals(point)){
							errorMsg("포인트 값 부족");
						}
						else{
							buyPoint();
							obj.put("error","0");
						}						
					}			
				}
			}
			else if("getSession".equals(select)){
				if("".equals(myPhoneNumber)){
					errorMsg("전화번호가 없습니다.");
				}
				else{
					getSession();
					obj.put("error","0");
				}				
			}			
			else{
				errorMsg("요청된 값이 잘못되었습니다.");
			}
			if("0".equals(obj.get("error"))){
				insertLog();
			}
			else{
				select = select.concat("(").concat(obj.getString("msg")).concat(")");
				insertLog();
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			obj.put("error", "1");
			obj.put("msg", e.toString());
			select = select.concat("(").concat(obj.getString("msg")).concat(")");
			insertLog();
		}
		return obj.toString();
	}
	
	public void buyPoint(){
		String session = "";
		List list = new ArrayList();
		StringBuffer query = new StringBuffer();
		query.append(" update propose.member set member_point = member_point+").append(point);
		query.append(" where member_phone='").append(myPhoneNumber).append("' ");
		int cnt = super.executeUpdate(query.toString());
		obj.put("updateFlag", cnt);
		getUpdateSession();		
	}
	
	public boolean checkSession(){
		String session = "";
		List list = new ArrayList();
		StringBuffer query = new StringBuffer();
		query.append(" select memberSession from propose.member ");
		query.append(" where member_phone='").append(myPhoneNumber).append("' ");
		query.append(" and memberSession='").append(memberSession).append("' ");
		list = super.executeQuery(query.toString());
		if(list.size() > 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void getSession(){
		getUpdateSession();
		String session = "";
		List list = new ArrayList();
		StringBuffer query = new StringBuffer();
		query.append(" select memberSession from propose.member ");
		query.append(" where member_phone='").append(myPhoneNumber).append("' ");
		list = super.executeQuery(query.toString());
		for(int k=0; k<list.size(); k++){
			Map map = (Map)list.get(k);
			session = String.valueOf(map.get("MEMBERSESSION"));
		}
		obj.put("se", encodingSession(session));
		//obj.put("itemInsert", cnt);
	}
	
	public String encodingSession(String session){
		String encodingSession = "";
		for(int k=0; k<session.length(); k++){
			int key = 3-(k%3);
			char c = session.charAt(k);
			int i = c+key;
			c = (char)i;
			encodingSession += c; 
		}
		return encodingSession;
	}
	
	public String decodingSession(String session){
		String decodingSession = "";
		for(int k=0; k<session.length(); k++){
			int key = (k%3)-3;
			char c = session.charAt(k);
			int i = c+key;
			c = (char)i;
			decodingSession += c; 
		}
		return decodingSession;
	}
	
	public void getUpdateSession(){
		Random random = new Random();
		StringBuffer query = new StringBuffer();
		query.append(" update propose.member set memberSession='").append(createSession()).append("' ");
		query.append(" where member_phone='").append(myPhoneNumber).append("' ");
		int cnt = super.executeUpdate(query.toString());
		obj.put("changeSession", cnt);
	}
	
	public String createSession(){
		Random random = new Random();
		String session = "";
		for(int k=0; k<60; k++){
			int ran = random.nextInt(10);
			session = session.concat(String.valueOf(ran));
		}
		return session;		
	}
	
	public void buyItem(){
		StringBuffer query = new StringBuffer();
		query.append(" update propose.member k set k.member_point = k.member_point-(select item_price from propose.item where item_id='").append(itemId).append("') ");
		query.append(" where k.member_phone='").append(myPhoneNumber).append("' ");
		int cnt = super.executeUpdate(query.toString());
		obj.put("pointminus", cnt);
		query = new StringBuffer("");
		query.append(" INSERT INTO `propose`.`memberItem` ( `use_flag`, `member_phone`, `item_id`, `start_date`) VALUES ");
		query.append(" ('0','").append(myPhoneNumber).append("','").append(itemId).append("', now()) ");
		cnt = super.executeUpdate(query.toString());
		obj.put("itemInsert", cnt);
		getUpdateSession();
	}
	
	public int getItemPrice(){
		List list = new ArrayList();
		int price =-1;
		StringBuffer query = new StringBuffer();
		query.append(" select item_price from propose.item ");
		query.append(" where item_id='").append(itemId).append("' ");
		list = super.executeQuery(query.toString());
		for(int k=0;k<list.size();k++){
			Map map = (Map)list.get(k);
			price = Integer.parseInt(String.valueOf(map.get("ITEM_PRICE")));
		}
		return price;		
	}
	
	public int cancelLove(){	
		int cnt =0;
		StringBuffer query = new StringBuffer();
		query = new StringBuffer("");
		query.append(" update loveMember set heart_flag='3' ");
		query.append(" where member_phone='").append(myPhoneNumber).append("' ");
		query.append(" and member_love_phone='").append(loveMemberPhone).append("' ");
		query.append(" and heart_flag not in('2','3') ");
		cnt = super.executeUpdate(query.toString());
		obj.put("updateFlag", cnt);
		return cnt;
	}
	
	public int appDelete(){	
		int cnt =0;
		StringBuffer query = new StringBuffer();
		query = new StringBuffer("");
		query.append(" update member set appCheck='0' ");
		query.append(" where member_phone='").append(myPhoneNumber).append("' ");
		cnt = super.executeUpdate(query.toString());
		obj.put("updateFlag", cnt);
		return cnt;
	}

	
	public void getHeartTime(){
		List list = new ArrayList();
		StringBuffer query = new StringBuffer();
		query = new StringBuffer("");
		query.append(" select k.member_love_phone, DATE_FORMAT(k.heart_send_time,'%Y-%m-%d %H:%i:%S') as heart_send_time from loveMember k ");
		query.append(" where k.heart_flag not in('2','3') ");
		query.append(" and k.member_phone='").append(myPhoneNumber).append("'");
		list = super.executeQuery(query.toString());	
		JSONArray jsonArr = new JSONArray();
		for(int k=0; k<list.size(); k++){
			Map map = (Map)list.get(k);
			JSONObject data = new JSONObject();
			data.put("member_love_phone", map.get("MEMBER_LOVE_PHONE"));
			data.put("heart_send_time", map.get("HEART_SEND_TIME"));
			jsonArr.add(data);
		}
		obj.put("heartList", jsonArr);
	}
	
	public void getMyItemList(){
		List list = new ArrayList();
		StringBuffer query = new StringBuffer();
		query = new StringBuffer("");
		query.append(" select m.item_name, k.item_id, k.member_phone, DATE_FORMAT(k.start_date,'%Y-%m-%d %H:%i:%S') as start_date ");
		query.append(" from propose.memberItem k, propose.item m where m.item_id = k.item_id ");
		query.append(" and k.member_phone='").append(myPhoneNumber).append("' and k.use_flag='0' ");
		list = super.executeQuery(query.toString());	
		JSONArray jsonArr = new JSONArray();
		for(int k=0; k<list.size(); k++){
			Map map = (Map)list.get(k);
			JSONObject data = new JSONObject();
			data.put("itemId", map.get("ITEM_ID"));
			data.put("itemName", map.get("ITEM_NAME"));
			data.put("memberPhone", map.get("MEMBER_PHONE"));
			data.put("startDate", map.get("START_DATE"));
			jsonArr.add(data);
		}
		obj.put("myItemList", jsonArr);
	}
	
	public void getMyItemCount(){
		List list = new ArrayList();
		StringBuffer query = new StringBuffer();
		query = new StringBuffer("");
		query.append(" select item_id, COUNT(*) as cnt from propose.memberItem ");
		query.append(" where member_phone='").append(myPhoneNumber).append("' ");
		query.append(" and use_flag='0' ");
		query.append(" group by item_id ");
		list = super.executeQuery(query.toString());	
		JSONArray jsonArr = new JSONArray();
		for(int k=0; k<list.size(); k++){
			Map map = (Map)list.get(k);
			JSONObject data = new JSONObject();
			data.put("itemId", map.get("ITEM_ID"));
			data.put("count", map.get("CNT"));			
			jsonArr.add(data);
		}
		obj.put("myItemList", jsonArr);
	}


	public int getHeartCount() {
		StringBuffer query = new StringBuffer();
		List list = new ArrayList();
		query.append(" select k.heartCount from member k ");
		query.append(" where k.member_phone='").append(myPhoneNumber).append("'");
		list = super.executeQuery(query.toString());
		int cnt =0;
		for(int k=0; k<list.size(); k++){
			Map map = (Map)list.get(k);
			obj.put("heartCount", map.get("HEARTCOUNT"));
			cnt = Integer.parseInt(String.valueOf(map.get("HEARTCOUNT")));
		}
		return cnt;
	}
	
	public int getMainHeartCount(){
		StringBuffer query = new StringBuffer();
		List list = new ArrayList();
		query.append(" select ifnull(k.heartCount,0)+ifnull(m.itemHeartCount,0) as heartCount from propose.member k  ");
		query.append(" left outer join ");
		query.append(" (select member_phone, COUNT(*) as itemHeartCount from propose.memberItem m where use_flag='0' and item_id in('0','1') ");
		query.append(" and member_phone='").append(myPhoneNumber).append("') m ");
		query.append(" on k.member_phone = m.member_phone ");
		query.append(" where k.member_phone='").append(myPhoneNumber).append("'");
		
		list = super.executeQuery(query.toString());
		int cnt =0;
		for(int k=0; k<list.size(); k++){
			Map map = (Map)list.get(k);
			obj.put("heartCount", map.get("HEARTCOUNT"));
			cnt = Integer.parseInt(String.valueOf(map.get("HEARTCOUNT")));
		}
		return cnt;
	}
	
	public int getCheckMemberId(){
		List list = new ArrayList();
		StringBuffer query = new StringBuffer();
		query.append(" SELECT id FROM member ");	
		query.append(" WHERE id='").append(id).append("' ");
		query.append(" or member_phone='").append(myPhoneNumber).append("'");
		list = super.executeQuery(query.toString());
		obj.put("checkMemberId", list.size());
		return list.size();
	}
	
	public void getMyPoint(){	//내 포인트 가져오기
		List list = new ArrayList();
		StringBuffer query = new StringBuffer();
		query.append("SELECT member_point AS POINT FROM propose.member ");
		query.append("WHERE member_phone ='").append(myPhoneNumber).append("'");
		list = super.executeQuery(query.toString());	
		for(int k=0; k<list.size(); k++){
			Map map = (Map)list.get(k);
			obj.put("myPoint", map.get("POINT"));
		}
	}
	
	public int getMyPointInt(){	//내 포인트 가져오기
		int point = 0;
		List list = new ArrayList();
		StringBuffer query = new StringBuffer();
		query.append("SELECT member_point AS POINT FROM propose.member ");
		query.append("WHERE member_phone ='").append(myPhoneNumber).append("'");
		list = super.executeQuery(query.toString());	
		for(int k=0; k<list.size(); k++){
			Map map = (Map)list.get(k);
			point = Integer.parseInt(String.valueOf(map.get("POINT")));
		}
		return point;
	}
	
	public void getLoveCount(){	
		List list = new ArrayList();
		StringBuffer query = new StringBuffer();
		query.append("SELECT COUNT(*) AS CNT FROM propose.loveMember ");	
		query.append("WHERE member_love_phone='").append(myPhoneNumber).append("'");
		query.append(" and  heart_flag not in ('3')");
		list = super.executeQuery(query.toString());		
		for(int k=0; k<list.size(); k++){
			Map map = (Map)list.get(k);
			obj.put("meLoveCount", map.get("CNT"));
		}
	}
	
	public void getHeartList(){	
		getHeartList("");
	}

	public void getHeartList(String sendflag){	
		List list = new ArrayList();
		StringBuffer query = new StringBuffer();
		query = new StringBuffer("");			//하트 정보 가져오기 sendflag -> 0 : 내가 보낸거, 1 :내가 받은거
		query.append(" select case when s.heart_flag='2' then case when s.sendflag='0' then '3' else '4' end ");
		query.append(" else s.sendflag end as sendflag, s.member_love_phone,s.member_phone,s.heart_send_time, s.heart_flag ");
		query.append(" , s.loveMsg, case when s.loveMsg is null then 0 else 1 end as msgFlag ");		
		query.append(" from ( select case when m.member_phone is not null then 2 ");
		query.append(" when k.member_phone = '").append(myPhoneNumber).append("' then 0 else 1 end as sendflag, ");
		query.append(" k.member_love_phone, k.member_phone, DATE_FORMAT(k.heart_send_time,'%Y-%m-%d %H:%i:%S') as heart_send_time , k.heart_flag,k.loveMsg ");
		query.append(" from loveMember k left outer join loveMember m ");
		query.append(" on k.member_phone = m.member_love_phone and k.member_love_phone = m.member_phone ");
		query.append(" and k.heart_flag not in('2','3') and m.heart_flag not in('2','3') ");
		query.append(" where (k.member_phone ='").append(myPhoneNumber).append("' or ");
		query.append(" k.member_love_phone = '").append(myPhoneNumber).append("') ");
		query.append(" ) s ");
		if("".equals(sendflag)){
			query.append(" where (s.sendflag =2 and s.member_phone='").append(myPhoneNumber).append("') ");
			query.append(" or (s.sendflag != 2) and s.heart_flag not in('3')");
		}
		else{
			query.append(" where s.member_phone='").append(myPhoneNumber).append("' ");
			query.append(" and s.heart_flag not in ('2','3') ");
		}
		query.append(" order by s.heart_send_time desc ");
		
		list = super.executeQuery(query.toString());	
		JSONArray jsonArr = new JSONArray();
		for(int k=0; k<list.size(); k++){
			Map map = (Map)list.get(k);
			JSONObject data = new JSONObject();
			data.put("sendflag", map.get("SENDFLAG"));
			data.put("member_love_phone", map.get("MEMBER_LOVE_PHONE"));
			data.put("member_phone", map.get("MEMBER_PHONE"));
			data.put("heart_send_time", map.get("HEART_SEND_TIME"));
			data.put("msgFlag", map.get("MSGFLAG"));
			data.put("loveMsg", map.get("LOVEMSG"));
			jsonArr.add(data);
		}
		obj.put("heartList", jsonArr);
	}
	
	public int getCheckMember(){
		List list = new ArrayList();
		StringBuffer query = new StringBuffer();
		query.append(" SELECT member_phone FROM member ");	
		query.append(" WHERE member_phone='").append(myPhoneNumber).append("' ");
		query.append(" and deviceNumber='").append(deviceNumber).append("' ");
		//obj.put("query", query.toString());
		list = super.executeQuery(query.toString());		
		obj.put("checkMember", list.size());
		return list.size();
	}
	
	public void updateMemberPushId(){
		StringBuffer query = new StringBuffer();
		query.append(" UPDATE member ");	
		query.append(" SET pushId = '").append(pushId).append("', appCheck='1' ");
		query.append(" WHERE member_phone='").append(myPhoneNumber).append("' ");
		query.append(" and deviceNumber='").append(deviceNumber).append("' ");
		super.executeUpdate(query.toString());
	}
	
	public void insertLog(){
		StringBuffer query = new StringBuffer();
		query.append(" INSERT INTO loginLog (member_phone, loginTime, activity) ");	
		query.append(" values ('").append(myPhoneNumber).append("', now(),'").append(select).append("' )");
		super.executeUpdate(query.toString());
	}
	
	public void insertMember() {
		StringBuffer query = new StringBuffer();
		query.append(" INSERT INTO member (member_phone, deviceNumber,pushId, id, member_point, chargedCheck) values ( ");
		query.append(" '").append(myPhoneNumber).append("'");
		query.append(",'").append(deviceNumber).append("'");
		query.append(",'").append(pushId).append("'");
		query.append(",'").append(id).append(" ','0',now()) ");
		int cnt = super.executeUpdate(query.toString());
		obj.put("insertData", cnt);
		obj.put("insertFlag", "1");
	}
	
	public boolean insertLoevMember() {
		StringBuffer query = new StringBuffer();
		
		if("charged".equals(flag)){	//유료하트
			/*
			 * update propose.memberItem a, ( select k.memberItemid, k.member_phone from propose.memberItem k,
				 (select  case when m.id1_cnt > 0 then 1 else 0 end as item_id, k.member_phone from propose.member k
				 left outer join (select count(*) as id1_cnt,member_phone,item_id from propose.memberItem where item_id='1' and use_flag='0' group by member_phone,item_id) m
				 on k.member_phone = m.member_phone
				 left outer join (select count(*) as id0_cnt,member_phone,item_id from propose.memberItem where item_id='0' and use_flag='0' group by member_phone,item_id) s
				 on k.member_phone = s.member_phone
				 ) m where m.member_phone = k.member_phone
				 and m.item_id=k.item_id and k.use_flag='0' limit 0,1 ) c set a.use_flag='1'  where a.memberItemid = c.memberItemid	;
			 */
			query.append(" update propose.memberItem a, ( select k.memberItemid, k.member_phone from propose.memberItem k, ");
			query.append(" (select case when m.id1_cnt > 0 then 1 else 0 end as item_id, k.member_phone from propose.member k ");
			query.append(" left outer join ( select count(*) as id1_cnt,member_phone,item_id from propose.memberItem where item_id='1' and use_flag='0' group by member_phone,item_id) m ");
			query.append(" on k.member_phone = m.member_phone ");
			query.append(" left outer join (select count(*) as id0_cnt,member_phone,item_id from propose.memberItem where item_id='0' and use_flag='0' group by member_phone,item_id) s ");
			query.append(" on k.member_phone = s.member_phone ) m where m.member_phone = k.member_phone ");
			query.append(" and m.item_id=k.item_id and k.use_flag='0' ");
			query.append(" and k.member_phone='").append(myPhoneNumber).append("' limit 0,1 ) c set a.use_flag='1', a.end_date=now()  where a.memberItemid = c.memberItemid ");
		}
		else if("free".equals(flag)){ //무료하트
			query.append(" update member set heartCount=heartCount-1 ");		//하트감소
			query.append(" where member_phone='").append(myPhoneNumber).append("'");
		}
		else{
			errorMsg("플래그 값이 일치하지 않습니다.");
			return false;
		}
		//무료하트
		int cnt = super.executeUpdate(query.toString());
		System.out.println(query.toString());
		obj.put("updateFlag", cnt);
		if(cnt >0){
			query = new StringBuffer("");
			query.append(" INSERT INTO loveMember (member_phone, member_love_phone,loveMsg,heart_flag,heart_send_time ) values ( ");
			query.append(" '").append(myPhoneNumber).append("'");
			query.append(",'").append(loveMemberPhone).append("'");
			query.append(",'").append(loveMsg).append("'");
			query.append(",'0',now()) ");
			cnt = super.executeUpdate(query.toString());
			obj.put("insertFlag", cnt);		
			if(!"".equals(loveMsg)){		//Msg 있으면 아이템 사용
				query = new StringBuffer("");
				query.append(" update propose.memberItem a, ");
				query.append(" (select * from propose.memberItem k where k.memberItemId = ");
				query.append(" (select memberItemId from propose.memberItem where use_flag='0' ");
				query.append(" and item_id='2' ");
				query.append(" and member_phone='").append(myPhoneNumber).append("' ");
				query.append(" limit 0,1)) b ");
				query.append(" set a.use_flag='1', a.end_date=now() ");
				query.append(" where a.memberItemId = b.memberItemId ");
				super.executeUpdate(query.toString());
			}
		}
		else{
			errorMsg("아이템이 없습니다.");
			return false;
		}
		return true;
	}
	
	public int checkItemCnt(String itemId){
		List list = new ArrayList();
		StringBuffer query = new StringBuffer();
		query.append(" SELECT item_id FROM propose.memberItem ");
		query.append(" WHERE member_phone='").append(myPhoneNumber).append("' ");
		query.append(" AND item_id='").append(itemId).append("' and use_flag='0' ");
		list = super.executeQuery(query.toString());
		return list.size();
	}
	
	public void errorMsg(String msg){
		obj.put("error","1");
		obj.put("msg", msg);
	}
	
	public void setMyPhoneNumber(String myPhoneNumber) {
		this.myPhoneNumber = myPhoneNumber;
	}

	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public void setPushId(String pushId) {
		this.pushId = pushId;
	}

	public void setLoveMemberPhone(String loveMemberPhone) {
		this.loveMemberPhone = loveMemberPhone;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSelect(String select) {
		this.select = select;
	}
	public String getLoveMsg() {
		return loveMsg;
	}

	public void setLoveMsg(String loveMsg) {
		this.loveMsg = loveMsg;
	}
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getMemberSession() {
		return memberSession;
	}

	public void setMemberSession(String memberSession) {
		this.memberSession = memberSession;
	}
	
	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}
	
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}