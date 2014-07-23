package com.kms.propose.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
					getHeartCount();	//내하트개수 가져오기
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
						insertLog();
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
						obj.put("error","0");
						insertMember();
					}
				}			
			}
			else if("heartCount".equals(select)){
				if("".equals(myPhoneNumber)){
					obj.put("error","1");
					obj.put("msg", "전화번호가 없습니다.");
				}
				else{
					getHeartCount();
					getHeartTime();
				}
			}
			else if("insertLoevMember".equals(select)){
				if("".equals(myPhoneNumber) || "".equals(loveMemberPhone)){
					obj.put("error","1");
					obj.put("msg", "전화번호 및 상대전화번호가 없습니다.");
				}
				else{
					if(getHeartCount() < 1){
						obj.put("error","1");
						obj.put("insertFlag","0");
						obj.put("msg","하트가 없습니다.");
					}
					else{
						if("".equals(loveMsg)){
							insertLoevMember();
						}
						else{
							if(checkItem("2") > 0){
								insertLoevMember();
							}
							else{
								errorMsg("아이템이 부족합니다.");
							}
						}
						
					}
				}
			}
			else if("cancelLove".equals(select)){
				if("".equals(myPhoneNumber) || "".equals(loveMemberPhone)){
					obj.put("error","1");
					obj.put("msg", "전화번호 및 상대전화번호가 없습니다.");
				}
				else{
					if(cancelLove() < 1){
						obj.put("error","1");
						obj.put("msg", "취소된 내역이 없습니다.");
					}
				}
			}
			else if("appDelete".equals(select)){
				if("".equals(myPhoneNumber)){
					obj.put("error","1");
					obj.put("msg", "전화번가 없습니다.");
				}
				else{
					appDelete();
				}
			}
			else if("getMyItem".equals(select)){
				if("".equals(myPhoneNumber)){
					obj.put("error","1");
					obj.put("msg", "전화번가 없습니다.");
				}
				else{
					getMyItemList();
				}
			}
			else{
				obj.put("errer","1");
				obj.put("msg","요청된 값이 잘못되었습니다.");
			}
		}
		catch(Exception e){
			e.printStackTrace();
			obj.put("error", "1");
		}
		return obj.toString();
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
		query.append(" else s.sendflag end as sendflag, s.member_love_phone,s.member_phone,s.heart_send_time, s.heart_flag from ");
		query.append(" ( select case when m.member_phone is not null then 2 ");
		query.append(" when k.member_phone = '").append(myPhoneNumber).append("' then 0 else 1 end as sendflag, ");
		query.append(" k.member_love_phone, k.member_phone, DATE_FORMAT(k.heart_send_time,'%Y-%m-%d %H:%i:%S') as heart_send_time , k.heart_flag ");
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
		query.append(" INSERT INTO loginLog (member_phone, loginTime) ");	
		query.append(" values ('").append(myPhoneNumber).append("', now() )");
		super.executeUpdate(query.toString());
	}
	
	public void insertMember() {
		StringBuffer query = new StringBuffer();
		query.append(" INSERT INTO member (member_phone, deviceNumber,pushId, id, member_point) values ( ");
		query.append(" '").append(myPhoneNumber).append("'");
		query.append(",'").append(deviceNumber).append("'");
		query.append(",'").append(pushId).append("'");
		query.append(",'").append(id).append(" ','0') ");
		int cnt = super.executeUpdate(query.toString());
		obj.put("insertData", cnt);
		obj.put("insertFlag", "1");
	}
	
	public void insertLoevMember() {
		StringBuffer query = new StringBuffer();
		query.append(" INSERT INTO loveMember (member_phone, member_love_phone,loveMsg,heart_flag,heart_send_time ) values ( ");
		query.append(" '").append(myPhoneNumber).append("'");
		query.append(",'").append(loveMemberPhone).append("'");
		query.append(",'").append(loveMsg).append("'");
		query.append(",'0',now()) ");
		int cnt = super.executeUpdate(query.toString());
		obj.put("insertFlag", cnt);
		query = new StringBuffer("");
		query.append(" update member set heartCount=heartCount-1 ");
		query.append(" where member_phone='").append(myPhoneNumber).append("'");
		cnt = super.executeUpdate(query.toString());
		obj.put("updateFlag", cnt);
		if(!"".equals(loveMsg)){
			query = new StringBuffer("");
			//qeury.append("update ")
			//query.append("update )
		}
	}
	
	public int checkItem(String itemId){
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
}